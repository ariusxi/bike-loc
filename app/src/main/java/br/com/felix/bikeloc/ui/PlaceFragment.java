package br.com.felix.bikeloc.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.adapter.PlaceAdapter;
import br.com.felix.bikeloc.auth.Session;
import br.com.felix.bikeloc.model.Place;

public class PlaceFragment extends Fragment {

    private RecyclerView mrecyclerview;
    private PlaceAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Session session;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private FloatingActionButton buttonRegister;
    private SwipeRefreshLayout swipeRefreshLayout;

    public PlaceFragment() {

    }

    public static PlaceFragment newInstance() {
        PlaceFragment fragment = new PlaceFragment();
        return fragment;
    }

    public void goToPlaceForm() {
        replaceFragment(
                R.id.frameLayoutMain,
                PlaceFormFragment.newInstance(),
                "PROFILEFRAGMENT",
                "PROFILE"
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mrecyclerview = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        FirebaseApp.initializeApp(getActivity());

        session = new Session(getActivity());

        getAllPlaces();
    }

    public void removePlace(String placeId){
        firebaseFirestore.collection("places").document(placeId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Local removido com sucesso", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Falha ao remover o local", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllPlaces() {
        ArrayList<Place> tempPlacesList = new ArrayList<>();
        String user = session.get("user_id");

        firebaseFirestore.collection("places").whereEqualTo("user", user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e == null) {
                    for (DocumentSnapshot ds: documentSnapshots) {
                        Place currentPlace = new Place(
                                ds.getId(),
                                ds.getString("user"),
                                ds.getString("name"),
                                ds.getString("description"),
                                ds.getDouble("latitude"),
                                ds.getDouble("longitude")
                        );

                        tempPlacesList.add(currentPlace);
                    }

                    mrecyclerview = getActivity().findViewById(R.id.recyclerView);
                    if (mrecyclerview != null) {
                        mrecyclerview.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        adapter = new PlaceAdapter(tempPlacesList);

                        adapter.setOnItemClickListener(new PlaceAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(Place place) {
                                // Movendo para a janela de informações do lugar
                                replaceFragment(
                                        R.id.frameLayoutMain,
                                        PlaceInfoFragment.newInstance(place),
                                        "PLACEINFOFRAGMENT",
                                        "PLACEINFO"
                                );
                            }

                            @Override
                            public void onUpdateClick(Place place) {
                                // Movendo para a janela de informações do lugar
                                replaceFragment(
                                        R.id.frameLayoutMain,
                                        PlaceFormFragment.newInstance(place),
                                        "PLACEFORMFRAGMENT",
                                        "PLACEFORM"
                                );
                            }

                            @Override
                            public void onDeleteClick(int position, Place place) {
                                removePlace(place.getId());
                                mrecyclerview.removeViewAt(position);
                                adapter.notifyItemRemoved(position);
                                getAllPlaces();
                                adapter.notifyItemRemoved(position);
                            }
                        });

                        mrecyclerview.setLayoutManager(mLayoutManager);
                        mrecyclerview.setAdapter(adapter);
                    }
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botão para registrar novo local
        buttonRegister = view.findViewById(R.id.buttonRegisterPlace);
        if (buttonRegister != null) {
            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToPlaceForm();
                }
            });
        }

        // Refresh swipe
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.refresh_place_items);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (swipeRefreshLayout.isRefreshing()) {
                        getAllPlaces();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }

    protected void replaceFragment(
            @IdRes int containerViewId,
            @NonNull Fragment fragment,
            @NonNull String fragmentTag,
            @Nullable String backStackStateName
    ) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_place, container, false);
    }
}
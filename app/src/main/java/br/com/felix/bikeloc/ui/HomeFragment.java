package br.com.felix.bikeloc.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.auth.Session;
import br.com.felix.bikeloc.model.Place;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Context context;
    private Session session;
    private FirebaseFirestore firebaseFirestore;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializando o firebase na listagem
        FirebaseApp.initializeApp(getActivity());
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Recuperando os dados da sessão
        session = new Session(getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String user = session.get("user_id");

        firebaseFirestore.collection("places").whereEqualTo("user", user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e == null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (DocumentSnapshot ds: documentSnapshots) {
                        Place currentPlace = new Place(
                                ds.getId(),
                                ds.getString("user"),
                                ds.getString("name"),
                                ds.getString("description"),
                                ds.getDouble("latitude"),
                                ds.getDouble("longitude")
                        );

                        LatLng currentCoordinates = new LatLng(currentPlace.getLatitude(), currentPlace.getLongitude());
                        BitmapDescriptor iconMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
                        MarkerOptions marker = new MarkerOptions().position(currentCoordinates).icon(iconMarker).title(currentPlace.getName());

                        builder.include(marker.getPosition());

                        int padding = 250;
                        LatLngBounds bounds = builder.build();

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                        // Adicionando marker ao mapa
                        mMap.addMarker(marker);
                        mMap.moveCamera(cameraUpdate);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializando o mapa da tela
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
}
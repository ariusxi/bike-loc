package br.com.felix.bikeloc.ui;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.model.Place;

public class PlaceInfoFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference db;
    private Place currentPlaceData;

    public FloatingActionButton floatingButtonEdit;
    public FloatingActionButton floatingButtonDelete;

    public TextView titlePlaceText;
    public TextView descriptionPlaceText;

    public PlaceInfoFragment() {
        // Required empty public constructor
    }

    public static PlaceInfoFragment newInstance(Place place) {
        PlaceInfoFragment fragment = new PlaceInfoFragment();

        // Adicionando informações do local na página
        Bundle args = new Bundle();
        args.putString("id", place.getId());
        args.putString("user", place.getUser());
        args.putString("name", place.getName());
        args.putString("description", place.getDescription());
        args.putDouble("latitude", place.getLatitude());
        args.putDouble("longitude", place.getLongitude());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializando os TextViews da tela
        titlePlaceText = (TextView) getActivity().findViewById(R.id.titlePlaceText);
        descriptionPlaceText = (TextView) getActivity().findViewById(R.id.descriptionPlaceText);

        if (getArguments() != null) {
            currentPlaceData = new Place(
                    getArguments().getString("id"),
                    getArguments().getString("user"),
                    getArguments().getString("name"),
                    getArguments().getString("description"),
                    getArguments().getDouble("latitude"),
                    getArguments().getDouble("longitude")
            );

            // Inicializando os valores nos textviews
            titlePlaceText.setText(currentPlaceData.getName());
            descriptionPlaceText.setText(currentPlaceData.getDescription());
        }

        // Inicializando os botões da tela
        floatingButtonEdit = getActivity().findViewById(R.id.floatingButtonEdit);
        floatingButtonDelete = getActivity().findViewById(R.id.floatingButtonDelete);

        floatingButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTopPlaceEdit();
            }
        });
        floatingButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePlace();
            }
        });

        db = FirebaseDatabase.getInstance().getReference();

        // Inicializando o mapa da tela
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapPlaceData);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (getArguments() != null){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            LatLng currentCoordinates = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
            MarkerOptions marker = new MarkerOptions().position(currentCoordinates).title(getArguments().getString("name"));

            builder.include(marker.getPosition());

            int padding = 250;
            LatLngBounds bounds = builder.build();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            // Adicionando marker ao mapa
            mMap.addMarker(marker);
            mMap.moveCamera(cameraUpdate);
        }
    }

    public void removePlace() {
        db.child("places").child(currentPlaceData.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Local removido com sucesso", Toast.LENGTH_SHORT).show();
                // Movendo para a janela de informações do lugar
                replaceFragment(
                        R.id.frameLayoutMain,
                        PlaceFormFragment.newInstance(currentPlaceData),
                        "PLACEFORMFRAGMENT",
                        "PLACEFORM"
                );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Falha ao remover o local", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void goTopPlaceEdit() {
        replaceFragment(
                R.id.frameLayoutMain,
                PlaceFormFragment.newInstance(currentPlaceData),
                "PLACEFORMFRAGMENT",
                "PLACEFORM"
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_info, container, false);
    }
}
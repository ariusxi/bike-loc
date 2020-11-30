package br.com.felix.bikeloc.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.adapter.PlaceAdapter;
import br.com.felix.bikeloc.auth.Session;
import br.com.felix.bikeloc.model.Place;

public class PlaceMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ArrayList<Place> tempPlacesList;

    private Session session;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializando firebase
        db = FirebaseDatabase.getInstance().getReference();

        // Recuperando os dados da sessão
        session = new Session(this);

        getAllPlaces();
        Log.d("Call", "getAllPlaces();");

        setContentView(R.layout.activity_place_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getAllPlaces() {
        String user = session.get("user_id");
        db.child("places").orderByChild("user").equalTo(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Place currentPlace = new Place(
                            ds.getKey(),
                            ds.child("user").getValue(String.class),
                            ds.child("name").getValue(String.class),
                            ds.child("description").getValue(String.class),
                            ds.child("latitude").getValue(Double.class),
                            ds.child("longitude").getValue(Double.class)
                    );

                    tempPlacesList.add(currentPlace);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Place currentPlace : tempPlacesList) {
            LatLng currentCoordinates = new LatLng(currentPlace.getLatitude(), currentPlace.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(currentCoordinates).title(currentPlace.getName());

            // Adicionando marker ao mapa
            mMap.addMarker(marker);
        }
    }

}
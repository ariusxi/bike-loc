package br.com.felix.bikeloc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.auth.Session;
import br.com.felix.bikeloc.model.Place;

public class PlaceInfo extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference db;
    private Place currentPlaceData;

    public TextView titlePlaceText;
    public TextView descriptionPlaceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        // Inicializando os TextViews da tela
        titlePlaceText = (TextView) findViewById(R.id.titlePlaceText);
        descriptionPlaceText = (TextView) findViewById(R.id.descriptionPlaceText);

        db = FirebaseDatabase.getInstance().getReference();

        // Resgatando os dados do lugar
        Bundle placeData = getIntent().getExtras();
        if (placeData != null) {
            // Salvando os dados do lugar
            currentPlaceData = new Place(
                    placeData.getString("id"),
                    placeData.getString("user"),
                    placeData.getString("name"),
                    placeData.getString("description"),
                    placeData.getDouble("latitude"),
                    placeData.getDouble("longitude")
            );

            // Adicionando informações na tela
            titlePlaceText.setText(currentPlaceData.getName());
            descriptionPlaceText.setText(currentPlaceData.getDescription());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPlaceData);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        LatLng currentCoordinates = new LatLng(currentPlaceData.getLatitude(), currentPlaceData.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(currentCoordinates).title(currentPlaceData.getName());

        builder.include(marker.getPosition());

        int padding = 250;
        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        // Adicionando marker ao mapa
        mMap.addMarker(marker);
        mMap.moveCamera(cameraUpdate);
    }

    public void removePlace(View view) {
        db.child("places").child(currentPlaceData.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PlaceInfo.this, "Local removido com sucesso", Toast.LENGTH_SHORT).show();
                Intent placeListIntent = new Intent(PlaceInfo.this, PlaceList.class);
                startActivity(placeListIntent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(PlaceInfo.this, "Falha ao remover o local", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goTopPlaceEdit(View view) {
        Intent placeFormEdit = new Intent(PlaceInfo.this, PlaceForm.class);
        Bundle placeData = new Bundle();

        // Adicionando os dados do lugar para a intent
        placeData.putString("id", currentPlaceData.getId());
        placeData.putString("user", currentPlaceData.getUser());
        placeData.putString("name", currentPlaceData.getName());
        placeData.putString("description", currentPlaceData.getDescription());
        placeData.putDouble("latitude", currentPlaceData.getLatitude());
        placeData.putDouble("longitude", currentPlaceData.getLongitude());

        // Enviando para formulário de edição
        placeFormEdit.putExtras(placeData);
        startActivity(placeFormEdit);
    }

}
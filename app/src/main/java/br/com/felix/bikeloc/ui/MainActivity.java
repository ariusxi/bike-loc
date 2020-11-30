package br.com.felix.bikeloc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.auth.Session;
import br.com.felix.bikeloc.model.Place;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Session session;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializando firebase
        db = FirebaseDatabase.getInstance().getReference();

        // Recuperando os dados da sessão
        session = new Session(this);

        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String user = session.get("user_id");
        db.child("places").orderByChild("user").equalTo(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    Place currentPlace = new Place(
                            ds.getKey(),
                            ds.child("user").getValue(String.class),
                            ds.child("name").getValue(String.class),
                            ds.child("description").getValue(String.class),
                            ds.child("latitude").getValue(Double.class),
                            ds.child("longitude").getValue(Double.class)
                    );

                    LatLng currentCoordinates = new LatLng(currentPlace.getLatitude(), currentPlace.getLongitude());
                    MarkerOptions marker = new MarkerOptions().position(currentCoordinates).title(currentPlace.getName());

                    builder.include(marker.getPosition());

                    int padding = 250;
                    LatLngBounds bounds = builder.build();

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    // Adicionando marker ao mapa
                    mMap.addMarker(marker);
                    mMap.moveCamera(cameraUpdate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                finish();
                Intent home = new Intent(this, MainActivity.class);
                startActivity(home);
                return(true);

            case R.id.lista:
                //Chama a pagina de cadastro
                finish();
                Intent placeList = new Intent(this, PlaceList.class);
                startActivity(placeList);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

}
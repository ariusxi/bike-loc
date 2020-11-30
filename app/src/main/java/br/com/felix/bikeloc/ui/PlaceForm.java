package br.com.felix.bikeloc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.auth.Session;
import br.com.felix.bikeloc.model.Place;

public class PlaceForm extends AppCompatActivity {

    private static final String TAG = "PlaceAdd";
    private static final int LOCATION_REQUEST_CODE = 1001;

    private DatabaseReference db;
    private Session session;

    public FusedLocationProviderClient fusedLocationProviderClient;

    public String placeId;

    public TextView titleFormText;
    public Button registerPlaceButton;

    public EditText nameInput;
    public EditText descriptionInput;

    public String latitude;
    public String longitude;

    public LocationRequest locationRequest;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null) {
                for (Location location : locationResult.getLocations()) {
                    latitude = String.format("%s", location.getLatitude());
                    longitude = String.format("%s", location.getLongitude());
                }
            }
        }
    };

    public void goToPlaceForm(View view) {

        Intent placeForm = new Intent(this, PlaceForm.class);
        startActivity(placeForm);
        finish();
    }

    private void checkSettingAndStartUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(PlaceForm.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_form);

        // Inicializando os textos da tela
        titleFormText = (TextView) findViewById(R.id.titleFormText);
        registerPlaceButton = (Button) findViewById(R.id.registerPlaceButton);

        // Inicializando sessão
        session = new Session(this);

        // Inicializando os campos do formulário
        nameInput = (EditText) findViewById(R.id.nameInput);
        descriptionInput = (EditText) findViewById(R.id.descriptionInput);

        FirebaseApp.initializeApp(PlaceForm.this);
        db = FirebaseDatabase.getInstance().getReference();

        // Resgatando os dados do lugar
        Bundle placeData = getIntent().getExtras();
        if (placeData != null) {
            titleFormText.setText(String.format("Editar evento %s", placeData.getString("name")));
            registerPlaceButton.setText("Editar");

            // Salvando a informação do id
            placeId = placeData.getString("id");

            // Adicionando os dados no input
            nameInput.setText(String.format("%s", placeData.getString("name")));
            descriptionInput.setText(String.format("%s", placeData.getString("description")));
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void registerPlace(String name, String description) {
        String user = session.get("user_id");

        Place place = new Place(
                user,
                name,
                description,
                Double.parseDouble(latitude),
                Double.parseDouble(longitude)
        );

        Map<String, Object> location = new HashMap<>();
        location.put("user", place.getUser());
        location.put("name", place.getName());
        location.put("description", place.getDescription());
        location.put("latitude", place.getLatitude());
        location.put("longitude", place.getLongitude());
        location.put("createdAt", place.getCreatedAt());

        db.child("places").child(place.getId()).setValue(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PlaceForm.this, "Local cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PlaceForm.this, "Falha ao cadastrar local", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePlace(String name, String description) {
        String user = session.get("user_id");
        Place place = new Place(
                placeId,
                user,
                name,
                description,
                Double.parseDouble(latitude),
                Double.parseDouble(longitude)
        );

        Map<String, Object> location = new HashMap<>();
        location.put("user", place.getUser());
        location.put("name", place.getName());
        location.put("description", place.getDescription());
        location.put("latitude", place.getLatitude());
        location.put("longitude", place.getLongitude());
        location.put("createdAt", place.getCreatedAt());

        db.child("places").child(place.getId()).setValue(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PlaceForm.this, "Local atualizado com sucesso", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PlaceForm.this, "Falha ao atualizar local", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void savePlace(View view) {
        String name = nameInput.getEditableText().toString().trim();
        String description = descriptionInput.getEditableText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Você deve preencher todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        if (placeId != null) {
            this.updatePlace(name, description);
        } else {
            this.registerPlace(name, description);
        }

        // Redirecionando o usuário para a tela de listagem de eventos
        Intent placeListIntent = new Intent(PlaceForm.this, PlaceList.class);
        startActivity(placeListIntent);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkSettingAndStartUpdates();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkSettingAndStartUpdates();
        } else {
            askLocationPermission();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent placeList = new Intent(this, PlaceList.class);
        startActivity(placeList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                // Chama da página home
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
package br.com.felix.bikeloc.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Database;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.auth.Session;
import br.com.felix.bikeloc.model.Place;

public class PlaceFormFragment extends Fragment {

    private static final String TAG = "PlaceAdd";
    private static final int LOCATION_REQUEST_CODE = 1001;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Session session;

    public FusedLocationProviderClient fusedLocationProviderClient;

    public String placeId;

    public TextView titleFormText;
    public Button registerplaceButton;

    public EditText nameInput;
    public EditText descriptionInput;

    public Button registerPlaceButton;

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

    public PlaceFormFragment() {

    }

    private void checkSettingAndStartUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(getActivity());

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
                        apiException.startResolutionForResult(getActivity(), 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public static PlaceFormFragment newInstance() {
        PlaceFormFragment fragment = new PlaceFormFragment();
        return fragment;
    }

    public static PlaceFormFragment newInstance(Place place) {
        PlaceFormFragment fragment = new PlaceFormFragment();

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

        firebaseFirestore.collection("places").document(place.getId()).set(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Local cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Falha ao cadastrar local", Toast.LENGTH_SHORT).show();
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

        firebaseFirestore.collection("places").document(place.getId()).set(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Local atualizado com sucesso", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Falha ao atualizar local", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void savePlace() {
        String name = nameInput.getEditableText().toString().trim();
        String description = descriptionInput.getEditableText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(getActivity(), "Você deve preencher todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        if (placeId != null) {
            this.updatePlace(name, description);
        } else {
            this.registerPlace(name, description);
        }

        // Redirecionando o usuário para a tela de listagem de eventos
        replaceFragment(
                R.id.frameLayoutMain,
                PlaceFragment.newInstance(),
                "PROFILEFRAGMENT",
                "PROFILE"
        );
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializando os textos da tela
        titleFormText = (TextView) getActivity().findViewById(R.id.titleFormText);
        registerplaceButton = (Button) getActivity().findViewById(R.id.registerPlaceButton);

        // Inicializando os campos do formulário
        nameInput = (EditText) getActivity().findViewById(R.id.nameInput);
        descriptionInput = (EditText) getActivity().findViewById(R.id.descriptionInput);

        if (getArguments() != null) {
            titleFormText.setText(String.format("Editar evento %s", getArguments().getString("name")));
            registerplaceButton.setText("Editar");

            // Salvando a informação do id
            placeId = getArguments().getString("id");

            // Adicionando os dados no input
            nameInput.setText(String.format("%s", getArguments().getString("name")));
            descriptionInput.setText(String.format("%s", getArguments().getString("description")));
        }

        // Inicializando a instância do botão
        registerplaceButton = (Button) getActivity().findViewById(R.id.registerPlaceButton);
        registerplaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePlace();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializando a sessão
        session = new Session(getActivity());

        FirebaseApp.initializeApp(getActivity());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkSettingAndStartUpdates();
        } else {
            askLocationPermission();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_place_form, container, false);
    }
}
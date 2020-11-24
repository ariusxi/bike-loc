package br.com.felix.bikeloc.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.felix.bikeloc.ListaLocal;
import br.com.felix.bikeloc.PlaceAdd;
import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.model.Place;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private List<Place> places;
    private double latitude;
    private double longitude;

    public EditText editTextDescription;

    public DatabaseReference databasePlaces;
    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializando o módulo de geolocalização
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        editTextDescription = findViewById(R.id.editTextDescription);

        // Verificando se o celular liberou a permissão de geolocalização
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // Pegando todos os dados do lugares
        databasePlaces = FirebaseDatabase.getInstance().getReference("places");

        // Inicializando lista de lugares
        places = new ArrayList<Place>();

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }


    public void addPlace() {
        // Recuperando dados do formulário
        String description = editTextDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(description)) {
            // Gerando o id do lugar
            String id = databasePlaces.push().getKey();

            // Inicializando a instância do lugar atual
            Place currentPlace = new Place(id, description, latitude, longitude);

            // Adicionando o lugar no banco de dados
            databasePlaces.child(id).setValue(currentPlace);

            // Resetando o valor do campo de descrição
            editTextDescription.setText("");

            // Mostrando resultado ao usuário
            Toast.makeText(this, "Lugar registrado com sucesso", Toast.LENGTH_LONG).show();
        } else {
            // Caso tenha acontecido algum problema ao cadastrar o lugar
            Toast.makeText(this, "Por favor digite a descrição do lugar", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.lista:
                //Chama a pagina de cadastro
                finish();
                Intent intent = new Intent(this, ListaLocal.class);
                startActivity(intent);
                return(true);
            case R.id.cadastrar:
                finish();
                Intent intent1 = new Intent(this, PlaceAdd.class);
                startActivity(intent1);

                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

}
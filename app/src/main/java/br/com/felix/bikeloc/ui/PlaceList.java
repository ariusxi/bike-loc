package br.com.felix.bikeloc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.felix.bikeloc.adapter.PlaceAdapter;
import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.model.Place;

public class PlaceList extends AppCompatActivity {

    private RecyclerView mrecyclerView;
    private PlaceAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference db;

    public void goToPlaceForm(View view) {
        Intent placeForm = new Intent(this, PlaceForm.class);
        startActivity(placeForm);
    }

    public void goTopPlaceEdit(View view) {
        Log.v("View", String.valueOf(view));
        Intent placeForm = new Intent(this, PlaceForm.class);
        startActivity(placeForm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        FirebaseApp.initializeApp(PlaceList.this);
        db = FirebaseDatabase.getInstance().getReference();

        getAllPlaces();
    }

    public void getAllPlaces() {
        ArrayList<Place> tempPlacesList = new ArrayList<>();
        db.child("places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Place currentPlace = new Place(
                            ds.child("id").getValue(String.class),
                            ds.child("name").getValue(String.class),
                            ds.child("description").getValue(String.class),
                            ds.child("latitude").getValue(Double.class),
                            ds.child("longitude").getValue(Double.class)
                    );

                    Log.d("Place", String.valueOf(currentPlace));

                    tempPlacesList.add(currentPlace);
                }

                mrecyclerView = findViewById(R.id.recyclerView);
                mrecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(PlaceList.this);
                adapter = new PlaceAdapter(tempPlacesList);

                adapter.setOnItemClickListener(new PlaceAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int position, Place place) {

                    }
                });

                mrecyclerView.setLayoutManager(mLayoutManager);
                mrecyclerView.setAdapter(adapter);
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
                // Chama da página home
                finish();
                Intent home = new Intent(this, PlaceMap.class);
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
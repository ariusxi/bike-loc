package br.com.felix.bikeloc.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    private SwipeRefreshLayout swipeRefreshLayout;

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
    public void goTopPlaceRemove(View view) {

    }

    public void showRemoveDialog(String placeId){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.remove_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView editTextName = (TextView) dialogView.findViewById(R.id.warningTextView);
        final Button buttonRemove = (Button) dialogView.findViewById(R.id.removePlace);

        dialogBuilder.setTitle("Removendo " + placeId);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        mrecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        FirebaseApp.initializeApp(PlaceList.this);

        db = FirebaseDatabase.getInstance().getReference();

        getAllPlaces();

        // Refresh swipe
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_place_items);

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

    public void removePlace(String placeId){
        db.child("places").child(placeId).removeValue();
    }

    public void getAllPlaces() {
        ArrayList<Place> tempPlacesList = new ArrayList<>();
        db.child("places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Place currentPlace = new Place(
                            ds.getKey(),
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

                    @Override
                    public void onUpdateClick(Place place) {
                        Bundle placeData = new Bundle();

                        // Adicionando os dados do lugar para a intent
                        placeData.putString("id", place.getId());
                        placeData.putString("name", place.getName());
                        placeData.putString("description", place.getDescription());
                        placeData.putDouble("latitude", place.getLatitude());
                        placeData.putDouble("longitude", place.getLongitude());

                        Intent placeFormIntent = new Intent(PlaceList.this, PlaceForm.class);
                        placeFormIntent.putExtras(placeData);
                        startActivity(placeFormIntent);
                    }

                    @Override
                    public void onDeleteClick(int position, Place place) {
                       removePlace(place.getId());
                       getAllPlaces();
                       adapter.notifyItemRemoved(position);
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
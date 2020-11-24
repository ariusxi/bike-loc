package br.com.felix.bikeloc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.felix.bikeloc.model.Place;

public class ListaLocal extends AppCompatActivity {

    private RecyclerView mrecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_local);


        ArrayList<Place> listas = new ArrayList<Place>();
        Place p1 = new Place("teste", "teste2", 2.0,2.0);
        Place p2 = new Place("teste", "teste3", 2.0,2.0);
        listas.add(p1);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);
        listas.add(p2);



        mrecyclerView = findViewById(R.id.recyclerView);
        mrecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new PlaceAdapter(listas);
        mrecyclerView.setLayoutManager(mLayoutManager);
        mrecyclerView.setAdapter(mAdapter);
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
                //Chama a lista de lugares

                return(true);
            case R.id.cadastrar:
                //Chama a tela de cadastro
                finish();
                Intent intent = new Intent(this, PlaceAdd.class);
                startActivity(intent);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }
}
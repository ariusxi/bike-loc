package br.com.felix.bikeloc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PlaceAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_add);
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
                finish();
                Intent intent = new Intent(this, ListaLocal.class);
                startActivity(intent);
                return(true);
            case R.id.cadastrar:

                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

}
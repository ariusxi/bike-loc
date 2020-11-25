package br.com.felix.bikeloc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.model.Place;
import br.com.felix.bikeloc.ui.PlaceForm;
import br.com.felix.bikeloc.ui.PlaceList;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private Context context;

    private ItemClickListener itemClickListener;

    private ArrayList<Place> mPlaceArrayList;

    public PlaceAdapter(ArrayList<Place> placeArrayList){
        this.mPlaceArrayList = placeArrayList;
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context context;

        public TextView name;
        public TextView description;

        public Button editPlaceButton;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            editPlaceButton = itemView.findViewById(R.id.editPlaceButton);

            editPlaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer positionPlace = getAdapterPosition();
                    Place place = mPlaceArrayList.get(positionPlace);
                    Bundle placeData = new Bundle();

                    // Adicionando os dados do lugar para a intent
                    placeData.putString("id", place.getId());
                    placeData.putString("name", place.getName());
                    placeData.putString("description", place.getDescription());
                    placeData.putDouble("latitude", place.getLatitude());
                    placeData.putDouble("longitude", place.getLongitude());

                    Intent placeFormIntent = new Intent(view.getContext(), PlaceForm.class);
                    placeFormIntent.putExtras(placeData);
                    view.getContext().startActivity(placeFormIntent);

                    Log.d("place", String.valueOf(placeData));
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition(), mPlaceArrayList.get(getAdapterPosition()));
            }
        }

    }

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        PlaceViewHolder pvh = new PlaceViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place currentPlace = mPlaceArrayList.get(position);
        holder.name.setText(currentPlace.getName());
        holder.description.setText(currentPlace.getDescription());
    }

    public interface ItemClickListener {
        void onItemClick(int position, Place place);
    }

    @Override
    public int getItemCount() {
        return mPlaceArrayList.size();
    }
}

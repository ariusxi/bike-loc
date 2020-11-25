package br.com.felix.bikeloc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.model.Place;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private ArrayList<Place> mPlaceArrayList;

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
        }
    }

    public PlaceAdapter(ArrayList<Place> placeArrayList){
        mPlaceArrayList = placeArrayList;
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

    @Override
    public int getItemCount() {
        return mPlaceArrayList.size();
    }
}

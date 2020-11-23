package br.com.felix.bikeloc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.com.felix.bikeloc.model.Place;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private ArrayList<Place> mPlaceArrayList;
    public static class PlaceViewHolder extends RecyclerView.ViewHolder{
        //public TextView id;
        public TextView descricao;
        public TextView longitude;
        public TextView latitude;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
           // id = itemView.findViewById(R.id.placeID);
            descricao= itemView.findViewById(R.id.descricao);
            longitude = itemView.findViewById(R.id.longitude);
            latitude = itemView.findViewById(R.id.latitude);
        }
    }

    public PlaceAdapter(ArrayList<Place> placeArrayList){
        mPlaceArrayList = placeArrayList;
    }
    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
        PlaceViewHolder pvh = new PlaceViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place currentPlace = mPlaceArrayList.get(position);
       // holder.id.setText(currentPlace.getId());
        holder.descricao.setText(currentPlace.getDescription());
        holder.longitude.setText(currentPlace.getLongitude() + "");
        holder.latitude.setText(currentPlace.getLatitude() + "");
    }

    @Override
    public int getItemCount() {
        return mPlaceArrayList.size();
    }
}

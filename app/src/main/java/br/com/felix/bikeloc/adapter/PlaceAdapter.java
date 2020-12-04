package br.com.felix.bikeloc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.model.Place;


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
        public CardView cardView;


        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            editPlaceButton = itemView.findViewById(R.id.editPlaceButton);
            cardView = itemView.findViewById(R.id.CardView);

            editPlaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int positionPlace = getAdapterPosition();
                    Place place = mPlaceArrayList.get(positionPlace);

                    itemClickListener.onUpdateClick(place);
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        Place place = mPlaceArrayList.get(position);
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(place);
                        }
                    }
                }
            });
            
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        Place place = mPlaceArrayList.get(position);
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onDeleteClick(position, place);
                        }
                    }
                    return true;
                }
            });

        }


        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(mPlaceArrayList.get(getAdapterPosition()));
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
        void onItemClick(Place place);
        void onUpdateClick(Place place);
        void onDeleteClick(int position, Place place);
    }

    @Override
    public int getItemCount() {
        return mPlaceArrayList.size();
    }
}

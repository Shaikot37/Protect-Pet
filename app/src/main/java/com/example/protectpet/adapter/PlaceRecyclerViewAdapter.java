package com.example.protectpet.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protectpet.PlaceDetailsActivity;
import com.example.protectpet.R;
import com.example.protectpet.models.MyPlaces;
import com.example.protectpet.models.Results;

public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final MyPlaces myPlaces;
    private final double lat;
    private final double lng;

    public PlaceRecyclerViewAdapter(Context context, MyPlaces myPlaces, double lat, double lng) {
        this.context = context;
        this.myPlaces = myPlaces;
        this.lat = lat;
        this.lng = lng;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_place_single, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Results results = myPlaces.getResults().get(position);
        holder.bind(results);
        holder.linearLayoutDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceDetailsActivity.class);
                intent.putExtra("result", results);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myPlaces.getResults().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, address;
        public LinearLayout linearLayoutDetails;
        ImageView placeIV;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textViewPlaceName);
            address = view.findViewById(R.id.textViewAddress);
            linearLayoutDetails = view.findViewById(R.id.linearLayoutDetails);
            placeIV = view.findViewById(R.id.placeImageView);
        }

        public void bind(Results results) {
            name.setText(results.getName());
            address.setText(results.getVicinity());
        }
    }
}

package com.example.protectpet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.protectpet.models.Photos;
import com.example.protectpet.models.Results;
import com.squareup.picasso.Picasso;

public class PlaceDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private Photos photos;
    private TextView textViewName;
    private TextView textViewRating;
    private TextView textViewAddress;
    private TextView textViewAvailability;
    private RatingBar ratingBar;
    private LinearLayout linearLayoutRating;
    private LinearLayout linearLayoutShowOnMap;
    private LinearLayout linearLayoutShowDistanceOnMap;
    private ImageButton back;

    // variable
    private Results results;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // init UI
        init();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            results = (Results) bundle.getSerializable("result");
            lat = bundle.getDouble("lat");
            lng = bundle.getDouble("lng");
            //Toast.makeText(this, String.valueOf(results.getPhotos()[0].getPhoto_reference()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Got Nothing!!", Toast.LENGTH_SHORT).show();
            return;
        }


        imageView = findViewById(R.id.imageView);

        try {
            // get photo
            photos = results.getPhotos()[0];
            String photoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=%s&photoreference=%s&key=%s", 400, photos.getPhoto_reference(), getResources().getString(R.string.google_maps_key));
            Log.d("photoUrl", photoUrl);
            Picasso.get().load(photoUrl).into(imageView);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Picasso.get().load(R.drawable.ic_error_image).into(imageView);
        }

        textViewName.setText(results.getName());
        textViewAddress.setText(results.getVicinity());
        // check if ratings is available for the place
        if (results.getRating() != null) {
            linearLayoutRating.setVisibility(View.VISIBLE);
            textViewRating.setText(results.getRating());
            ratingBar.setRating(Float.valueOf(results.getRating()));
        }
        // check if opening hours is available
        if (results.getOpeningHours() != null) {
            textViewAvailability.setText(results.getOpeningHours().getOpenNow() == false ? "Close now" : "Open now");
        } else {
            textViewAvailability.setText("Not found!");
        }

        linearLayoutShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceDetailsActivity.this, PlaceOnMapActivity.class);
                intent.putExtra("result", results);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("type", "map");
                startActivity(intent);
            }
        });

        linearLayoutShowDistanceOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceDetailsActivity.this, PlaceOnMapActivity.class);
                intent.putExtra("result", results);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("type", "distance");
                startActivity(intent);
            }
        });

        back = findViewById(R.id.backbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backactivity = new Intent(v.getContext(), NearbyPlaces.class);
                startActivity(backactivity);
            }});
    }

    private void init() {
        linearLayoutRating = findViewById(R.id.linearLayoutRating);
        linearLayoutShowOnMap = findViewById(R.id.linearLayoutShowOnMap);
        linearLayoutShowDistanceOnMap = findViewById(R.id.linearLayoutShowDistanceOnMap);
        textViewName = findViewById(R.id.textViewName);
        textViewRating = findViewById(R.id.textViewRating);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewAvailability = findViewById(R.id.textViewAvailability);
        ratingBar = findViewById(R.id.ratingBar);
    }
}

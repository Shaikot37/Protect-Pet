package com.example.protectpet;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment implements View.OnClickListener{

    private ImageView map, about, vaccines, petCare, adopt, rescue;
    public PageFragment() {
        // Required empty public constructor
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_page, container, false);

        map = v.findViewById(R.id.NearbyHelp);
        map.setOnClickListener(this);

        about = v.findViewById(R.id.AboutUs);
        about.setOnClickListener(this);

        vaccines = v.findViewById(R.id.Vaccine);
        vaccines.setOnClickListener(this);

        petCare = v.findViewById(R.id.PetCare);
        petCare.setOnClickListener(this);

        adopt = v.findViewById(R.id.Adopt);
        adopt.setOnClickListener(this);

        rescue = v.findViewById(R.id.Rescue);
        rescue.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.NearbyHelp:
                Intent map = new Intent(v.getContext(), NearbyPlaces.class);
                startActivity(map);
                break;

            case R.id.AboutUs:
                Intent myIntent = new Intent(v.getContext(), AboutUs.class);
                startActivity(myIntent);
                break;

            case R.id.Vaccine:
                Intent vac = new Intent(v.getContext(), Vaccine.class);
                startActivity(vac);
                break;

            case R.id.PetCare:
                Intent pet = new Intent(v.getContext(), PetCare.class);
                startActivity(pet);
                break;

            case R.id.Adopt:
                Intent adopt = new Intent(v.getContext(), AdoptPage.class);
                startActivity(adopt);
                break;

            case R.id.Rescue:
                Intent rescue = new Intent(v.getContext(), RescuePage.class);
                startActivity(rescue);
                break;

        }
    }
}



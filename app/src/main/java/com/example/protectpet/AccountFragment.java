package com.example.protectpet;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    TextView pro_description,pro_name;
    ImageView pro_image;
    public String user_id,user_name, bio, img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private FirebaseFirestore firebaseFirestore;
    public Context context1;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        context1 = container.getContext();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        user_id = Objects.requireNonNull(firebaseUser.getUid());
        firebaseFirestore = FirebaseFirestore.getInstance();

        //retrived the data
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        user_name = task.getResult().getString("username");
                        img = task.getResult().getString("imageURL");
                        bio = task.getResult().getString("bio");

            }
        });



        pro_image =(ImageView)view.findViewById(R.id.img_ele);
        Glide.with(context1).load(img).into(pro_image);

        pro_name=(TextView)view.findViewById(R.id.proName);
        pro_name.setText(user_name);

        pro_description=(TextView)view.findViewById(R.id.description);
        pro_description.setText(bio);

        collapsingToolbarLayout =(CollapsingToolbarLayout)view.findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapseAppBar);
        return view;
    }

}

package com.example.protectpet.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.protectpet.ChatActivity;
import com.example.protectpet.R;
import com.example.protectpet.ShowPlacesOnMapActivity;
import com.example.protectpet.constants.PlacesConstant;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetProfileDetailUser extends BottomSheetDialogFragment {
    String username;
    String imageURL;
    String bio;
    Context context;
    String user_id;

    CircleImageView profile_image;
    TextView p_username;
    TextView p_bio;
    TextView msg;

    public BottomSheetProfileDetailUser(String username, String imageURL, String bio, Context context, String user_id) {
        this.username = username;
        this.imageURL = imageURL;
        this.bio = bio;
        this.context = context;
        this.user_id = user_id;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet_show_profile, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init(view);
        setDetails(username, imageURL, bio);
        return view;
    }

    private void setDetails(String username, String imageURL, String bio) {
        p_username.setText(username);
        p_bio.setText(bio);
try {
    if (imageURL.equals("default")) {
        profile_image.setImageResource(R.drawable.user);
    } else {
        Glide.with(context).load(imageURL).into(profile_image);
    }
}
catch (Exception g){}
    }

    private void init(View view) {
        profile_image = view.findViewById(R.id.iv_profile_bottom_sheet);
        p_username = view.findViewById(R.id.tv_profile__bottom_sheet_fragment_username);
        p_bio = view.findViewById(R.id.tv_profile_bottom_sheet_fragment_bio);
        msg = view.findViewById(R.id.message);

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("userid",user_id);
                startActivity(intent);
            }
        });
    }

}

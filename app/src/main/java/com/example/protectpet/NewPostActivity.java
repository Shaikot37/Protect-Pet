package com.example.protectpet;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

        private Toolbar newPostToolbar;
        private ImageView newPostImage;
        private EditText newPostDesc;
        private Button newPostBtn;
        private Spinner newPostType, newLocation;

        private Uri postImageUri = null;
        private static final int LOCATION_CODE = 1000;

        private ProgressBar newPostProgress;

        private StorageReference storageReference;
        private FirebaseFirestore firebaseFirestore;
        private FirebaseAuth firebaseAuth;
        private Bitmap compressedImageFile;

        private String current_user_id;
        private String type, userlocation;

        private double latitude;
        private double longitude;
        private Location mLastLocation;
        Geocoder geocoder;
        List<Address> addresses;

        String[] post_type = {"Random", "Adopt", "Rescue"};


        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        geocoder = new Geocoder(this, new Locale("bn_BD"));

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostType = findViewById(R.id.spinner);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);


        final ArrayAdapter data = new ArrayAdapter(this, android.R.layout.simple_spinner_item, post_type);

        data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newPostType.setAdapter(data);

        newPostType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                type = data.getItem(arg2).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .start(NewPostActivity.this);

            }
        });


        FirebaseMessaging.getInstance().subscribeToTopic("all");




            newPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isLocationEnabled(NewPostActivity.this)) {
                        Toast.makeText(NewPostActivity.this, "Turn on your location please!", Toast.LENGTH_LONG).show();

                        Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                    else{

                    if (type.equals("Adopt")) {

                        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/all", "Adopt Pet!!",
                                "You should take a look at this post", getApplicationContext(), NewPostActivity.this);
                        fcmNotificationsSender.SendNotifications();
                    } else if (type.equals("Rescue")) {

                        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/all", "Help!!!",
                                "Please rescue a pet!", getApplicationContext(), NewPostActivity.this);
                        fcmNotificationsSender.SendNotifications();
                    }


                    final String desc = newPostDesc.getText().toString();

                    if (!TextUtils.isEmpty(desc) && postImageUri != null) {

                        newPostProgress.setVisibility(View.VISIBLE);

                        final String randomName = UUID.randomUUID().toString();

                        // PHOTO UPLOAD
                        File newImageFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(NewPostActivity.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();

                        // PHOTO UPLOAD

                        UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);
                        filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                                final String downloadUri = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();

                                if (task.isSuccessful()) {

                                    File newThumbFile = new File(postImageUri.getPath());
                                    try {

                                        compressedImageFile = new Compressor(NewPostActivity.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(1)
                                                .compressToBitmap(newThumbFile);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();

                                    UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                            .child(randomName + ".jpg").putBytes(thumbData);

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            try {
                                                String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();
                                                FindLocation();

                                                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                                    //String address = addresses.get(0).getAddressLine(0);
                                                    String address = addresses.get(0).getLocality() + ", " + addresses.get(0).getSubAdminArea() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                                                    //String city = addresses.get(0).getLocality();
                                                    String state = addresses.get(0).getAdminArea();


                                                Map<String, Object> postMap = new HashMap<>();
                                                postMap.put("image_url", downloadUri);
                                                postMap.put("image_thumb", downloadthumbUri);
                                                postMap.put("post_category", type);
                                                postMap.put("location", state);
                                                postMap.put("desc", desc);
                                                postMap.put("address", address);
                                                postMap.put("latitude", latitude);
                                                postMap.put("longitude", longitude);
                                                postMap.put("user_id", current_user_id);
                                                postMap.put("timestamp", FieldValue.serverTimestamp());


                                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                                            Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                            startActivity(mainIntent);
                                                            finish();

                                                        } else {
                                                            Toast.makeText(NewPostActivity.this, "Sorry! we can not add this post", Toast.LENGTH_LONG).show();

                                                        }

                                                        newPostProgress.setVisibility(View.INVISIBLE);

                                                    }
                                                });
                                            } catch (Exception add){
                                                Toast.makeText(NewPostActivity.this, "Address can not be located! please try again", Toast.LENGTH_LONG).show();

                                                Intent mainIntent = new Intent(NewPostActivity.this, NewPostActivity.class);
                                                startActivity(mainIntent);
                                            }


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(NewPostActivity.this, "Address can not be located! please try again", Toast.LENGTH_LONG).show();


                                        }
                                    });


                                } else {

                                    newPostProgress.setVisibility(View.INVISIBLE);

                                }

                            }
                        });


                    }

                }
            }

            });



    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;


    }

    public void FindLocation () {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location;

        if (network_enabled && gps_enabled) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                mLastLocation = location;
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Toast.makeText(
                        getApplicationContext(),
                        String.valueOf(location.getLatitude()) + "\n" + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}


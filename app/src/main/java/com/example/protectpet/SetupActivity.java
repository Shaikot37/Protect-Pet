package com.example.protectpet;

import android.Manifest;
import android.content.Context;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private CircleImageView setupImage;
    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setupProgress;
    private EditText bio;

    private double latitude;
    private double longitude;
    private Location mLastLocation;
    Geocoder geocoder;
    List<Address> addresses;

    private FirebaseFirestore firebaseFirestore; //create particuler user account
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;

    private Uri mainImageURI = null;

    private String user_id, loc; //all new user have unique id

    private boolean isChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        final Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");//toolbar
        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        bio = findViewById(R.id.setup_bio);
        setupProgress = findViewById(R.id.setup_progress);

        setupProgress.setVisibility(View.VISIBLE);

        geocoder = new Geocoder(this, Locale.getDefault());

        //retrived the data
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           if(task.isSuccessful()){
               if(task.getResult().exists()){
                        String name = task.getResult().getString("username");
                        String image = task.getResult().getString("imageURL");
                        String status = task.getResult().getString("bio");
                        mainImageURI = Uri.parse(image);
                        setupName.setText(name);
                        bio.setText(status);

                   RequestOptions placeholderRequest = new RequestOptions();
                   Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest ).load(image).into(setupImage);
                  // Toast.makeText(SetupActivity.this,"data exixts",Toast.LENGTH_LONG).show();
               }

           }else{
               String error = task.getException().getMessage();
               Toast.makeText(SetupActivity.this,"Error:"+error, Toast.LENGTH_LONG).show();

           }
        setupProgress.setVisibility(View.INVISIBLE);
        setupBtn.setEnabled(true);
                                   }
});

//save data into firebase
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                final String user_name = setupName.getText().toString();
                final String biography = bio.getText().toString();
                if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(biography) && mainImageURI != null) {
                setupProgress.setVisibility(View.VISIBLE);

                if(isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();
                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name, biography);

                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "image Error:" + error, Toast.LENGTH_LONG).show();
                                    setupProgress.setVisibility(View.INVISIBLE);
                                }

                            }
                        });
                    }
                else{
                    storeFirestore(null,user_name,biography);
                }
                }
            }

            private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String user_name, String biography) {
                Uri download_uri;

                if(task != null) {

                    download_uri = task.getResult().getDownloadUrl();

                } else {

                    download_uri = mainImageURI;

                }



                FindLocation();
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //String address = addresses.get(0).getAddressLine(0);
                //String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String lat = String.valueOf(latitude);
                String lon = String.valueOf(longitude);

                reference = FirebaseDatabase.getInstance("https://cse499-3dd6a-default-rtdb.firebaseio.com/").getReference("Users").child(user_id);

                Map<String, String> userMap = new HashMap<>();
                userMap.put("id", user_id);
                userMap.put("username", user_name);
                userMap.put("imageURL", download_uri.toString());
                userMap.put("bio",biography);
                userMap.put("location", state);
                userMap.put("status", "offline");
                userMap.put("search", user_name.toLowerCase());
                userMap.put("latitude", lat);
                userMap.put("longitude", lon);

                reference.setValue(userMap);

                firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(SetupActivity.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        } else {

                             String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                        }

                        setupProgress.setVisibility(View.INVISIBLE);

                    }
                });

            }
        });




//select image
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES .M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){


                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });

    }

    public void FindLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location;

        if (network_enabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


    //crop image
    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}

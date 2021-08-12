package com.example.protectpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RescuePage extends AppCompatActivity {
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    private HomeFragment homeFragment;
    private PageFragment pageFragment;
    private AccountFragment accountFragment;
    private RescueFragment rescueFragment;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuepage);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        addPostBtn = findViewById(R.id.add_post_btn);

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        setSupportActionBar(mainToolbar);


        //getSupportActionBar().setTitle("ProtectPet");

        if(mAuth.getCurrentUser() != null) {
            // FRAGMENTS
            mainbottomNav=(BottomNavigationView)findViewById(R.id.mainBottomNav);
            homeFragment = new HomeFragment();
            pageFragment = new PageFragment();
            accountFragment = new AccountFragment();
            rescueFragment = new RescueFragment();

            initializeFragment();
            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.bottom_action_home:

                            replaceFragment(homeFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_account:

                            replaceFragment(accountFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_notif:

                            replaceFragment(pageFragment, currentFragment);
                            return true;

                        default:
                            return false;


                    }

                }
            });


            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(RescuePage.this, NewPostActivity.class);
                    startActivity(newPostIntent);

                }
            });
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:

                Intent settingsIntent = new Intent(RescuePage.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.message:
                Intent msg = new Intent(RescuePage.this, MessageActivity.class);
                startActivity(msg);
                return true;

            default:
                return false;


        }

    }
    private void logOut() {


        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(RescuePage.this, loginActivity.class);
        startActivity(loginIntent);
        finish();

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(RescuePage.this, loginActivity.class);
            startActivity(intent);
            finish();
        }else{
            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent intent = new Intent(RescuePage.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(RescuePage.this, "error"+error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, pageFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);
        fragmentTransaction.add(R.id.main_container, rescueFragment);

        fragmentTransaction.hide(pageFragment);
        fragmentTransaction.hide(accountFragment);
        fragmentTransaction.hide(homeFragment);

        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(pageFragment);

        }

        if(fragment == accountFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(pageFragment);

        }

        if(fragment == pageFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);

        }
        fragmentTransaction.show(fragment);

        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }

}



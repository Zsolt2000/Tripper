package com.zsolt.licenta.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zsolt.licenta.Login.LoginActivity;
import com.zsolt.licenta.R;
import com.zsolt.licenta.Models.Users;

public class MainMenuActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAddTrips;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private ActionBar actionbar;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Users currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setupViews();
        setupToolbar();
        setupFirebase();
        setHeaderProfile();
        addTrips();
        navigationDrawerSelection();
        bottomNavigationSelection();





        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutMain, new HomeFragment()).commit();

    }

    private void setHeaderProfile() {
        View headerView = navigationView.getHeaderView(0);
        ImageView headerProfileImage = headerView.findViewById(R.id.header_profile_image);
        TextView headerTextView = headerView.findViewById(R.id.header_profile_name);
        String uid = firebaseUser.getUid();
        databaseReference.child("Users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot=task.getResult();
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String imageUri = dataSnapshot.child("profileImage").getValue(String.class);
                    String imagePath="Images/"+imageUri;

                    final long IMAGE_SIZE=1024*1024;
                    storageReference.child(imagePath).getBytes(IMAGE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap imageBitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            headerProfileImage.setImageBitmap(imageBitmap);
                        }
                    });
                    headerTextView.setText(userName);
                }
            }
        });
    }

    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void setupToolbar() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Home");
    }

    private void bottomNavigationSelection() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home_navigation:
                    selectedFragment = new HomeFragment();
                    toolbar.setTitle("Home");
                    break;
                case R.id.trips_navigation:
                    selectedFragment = new TripsFragment();
                    toolbar.setTitle("Trips");
                    break;
                case R.id.chat_navigation:
                    selectedFragment = new ChatFragment();
                    toolbar.setTitle("Chats");
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, selectedFragment).commit();
            return true;
        });
    }

    private void navigationDrawerSelection() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Toast.makeText(getApplicationContext(), "Profile was pressed", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_trips:
                    Toast.makeText(getApplicationContext(), "Your trips was pressed", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_friend_list:
                    Toast.makeText(getApplicationContext(), "Friends list was pressed", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_settings:
                    Toast.makeText(getApplicationContext(), "Settings was pressed", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent loginActivity = new Intent(MainMenuActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
            }
            return true;
        });
    }

    private void setupViews() {
        bottomNav = findViewById(R.id.bottomNavigation);
        fabAddTrips = findViewById(R.id.fabAddTrips);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
    }

    private void addTrips() {
        fabAddTrips.setOnClickListener(view -> {
            Intent addTripActivity = new Intent(MainMenuActivity.this, AddTripActivity.class);
            startActivity(addTripActivity);
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
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
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zsolt.licenta.Login.LoginActivity;
import com.zsolt.licenta.R;

public class MainMenuActivity extends AppCompatActivity{
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAddTrips;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private ActionBar actionbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setupViews();
        setupToolbar();

        addTrips();
        navigationDrawerSelection();
        bottomNavigationSelection();


        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutMain, new HomeFragment()).commit();

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
                    break;
                case R.id.trips_navigation:
                    selectedFragment = new TripsFragment();
                    break;
                case R.id.chat_navigation:
                    selectedFragment = new ChatFragment();
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
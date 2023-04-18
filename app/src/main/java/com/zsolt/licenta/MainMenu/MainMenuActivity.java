package com.zsolt.licenta.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.zsolt.licenta.AddTripActivity;
import com.zsolt.licenta.Login.LoginActivity;
import com.zsolt.licenta.R;

public class MainMenuActivity extends AppCompatActivity {
private BottomNavigationView bottomNav;
private FloatingActionButton fabAddTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bottomNav=findViewById(R.id.bottomNavigation);
        fabAddTrips=findViewById(R.id.fabAddTrips);
        bottomNav.setOnItemSelectedListener(navigationListener);
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutMain,new HomeFragment()).commit();
        fabAddTripsOnClick();
    }

    private void fabAddTripsOnClick() {
        fabAddTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTripActivity=new Intent(MainMenuActivity.this, AddTripActivity.class);
                startActivity(addTripActivity);
            }
        });
    }

    private NavigationBarView.OnItemSelectedListener navigationListener= item -> {
        Fragment selectedFragment=null;
        switch (item.getItemId()){
            case R.id.home_navigation:
                selectedFragment= new HomeFragment();
                break;
            case R.id.trips_navigation:
                selectedFragment=new TripsFragment();
                break;
            case R.id.chat_navigation:
                selectedFragment=new ChatFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain,selectedFragment).commit();
        return true;
    };


}
package com.zsolt.licenta.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.zsolt.licenta.R;

public class MainMenuActivity extends AppCompatActivity {
private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bottomNav=findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(navigationListener);
    }

    private NavigationBarView.OnItemSelectedListener navigationListener= new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        }
    };
}
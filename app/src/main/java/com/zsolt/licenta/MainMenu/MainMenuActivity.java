package com.zsolt.licenta.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.Activities.FriendsListActivity;
import com.zsolt.licenta.Activities.PersonalProfileActivity;
import com.zsolt.licenta.Activities.UserProfileActivity;
import com.zsolt.licenta.Activities.YourTripsActivity;
import com.zsolt.licenta.Login.LoginActivity;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.R;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.ViewHolders.SuggestionsAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private List<Users> usersList;
    private SuggestionsAdapter suggestionsAdapter;
    private Users currentUser;
    private List<Trips> tripsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setupViews();
        setupToolbar();
        setupFirebase();
        setupTripsList();
        addTrips();
        navigationDrawerSelection();
        bottomNavigationSelection();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutMain, new HomeFragment()).commit();

    }

    private void setupTripsList() {
        tripsList = new ArrayList<>();
        databaseReference.child("Trips").get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Trips trip = ds.getValue(Trips.class);
                if (trip.isPrivate() == false) {
                    tripsList.add(trip);
                }
            }
        });
    }


    private void setHeaderProfile() {
        View headerView = navigationView.getHeaderView(0);
        ImageView headerProfileImage = headerView.findViewById(R.id.header_profile_image);
        TextView headerTextView = headerView.findViewById(R.id.header_profile_name);
        String uid = firebaseUser.getUid();
        databaseReference.child("Users").child(uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(Users.class);
                storageReference.child("Images/" + currentUser.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.profile_icon).into(headerProfileImage));
                headerTextView.setText(currentUser.getName());
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!currentUser.getDeviceToken().equals(s)) {
                            databaseReference.child("Users").child(currentUser.getUid()).child("deviceToken").setValue(s);
                        }
                    }
                });
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
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.home_navigation:
                    fragment = new HomeFragment();
                    toolbar.setTitle("Home");
                    break;
                case R.id.trips_navigation:
                    fragment = new TripsFragment();
                    toolbar.setTitle("Trips");
                    break;
                case R.id.chat_navigation:
                    fragment = new ChatFragment();
                    toolbar.setTitle("Chats");
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, fragment).commit();
            return true;
        });
    }


    private void navigationDrawerSelection() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Intent profileActivity = new Intent(MainMenuActivity.this, PersonalProfileActivity.class);
                    startActivity(profileActivity);
                    break;
                case R.id.nav_trips:
                    Intent yourTripsActivity = new Intent(MainMenuActivity.this, YourTripsActivity.class);
                    startActivity(yourTripsActivity);
                    break;
                case R.id.nav_friend_list:
                    Intent friendsListActivity = new Intent(MainMenuActivity.this, FriendsListActivity.class);
                    startActivity(friendsListActivity);
                    break;
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();

                    Intent loginActivity = new Intent(MainMenuActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setHeaderProfile();
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search).setOnActionExpandListener(onActionExpandListener);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) menuItem.getActionView();
        autoCompleteTextView.setHint("Search for people");
        autoCompleteTextView.setThreshold(1);
        setupSearchSuggestions();
        suggestionsAdapter = new SuggestionsAdapter(getApplicationContext(), R.layout.custom_suggestion_dropdown_item, usersList);
        autoCompleteTextView.setAdapter(suggestionsAdapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            Users selectedUser = suggestionsAdapter.getItem(position);
            Intent intent = new Intent(MainMenuActivity.this, UserProfileActivity.class);
            intent.putExtra("selectedUser", (Serializable) selectedUser);
            autoCompleteTextView.setText("");
            startActivity(intent);
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void setupSearchSuggestions() {
        usersList = new ArrayList<>();
        databaseReference.child("Users").get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                if (ds.getValue(Users.class).getUid().equals(currentUser.getUid()))
                    continue;
                else
                    usersList.add(ds.getValue(Users.class));
            }
        });

    }

    private final MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
            return true;
        }
    };


}
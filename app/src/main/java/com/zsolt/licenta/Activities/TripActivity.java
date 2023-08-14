package com.zsolt.licenta.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zsolt.licenta.CustomViews.AddFriendDialogFragment;
import com.zsolt.licenta.MainMenu.AddTripActivity;
import com.zsolt.licenta.Models.NotificationType;
import com.zsolt.licenta.Models.TripType;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.Notifications.Data;
import com.zsolt.licenta.Notifications.MyResponse;
import com.zsolt.licenta.Notifications.NotificationSender;
import com.zsolt.licenta.Notifications.RetrofitClient;
import com.zsolt.licenta.Notifications.TripperMessagingData;
import com.zsolt.licenta.R;
import com.zsolt.licenta.Utils.AddFriendsDialogListener;
import com.zsolt.licenta.ViewHolders.AddFriendsAdapter;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripActivity extends AppCompatActivity implements AddFriendsDialogListener {
    private EditText editViewTripTitle, editViewTripLocation, editViewTripStartDate, editViewTripNumberOfPeople;
    private TextView textViewTripVisibility;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ConstraintLayout constraintLayout;
    private Spinner spinnerViewTripType;
    private SwitchCompat switchViewTripVisibility;
    private RecyclerView recyclerViewInvitedPeople;
    private AddFriendsAdapter addFriendsAdapter;
    private ImageButton buttonViewAddPeople, buttonEditTrip, buttonSaveTrip;
    private Button buttonJoinTrip, buttonDeleteTrip;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private PlacesClient placesClient;
    private Trips currentTrip;
    private boolean isEditableLocation;
    private List<Users> invitedPeople;
    private TripperMessagingData tripperMessagingData;
    private String currentTripTitle;
    private Users currentUser;
    private boolean isUserJoined;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        setupViews();
        setupToolbar();
        setupFirebase();
        getApiKey();
        setupSwitch();
        getCurrentUser();
        setupAddPeopleDialog();
        setupAddLocation();
        setupStartDate();
        setupJoinTrip();
        setupDeleteTrip();
    }

    private void setupDeleteTrip() {
        buttonDeleteTrip.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);
            builder.setTitle("Delete this trip");
            builder.setMessage("Are you sure you want to delete this trip?");
            builder.setPositiveButton("Confirm", (dialog, which) -> databaseReference.child("Trips").child(currentTripTitle).removeValue((error, ref) -> {
                Toast.makeText(TripActivity.this, "This trip has been deleted", Toast.LENGTH_SHORT).show();
                finish();
            }));
            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });
            builder.create();
            builder.show();
        });
    }

    private void setupJoinTrip() {
        buttonJoinTrip.setOnClickListener(v -> {
            HashMap<String, Object> userHashMap = new HashMap<>();
            List<Users> usersList = currentTrip.getInvitedUsers();
            if (isUserJoined) {
                buttonJoinTrip.setText("Join Trip");
                isUserJoined = false;
                usersList.remove(currentUser);
                currentTrip.setInvitedUsers(usersList);
                userHashMap.put(currentTripTitle, currentTrip);
                databaseReference.child("Trips").updateChildren(userHashMap, (error, ref) -> addFriendsAdapter.notifyDataSetChanged());
            } else {
                buttonJoinTrip.setText("Leave Trip");
                isUserJoined = true;
                usersList.add(currentUser);
                currentTrip.setInvitedUsers(usersList);
                userHashMap.put(currentTripTitle, currentTrip);
                databaseReference.child("Trips").updateChildren(userHashMap, (error, ref) -> addFriendsAdapter.notifyDataSetChanged());
            }
        });
    }

    private void verifyIfUserIsJoined() {
        if (currentTrip.getInvitedUsers().contains(currentUser)) {
            buttonJoinTrip.setText("Leave Trip");
            isUserJoined = true;
        } else {
            buttonJoinTrip.setText("Join trip");
            isUserJoined = false;
        }
    }

    private void getCurrentUser() {
        databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(dataSnapshot -> {
            currentUser=dataSnapshot.getValue(Users.class);
            verifyIfUserIsJoined();
        });
    }

    private void setupStartDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        editViewTripStartDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = dateFormat.format(calendar.getTime());
                editViewTripStartDate.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupAddLocation() {
        editViewTripLocation.setOnClickListener(view -> {
            Intent mapActivity = new Intent(this, MapActivity.class);
            if (currentTrip.getCreator().getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                mapActivity.putExtra("trip", "creator");
                mapActivity.putExtra("location", editViewTripLocation.getText().toString());
                mapActivity.putExtra("editable", isEditableLocation);
            } else {
                mapActivity.putExtra("trip", "guest");
                mapActivity.putExtra("location", editViewTripLocation.getText().toString());
            }
            activityResultLauncher.launch(mapActivity);
        });
    }


    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent mapData = result.getData();
                Address address = mapData.getParcelableExtra("Location");
                editViewTripLocation.setText(address.getAddressLine(0));
            }
        }
    });

    private void setupAddPeopleDialog() {
        buttonViewAddPeople.setOnClickListener(v -> {
            if (editViewTripNumberOfPeople.getText().toString().isEmpty())
                Toast.makeText(this, "Please add a number of people to be invited", Toast.LENGTH_SHORT).show();
            else {
                int numberOfPeople = Integer.parseInt(editViewTripNumberOfPeople.getText().toString());
                if (currentTrip.getInvitedUsers().size() >= numberOfPeople) {
                    Toast.makeText(this, "Can't exceed the number of invited people", Toast.LENGTH_SHORT).show();
                } else {
                    AddFriendDialogFragment addFriendDialogFragment = new AddFriendDialogFragment();
                    addFriendDialogFragment.setDialogListener(this);
                    addFriendDialogFragment.show(getSupportFragmentManager(), "Friends");
                }
            }
        });
    }

    private void setLayoutReadOnly(ViewGroup layout, boolean focusable) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ImageButton) {
                if (focusable) {
                    child.setVisibility(View.VISIBLE);
                } else {
                    child.setVisibility(View.INVISIBLE);
                }
            } else if (child instanceof LinearLayout) {
                setLayoutReadOnly((ViewGroup) child, focusable);
            } else if (child instanceof RecyclerView) {
                continue;
            } else if (child.getId() == R.id.edit_view_location) {
                continue;
            } else {
                child.setEnabled(focusable);
            }
        }
    }

    private void setupSwitch() {
        switchViewTripVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textViewTripVisibility.setText("Private");
            } else {
                textViewTripVisibility.setText("Public");
            }
        });
    }

    private void setupTripInformation() {
        currentTripTitle = getIntent().getStringExtra("trip");
        databaseReference.child("Trips").child(currentTripTitle).get().addOnSuccessListener(dataSnapshot -> {
            currentTrip = dataSnapshot.getValue(Trips.class);
            editViewTripTitle.setText(currentTrip.getTitle());
            editViewTripLocation.setText(currentTrip.getLocation());
            editViewTripNumberOfPeople.setText(String.valueOf(currentTrip.getNumberOfPeople()));
            editViewTripStartDate.setText(currentTrip.getStartDate());
            spinnerViewTripType.setSelection(currentTrip.getTripType().ordinal());
            invitedPeople = new ArrayList<>(currentTrip.getInvitedUsers());
            addFriendsAdapter = new AddFriendsAdapter(currentTrip.getInvitedUsers(), this, this);
            setupSpinner();

            if (currentTrip.isPrivate()) {
                switchViewTripVisibility.setChecked(true);
            } else {
                switchViewTripVisibility.setChecked(false);
            }
            if (currentTrip.getCreator().getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                buttonEditTrip.setVisibility(View.VISIBLE);
                buttonSaveTrip.setVisibility(View.GONE);
                buttonJoinTrip.setVisibility(View.GONE);
            } else {
                buttonEditTrip.setVisibility(View.GONE);
                buttonSaveTrip.setVisibility(View.GONE);
                buttonJoinTrip.setVisibility(View.VISIBLE);
                addFriendsAdapter.setRemovable(false);
            }

            recyclerViewInvitedPeople.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerViewInvitedPeople.setAdapter(addFriendsAdapter);
        });

    }

    @Override
    public void addFriendtoRecyclerView(Users user) {
        List<Users> friendList = currentTrip.getInvitedUsers();
        if (friendList.size() == 0) {
            friendList.add(user);
            addFriendsAdapter.notifyItemChanged(0);
        } else if (friendList.contains(user)) {
            Toast.makeText(this, "This user is already invited", Toast.LENGTH_SHORT).show();
        } else if (friendList.size() >= Integer.parseInt(editViewTripNumberOfPeople.getText().toString())) {
            Toast.makeText(this, "Can't exceed the number of invited people", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < friendList.size(); i++) {
                if (friendList.get(i).equals(user)) {
                    Toast.makeText(this, "This user is already invited", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    friendList.add(user);
                    addFriendsAdapter.notifyItemChanged(i);
                    break;
                }
            }

        }
    }

    private void setupSpinner() {
        List<TripType> tripTypeList = Arrays.asList(TripType.values());
        spinnerViewTripType.setAdapter(new ArrayAdapter<TripType>(TripActivity.this, android.R.layout.simple_spinner_item, tripTypeList));
        spinnerViewTripType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTrip.setTripType((TripType) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getApiKey() {
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String apiKey = null;
        if (applicationInfo != null && applicationInfo.metaData != null) {
            apiKey = applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
        }
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra("trip"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        editViewTripTitle = findViewById(R.id.edit_view_trip_title);
        editViewTripLocation = findViewById(R.id.edit_view_location);
        editViewTripStartDate = findViewById(R.id.edit_view_start_date);
        editViewTripNumberOfPeople = findViewById(R.id.edit_view_number_people);
        constraintLayout = findViewById(R.id.constraint_layout);
        spinnerViewTripType = findViewById(R.id.spinner_view_trip_type);
        switchViewTripVisibility = findViewById(R.id.switch_view_trip_visibility);
        recyclerViewInvitedPeople = findViewById(R.id.recyclerview_view_add_people);
        buttonViewAddPeople = findViewById(R.id.button_view_add_people);
        buttonJoinTrip = findViewById(R.id.button_view_join_trip);
        buttonDeleteTrip = findViewById(R.id.button_view_delete_trip);
        toolbar = findViewById(R.id.toolbar_trip);
        textViewTripVisibility = findViewById(R.id.text_view_trip_visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setupTripInformation();
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.edit_profile);
        menuItem.setActionView(R.layout.custom_settings_profile);
        buttonEditTrip = menuItem.getActionView().findViewById(R.id.profile_edit_button);
        buttonSaveTrip = menuItem.getActionView().findViewById(R.id.profile_save_button);
        buttonDeleteTrip.setVisibility(View.GONE);
        setLayoutReadOnly(constraintLayout, false);
        buttonEditTrip.setOnClickListener(v -> {
            buttonSaveTrip.setVisibility(View.VISIBLE);
            buttonDeleteTrip.setVisibility(View.VISIBLE);
            isEditableLocation = true;
            setLayoutReadOnly(constraintLayout, true);
            addFriendsAdapter.setRemovable(true);
        });
        buttonSaveTrip.setOnClickListener(v -> {
            buttonSaveTrip.setVisibility(View.GONE);
            buttonDeleteTrip.setVisibility(View.GONE);
            setLayoutReadOnly(constraintLayout, false);
            isEditableLocation = false;
            updateTrip();
            addFriendsAdapter.setRemovable(false);
        });
        return true;
    }

    private boolean validTrip() {
        if (editViewTripTitle.getText().toString().isEmpty() ||
                editViewTripLocation.getText().toString().isEmpty() ||
                editViewTripStartDate.getText().toString().isEmpty() || addFriendsAdapter.getItemCount() < 0) {
            Toast.makeText(this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateTrip() {
        if (validTrip()) {
            tripperMessagingData = RetrofitClient.getClient("https://fcm.googleapis.com/").create(TripperMessagingData.class);
            List<Users> newInvitedUsers = new ArrayList<>(addFriendsAdapter.getFriendsList());
            newInvitedUsers.removeAll(invitedPeople);
            currentTrip.setLocation(editViewTripLocation.getText().toString());
            currentTrip.setTitle(editViewTripTitle.getText().toString());
            currentTrip.setStartDate(editViewTripStartDate.getText().toString());
            currentTrip.setNumberOfPeople(Integer.parseInt(editViewTripNumberOfPeople.getText().toString()));
            currentTrip.setPrivate(switchViewTripVisibility.isChecked());
            currentTrip.setInvitedUsers(addFriendsAdapter.getFriendsList());
            HashMap<String, Object> updatedTrip = new HashMap<>();
            updatedTrip.put(currentTripTitle, currentTrip);
            databaseReference.child("Trips").updateChildren(updatedTrip, (error, ref) -> Toast.makeText(TripActivity.this, "Successfully saved the trip", Toast.LENGTH_SHORT).show());
            for (int i = 0; i < newInvitedUsers.size(); i++) {
                sendNotification(newInvitedUsers.get(i).getDeviceToken(),currentTrip.getTitle());
            }
        }
    }

    private void sendNotification(String userToken,String tripTitle) {
        Data data = new Data(tripTitle,NotificationType.NEW_TRIP);
        NotificationSender notificationSender = new NotificationSender(data, userToken);
        tripperMessagingData.sendNotification(notificationSender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200)
                    if (response.body().success != 1)
                        Toast.makeText(TripActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
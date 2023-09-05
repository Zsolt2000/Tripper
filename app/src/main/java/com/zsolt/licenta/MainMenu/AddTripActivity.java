package com.zsolt.licenta.MainMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.zsolt.licenta.Activities.MapActivity;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.Notifications.Data;
import com.zsolt.licenta.Notifications.MyResponse;
import com.zsolt.licenta.Notifications.NotificationSender;
import com.zsolt.licenta.Notifications.NotificationType;
import com.zsolt.licenta.Notifications.RetrofitClient;
import com.zsolt.licenta.Notifications.TripperMessagingData;
import com.zsolt.licenta.Utils.AddFriendsDialogListener;
import com.zsolt.licenta.CustomViews.AddFriendDialogFragment;
import com.zsolt.licenta.Models.TripType;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.ViewHolders.AddFriendsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTripActivity extends AppCompatActivity implements AddFriendsDialogListener {
    private Toolbar toolbar;
    private ActionBar actionbar;
    private EditText editTripTitle, editStartDate, editNumberOfPeople, editAddLocation, editTripInformation;
    private Spinner spinnerTripType;
    private RecyclerView recyclerInvitedPeople;
    private SwitchCompat switchTripVisibility;
    private TextView textTripVisibility;
    private Button buttonAddTrip;
    private ImageButton buttonAddPeople;
    private StorageReference storageReference;
    private PlacesClient placesClient;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private List<TripType> tripTypeList;
    private AddFriendDialogFragment fragment;
    private AddFriendsAdapter addFriendsAdapter;
    private List<Users> friendList;
    private TripperMessagingData tripperMessagingData;
    private TripType tripType;
    private Users currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_add_trip);
        setupViews();
        setupToolbar();
        setupFirebase();
        setupStartDate();
        setupAddLocation();
        setupSwitch();
        setupSpinner();
        setupAddPeopleDialog();
        setupRecyclerView();
        getApiKey();
        setupAddTrip();
        getCurrentUser();
    }

    private void setupAddTrip() {
        buttonAddTrip.setOnClickListener(v -> {
            tripperMessagingData = RetrofitClient.getClient("https://fcm.googleapis.com/").create(TripperMessagingData.class);
            if (validTrip()) {
                String tripTitle = editTripTitle.getText().toString();
                String tripLocation = editAddLocation.getText().toString();
                String tripStartDate = editStartDate.getText().toString();
                String tripInformation = editTripInformation.getText().toString();
                boolean isPrivate = switchTripVisibility.isChecked();
                int numberOfPeople = Integer.parseInt(editNumberOfPeople.getText().toString());
                List<Users> invitedPeople = addFriendsAdapter.getFriendsList();
                Trips trip = new Trips(tripTitle, currentUser, tripStartDate, numberOfPeople, isPrivate, tripLocation, invitedPeople, tripType, tripInformation);
                saveTrip(trip);
            }
        });
    }

    private void sendNotification(String userToken, String tripTitle) {
        Data data = new Data(tripTitle, NotificationType.NEW_TRIP);
        NotificationSender notificationSender = new NotificationSender(data, userToken);
        tripperMessagingData.sendNotification(notificationSender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200)
                    if (response.body().success != 1)
                        Toast.makeText(AddTripActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });

    }

    private boolean validTrip() {
        if (editTripTitle.getText().toString().isEmpty() ||
                editAddLocation.getText().toString().isEmpty() ||
                editStartDate.getText().toString().isEmpty() || editTripInformation.getText().toString().isEmpty() || addFriendsAdapter.getItemCount() < 0) {
            Toast.makeText(this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void saveTrip(Trips trip) {

        databaseReference.child("Trips").child(trip.getTitle()).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddTripActivity.this, "Succesfully saved trip", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < trip.getInvitedUsers().size(); i++) {
                        sendNotification(trip.getInvitedUsers().get(i).getDeviceToken(), trip.getTitle());
                    }
                    finish();
                }
            }
        });
    }


    private void getCurrentUser() {
        databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(Users.class);
            }
        });
    }


    private void setupRecyclerView() {
        friendList = new ArrayList<>();
        recyclerInvitedPeople.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        addFriendsAdapter = new AddFriendsAdapter(friendList, this, this);
        addFriendsAdapter.setRemovable(true);
        recyclerInvitedPeople.setAdapter(addFriendsAdapter);

    }

    private void setupAddPeopleDialog() {
        buttonAddPeople.setOnClickListener(v -> {
            if (editNumberOfPeople.getText().toString().isEmpty())
                Toast.makeText(this, "Please add a number of people to be invited", Toast.LENGTH_SHORT).show();
            else {
                int numberOfPeople = Integer.parseInt(editNumberOfPeople.getText().toString());
                if (friendList.size() >= numberOfPeople) {
                    Toast.makeText(this, "Can't exceed the number of invited people", Toast.LENGTH_SHORT).show();
                } else {
                    fragment = new AddFriendDialogFragment();
                    fragment.setDialogListener(this);
                    fragment.show(getSupportFragmentManager(), "Friends");
                }
            }
        });
    }


    private void setupSpinner() {
        tripTypeList = new ArrayList<>();
        tripTypeList = Arrays.asList(TripType.values());
        spinnerTripType.setAdapter(getDefaultAdapter());
        spinnerTripType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tripType = (TripType) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private ArrayAdapter<TripType> getDefaultAdapter() {
        return new ArrayAdapter<>(AddTripActivity.this, android.R.layout.simple_spinner_item, tripTypeList);
    }


    private void setupSwitch() {
        switchTripVisibility.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                textTripVisibility.setText("Private");
            else {
                textTripVisibility.setText("Public");
            }
        });
    }

    private void setupAddLocation() {
        editAddLocation.setOnClickListener(view -> {
            Intent mapActivity = new Intent(AddTripActivity.this, MapActivity.class);
            mapActivity.putExtra("trip", "creator");
            activityResultLauncher.launch(mapActivity);
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent mapData = result.getData();
                Address address = mapData.getParcelableExtra("Location");
                editAddLocation.setText(address.getAddressLine(0));
            }
        }
    });


    private void setupStartDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        editStartDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTripActivity.this, (datePicker, i, i1, i2) -> {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = dateFormat.format(calendar.getTime());
                editStartDate.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
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

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar_add_trip);
        editAddLocation = findViewById(R.id.edit_view_location);
        editTripTitle = findViewById(R.id.edit_trip_title);
        editStartDate = findViewById(R.id.edit_view_start_date);
        editNumberOfPeople = findViewById(R.id.edit_view_number_people);
        spinnerTripType = findViewById(R.id.spinner_view_trip_type);
        recyclerInvitedPeople = findViewById(R.id.recyclerview_view_add_people);
        switchTripVisibility = findViewById(R.id.switch_view_trip_visibility);
        textTripVisibility = findViewById(R.id.text_view_trip_visibility);
        buttonAddTrip = findViewById(R.id.button_view_add_trip);
        buttonAddPeople = findViewById(R.id.button_view_add_people);
        editTripInformation = findViewById(R.id.edit_trip_information);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Add a trip");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addFriendtoRecyclerView(Users user) {
        if (friendList.size() == 0) {
            friendList.add(user);
            addFriendsAdapter.notifyItemChanged(0);
        } else if (friendList.contains(user)) {
            Toast.makeText(this, "This user is already invited", Toast.LENGTH_SHORT).show();
        } else if (friendList.size() >= Integer.parseInt(editNumberOfPeople.getText().toString())) {
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
}

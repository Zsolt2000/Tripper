package com.zsolt.licenta.MainMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zsolt.licenta.Models.TripType;
import com.zsolt.licenta.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTripActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionbar;
    private AutoCompleteTextView editAddLocation;
    private EditText editTripTitle, editStartDate, editNumberOfPeople;
    private Spinner spinnerTripType;
    private RecyclerView recyclerInvitedPeople;
    private SwitchCompat switchTripVisibility;
    private TextView textTripVisibility;
    private Button buttonAddTrip;
    private ImageButton buttonAddLocation, buttonAddPeople;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private PlacesClient placesClient;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private TripType tripType;
    private List<TripType> tripTypeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        setupViews();
        setupToolbar();
        setupFirebase();
        setupStartDate();
        setupAddLocation();
        setupSwitch();
        setupSpinner();
        getApiKey();



    }

    private void setupSpinner() {
        tripTypeList =new ArrayList<>();
        tripTypeList =Arrays.asList(TripType.values());
        spinnerTripType.setAdapter(getDefaultAdapter());
        spinnerTripType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tripType=(TripType) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private ArrayAdapter<TripType>getDefaultAdapter()
    {
        return new ArrayAdapter<>(AddTripActivity.this, android.R.layout.simple_spinner_item, tripTypeList);
    }


    private void setupSwitch() {
        switchTripVisibility.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                textTripVisibility.setText("Private");
            else {
                textTripVisibility.setText("Public");
            }
        });
    }

    private void setupAddLocation() {
        buttonAddLocation.setOnClickListener(view -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(AddTripActivity.this);
            startAutocomplete.launch(intent);
        });
    }

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

    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i("l", "Place: ${place.getName()}, ${place.getId()}");
                        editAddLocation.setText(place.getName());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i("l", "User canceled autocomplete");
                }
            });


    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
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
        editAddLocation = findViewById(R.id.edit_location);
        editTripTitle = findViewById(R.id.edit_trip_title);
        editStartDate = findViewById(R.id.edit_start_date);
        editNumberOfPeople = findViewById(R.id.edit_number_people);
        spinnerTripType = findViewById(R.id.spinner_trip_type);
        recyclerInvitedPeople = findViewById(R.id.recycler_add_people);
        switchTripVisibility = findViewById(R.id.switch_trip_visibility);
        textTripVisibility = findViewById(R.id.text_trip_visibility);
        buttonAddTrip = findViewById(R.id.button_add_trip);
        buttonAddLocation = findViewById(R.id.button_add_location);
        buttonAddPeople = findViewById(R.id.button_add_people);
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
}

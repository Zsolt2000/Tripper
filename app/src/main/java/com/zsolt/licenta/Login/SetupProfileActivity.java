package com.zsolt.licenta.Login;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.MainMenu.MainMenuActivity;
import com.zsolt.licenta.Models.Gender;
import com.zsolt.licenta.Models.Interests;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.ViewHolders.InterestsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class SetupProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private EditText editName, editAge, editDateOfBirth, editPhoneNumber;
    private AutoCompleteTextView editPlaceOfBirth;
    private ImageButton buttonAddInterests;
    private Button buttonSetupProfile;
    private RecyclerView recyclerViewInterests;
    private Spinner spinnerGender;
    private TextView textAddInterests, textAddProfileImage, textSelectGender;
    private Toolbar toolbar;
    private ActionBar actionbar;
    private List<Gender> genderList;
    private PlacesClient placesClient;
    private List<String> locationsList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Gender gender;
    private InterestsAdapter interestsAdapter;
    private List<Interests> interestsList;
    private boolean hasImage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        setupViews();
        setupToolbar();
        setupSpinner();
        setupDateOfBirth();
        setupLocation();
        setupInterests();
        getApiKey();
        setupProfile();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();

        profileImage.setOnClickListener(view -> openGallery.launch("image/*"));


    }

    private void setupProfile() {
        buttonSetupProfile.setOnClickListener(view -> {
            if (validProfile()) {
                String uid = firebaseUser.getUid();
                String name = editName.getText().toString();
                String phoneNumber = editPhoneNumber.getText().toString();
                String imageName = null;
                String dateOfBirth = editDateOfBirth.getText().toString();
                String location = editPlaceOfBirth.getText().toString();
                int age = Integer.parseInt(editAge.getText().toString());
                HashMap<Integer, Trips> trips = new HashMap<>();
                if (hasImage) {
                    UUID randomID = UUID.randomUUID();
                    imageName = randomID + ".jpg";
                    String imageLocation = "Images/" + imageName;
                    Uri userImage = Uri.parse(profileImage.getTag().toString());
                    storageReference.child(imageLocation).putFile(userImage).addOnSuccessListener(taskSnapshot -> {
                    });
                }
                Users user = new Users(uid, name, phoneNumber, age, dateOfBirth, location, interestsList, trips, imageName, gender);
                databaseReference.child("Users").child(user.getName()).setValue(user).addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Successfully created your profile", Toast.LENGTH_SHORT).show();
                    Intent mainActivity=new Intent(SetupProfileActivity.this,MainMenuActivity.class);
                    startActivity(mainActivity);
                    finish();
                });
            }
        });
    }

    private boolean validProfile() {
        String name = editName.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();
        String age = editAge.getText().toString();
        String dateOfBirth = editDateOfBirth.getText().toString();
        String location = editPlaceOfBirth.getText().toString();
        if (name.isEmpty() || phoneNumber.isEmpty() || age.isEmpty() || dateOfBirth.isEmpty() || location.isEmpty() || interestsList.isEmpty()) {
            Toast.makeText(this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void setupInterests() {
        interestsList = new ArrayList<>();
        List<Integer> selectedInterests = new ArrayList<>();
        String[] dialogItems = new String[Interests.values().length];
        for (int i = 0; i < Interests.values().length; i++)
            dialogItems[i] = Interests.values()[i].toString();
        recyclerViewInterests.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewInterests.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 10;
            }
        });
        buttonAddInterests.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SetupProfileActivity.this);
            builder.setTitle("Select your interests");
            builder.setMultiChoiceItems(dialogItems, null, (dialogInterface, which, isChecked) -> {
                if (isChecked) {
                    selectedInterests.add(which);
                } else if (selectedInterests.contains(which)) {
                    selectedInterests.remove(which);
                }
            }).setPositiveButton("Add", (dialogInterface, id) -> {
                interestsAdapter = new InterestsAdapter(interestsList);
                for (int j = 0; j < dialogItems.length; j++) {
                    if (selectedInterests.contains(j)) {
                        if (interestsList.contains(Interests.valueOf(dialogItems[j])))
                            continue;
                        else
                            interestsList.add(Interests.valueOf(dialogItems[j]));
                    }

                    recyclerViewInterests.setAdapter(interestsAdapter);

                }

            }).setNegativeButton("Cancel", (dialogInterface, id) -> dialogInterface.dismiss());
            builder.create();
            builder.show();
        });
    }

    private void setupLocation() {
        locationsList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationsList);
        editPlaceOfBirth.setAdapter(adapter);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        editPlaceOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setTypesFilter(Arrays.asList(PlaceTypes.CITIES))
                        .setQuery(charSequence.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        if (!locationsList.contains(prediction.getPrimaryText(null).toString()))
                            locationsList.add(prediction.getPrimaryText(null).toString());
                        else
                            locationsList.remove(prediction.getPrimaryText(null).toString());
                        adapter.clear();
                        adapter.addAll(locationsList);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    private void setupDateOfBirth() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        editDateOfBirth.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SetupProfileActivity.this, (datePicker, i, i1, i2) -> {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = dateFormat.format(calendar.getTime());
                editDateOfBirth.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_setup_profile);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Set up profile");
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViews() {
        profileImage = findViewById(R.id.imageProfile);
        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        editDateOfBirth = findViewById(R.id.editDateOfBirth);
        editPlaceOfBirth = findViewById(R.id.editPlaceOfBirth);
        recyclerViewInterests = findViewById(R.id.recyclerViewInterests);
        buttonAddInterests = findViewById(R.id.button_add_interests);
        textAddInterests = findViewById(R.id.text_add_interests);
        buttonSetupProfile = findViewById(R.id.buttonSaveProfile);
        textAddProfileImage = findViewById(R.id.text_setup_profile);
        textSelectGender = findViewById(R.id.text_select_gender);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
    }

    private void setupSpinner() {
        genderList = new ArrayList<>();
        genderList = Arrays.asList(Gender.values());
        spinnerGender.setAdapter(getDefaultAdapter());
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = (Gender) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private ArrayAdapter<Gender> getDefaultAdapter() {
        return new ArrayAdapter<Gender>(SetupProfileActivity.this, android.R.layout.simple_spinner_item, genderList);
    }

    private final ActivityResultLauncher<String> openGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Picasso.get().load(result).into(profileImage);
            profileImage.setTag(result);
            hasImage = true;
        }
    });

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImage);

        }
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
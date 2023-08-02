package com.zsolt.licenta.Login;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.CustomViews.AddInterestsDialogFragment;
import com.zsolt.licenta.MainMenu.MainMenuActivity;
import com.zsolt.licenta.Activities.MapActivity;
import com.zsolt.licenta.Models.Gender;
import com.zsolt.licenta.Models.Interests;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.Utils.AddInterestsDialogListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SetupProfileActivity extends AppCompatActivity implements AddInterestsDialogListener {
    private ImageView profileImage;
    private EditText editName, editAge, editDateOfBirth, editPhoneNumber;
    private AutoCompleteTextView editPlaceOfBirth;
    private ImageButton buttonAddInterests;
    private Button buttonSetupProfile;
    private FlowLayout flowLayoutInterests;
    private Spinner spinnerGender;
    private Toolbar toolbar;
    private ActionBar actionbar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private PlacesClient placesClient;
    private boolean hasImage = false;
    private Users currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        currentUser = new Users();
        setupViews();
        setupToolbar();
        setupSpinner();
        setupDateOfBirth();
        setupLocation();
        setupInterests();
        setupDeviceToken();
        setupProfile();
        setupFirebase();
        getApiKey();
        setupFlowLayout(new ArrayList<>());
        profileImage.setOnClickListener(view -> openGallery.launch("image/*"));


    }

    private void setupDeviceToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> currentUser.setDeviceToken(task.getResult()));
    }


    private void setupFlowLayout(List<Interests> interestsList) {
        flowLayoutInterests.setChildSpacing(5);
        flowLayoutInterests.setRowSpacing(15f);
        for (int i = 0; i < interestsList.size(); i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.custom_add_interests, flowLayoutInterests, false);
            TextView text = itemView.findViewById(R.id.text_interest_name);
            text.setText(interestsList.get(i).toString());
            flowLayoutInterests.addView(itemView);
        }
    }


    private void setupFirebase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
    }

    private void setupProfile() {
        buttonSetupProfile.setOnClickListener(view -> {
            if (validProfile()) {
                currentUser.setUid(firebaseUser.getUid());
                currentUser.setName(editName.getText().toString());
                currentUser.setPhoneNumber(editPhoneNumber.getText().toString());
                currentUser.setAge(Integer.parseInt(editAge.getText().toString()));
                if (hasImage) {
                    UUID randomID = UUID.randomUUID();
                    String imageName = randomID + ".jpg";
                    String imagePath = "Images/" + imageName;
                    currentUser.setProfileImage(imageName);
                    Uri userImage = Uri.parse(profileImage.getTag().toString());
                    storageReference.child(imagePath).putFile(userImage).addOnSuccessListener(taskSnapshot -> {
                    });
                }
                databaseReference.child("Users").child(currentUser.getUid()).setValue(currentUser).addOnSuccessListener(unused -> {
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SetupProfileActivity.this, "Messaging failed", Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                String token=task.getResult();
                                Toast.makeText(SetupProfileActivity.this, "Successfully created your profile", Toast.LENGTH_SHORT).show();
                                Intent mainActivity = new Intent(SetupProfileActivity.this, MainMenuActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }

                        }
                    });
                    Toast.makeText(this, "Successfully created your profile", Toast.LENGTH_SHORT).show();
                    Intent mainActivity = new Intent(SetupProfileActivity.this, MainMenuActivity.class);
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
        if (name.isEmpty() || phoneNumber.isEmpty() || age.isEmpty() || dateOfBirth.isEmpty() || location.isEmpty() || flowLayoutInterests.getChildCount()==0) {
            Toast.makeText(this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void setupInterests() {
        buttonAddInterests.setOnClickListener(view -> {
            AddInterestsDialogFragment fragment = new AddInterestsDialogFragment(currentUser.getInterests(), SetupProfileActivity.this);
            fragment.show(getSupportFragmentManager(), "Friends");
        });
    }

    private void setupLocation() {
        editPlaceOfBirth.setOnClickListener(v -> {
            Intent mapActivity = new Intent(SetupProfileActivity.this, MapActivity.class);
            getLocationFromMap.launch(mapActivity);
        });
    }

    private final ActivityResultLauncher<Intent> getLocationFromMap = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent mapData = result.getData();
                Address address = mapData.getParcelableExtra("Location");
                editPlaceOfBirth.setText(address.getLocality());
                currentUser.setHomeLocation(address.getLocality());
            }
        }
    });

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
                currentUser.setDateOfBirth(selectedDate);
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
        profileImage = findViewById(R.id.image_setup_profile);
        editName = findViewById(R.id.edit_setup_name);
        editAge = findViewById(R.id.edit_setup_age);
        spinnerGender = findViewById(R.id.spinner_setup_gender);
        editDateOfBirth = findViewById(R.id.edit_setup_date);
        editPlaceOfBirth = findViewById(R.id.edit_setup_place);
        flowLayoutInterests = findViewById(R.id.flowlayout_interests);
        buttonAddInterests = findViewById(R.id.button_profile_add_interests);
        buttonSetupProfile = findViewById(R.id.button_setup_save_profile);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
    }

    private void setupSpinner() {

        List<Gender> genderList = Arrays.asList(Gender.values());
        spinnerGender.setAdapter(new ArrayAdapter<Gender>(SetupProfileActivity.this, android.R.layout.simple_spinner_item, genderList));
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentUser.setGender((Gender) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private final ActivityResultLauncher<String> openGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Picasso.get().load(result).into(profileImage);
            profileImage.setTag(result);
            hasImage = true;
        }
    });


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddInterest(List<Interests> interestsList) {
        flowLayoutInterests.removeAllViews();
        currentUser.setInterests(interestsList);
        setupFlowLayout(interestsList);
        removeInterestFromLayout();
    }
    private void removeInterestFromLayout() {
        for (int i = 0; i < flowLayoutInterests.getChildCount(); i++) {
            View itemView = flowLayoutInterests.getChildAt(i);
            ImageButton buttonRemoveInterest = itemView.findViewById(R.id.remove_button_interest);
            buttonRemoveInterest.setVisibility(View.VISIBLE);
            buttonRemoveInterest.setOnClickListener(v1 -> {
                flowLayoutInterests.removeView(itemView);
                TextView interestName = itemView.findViewById(R.id.text_interest_name);
                currentUser.getInterests().remove(Interests.valueOf(interestName.getText().toString()));
            });
        }
    }
}
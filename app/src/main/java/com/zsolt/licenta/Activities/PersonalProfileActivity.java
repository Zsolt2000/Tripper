package com.zsolt.licenta.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.CustomViews.AddInterestsDialogFragment;
import com.zsolt.licenta.Models.Gender;
import com.zsolt.licenta.Models.Interests;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.Utils.AddInterestsDialogListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PersonalProfileActivity extends AppCompatActivity implements AddInterestsDialogListener {
    private ActionBar actionbar;
    private Toolbar toolbar;
    private Button buttonUpdateProfile;
    private ConstraintLayout layoutPersonalProfile;
    private ImageView imagePersonalProfile;
    private EditText editProfileName, editProfileAge, editProfilePhone, editProfileDate, editProfilePlace;
    private ImageButton buttonAddInterests, saveProfileButton;
    private FlowLayout flowLayoutInterests;
    private Spinner spinnerProfileGender;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Users currentUser;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        setupViews();
        getApiKey();
        setLayoutReadOnly(layoutPersonalProfile, false);
        setupFirebase();
        setupToolbar();
        //setupProfile();
        setupAddInterests();
        setupDateOfBirth();
        setupLocation();
        imagePersonalProfile.setOnClickListener(v -> openGallery.launch("image/*"));

    }

    private void updateProfile() {
        currentUser.setAge(Integer.parseInt(editProfileAge.getText().toString()));
        currentUser.setName(editProfileName.getText().toString());
        currentUser.setPhoneNumber(editProfilePhone.getText().toString());
        HashMap<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put(currentUser.getUid(), currentUser);
        databaseReference.child("Users").updateChildren(updatedProfile, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(PersonalProfileActivity.this, "Succesfully updated profile", Toast.LENGTH_SHORT).show();
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

    private void setupLocation() {
        editProfilePlace.setOnClickListener(v -> {
            Intent mapActivity = new Intent(PersonalProfileActivity.this, MapActivity.class);
            getLocationFromMap.launch(mapActivity);
        });
    }

    private void setupDateOfBirth() {
        editProfileDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            editProfileDate.setOnClickListener(view -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalProfileActivity.this, (datePicker, i, i1, i2) -> {
                    calendar.set(Calendar.YEAR, i);
                    calendar.set(Calendar.MONTH, i1);
                    calendar.set(Calendar.DAY_OF_MONTH, i2);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String selectedDate = dateFormat.format(calendar.getTime());
                    editProfileDate.setText(selectedDate);
                    currentUser.setDateOfBirth(selectedDate);
                }, year, month, day);
                datePickerDialog.show();
            });
        });

    }

    private final ActivityResultLauncher<Intent> getLocationFromMap = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent mapData = result.getData();
                Address address = mapData.getParcelableExtra("Location");
                editProfilePlace.setText(address.getLocality());
                currentUser.setHomeLocation(address.getLocality());
            }
        }
    });


    private final ActivityResultLauncher<String> openGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Picasso.get().load(result).into(imagePersonalProfile);
            storageReference.child("Images/" + currentUser.getProfileImage()).putFile(result).addOnSuccessListener(taskSnapshot -> {
            });
        }
    });


    private void setupAddInterests() {
        buttonAddInterests.setOnClickListener(v -> {
            AddInterestsDialogFragment fragment = new AddInterestsDialogFragment(currentUser.getInterests(), PersonalProfileActivity.this);
            fragment.show(getSupportFragmentManager(), "Friends");
        });
    }


    private void setupFlowLayout(List<Interests> interestsList, int viewVisibility) {
        flowLayoutInterests.setChildSpacing(5);
        flowLayoutInterests.setRowSpacing(15f);
        for (int i = 0; i < interestsList.size(); i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.custom_add_interests, flowLayoutInterests, false);
            TextView text = itemView.findViewById(R.id.text_interest_name);
            ImageButton buttonRemoveInterest = itemView.findViewById(R.id.remove_button_interest);
            buttonRemoveInterest.setVisibility(viewVisibility);
            text.setText(interestsList.get(i).toString());
            flowLayoutInterests.addView(itemView);
        }
    }

    private void setupSpinner() {
        List<Gender> genderList = Arrays.asList(Gender.values());
        spinnerProfileGender.setAdapter(new ArrayAdapter<Gender>(PersonalProfileActivity.this, android.R.layout.simple_spinner_item, genderList));
        spinnerProfileGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentUser.setGender((Gender) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setupProfile() {
        databaseReference.child("Users").child(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUser = task.getResult().getValue(Users.class);
                editProfileName.setText(currentUser.getName());
                editProfileAge.setText(String.valueOf(currentUser.getAge()));
                editProfilePhone.setText(currentUser.getPhoneNumber());
                editProfileDate.setText(currentUser.getDateOfBirth());
                editProfilePlace.setText(currentUser.getHomeLocation());
                spinnerProfileGender.setSelection(currentUser.getGender().ordinal());
                storageReference.child("Images/" + currentUser.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.profile_icon).into(imagePersonalProfile));
                setupSpinner();
                setupFlowLayout(currentUser.getInterests(), View.GONE);
            }
        });
    }


    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar_personal_profile);
        layoutPersonalProfile = findViewById(R.id.layout_user_activity);
        imagePersonalProfile = findViewById(R.id.image_user_profile);
        editProfileName = findViewById(R.id.text_user_profile_name);
        editProfileAge = findViewById(R.id.text_user_profile_age);
        editProfilePhone = findViewById(R.id.text_user_profile_phone);
        editProfileDate = findViewById(R.id.text_user_profile_date);
        editProfilePlace = findViewById(R.id.text_user_profile_place);
        flowLayoutInterests = findViewById(R.id.flowlayout_user_profile_interests);
        buttonAddInterests = findViewById(R.id.button_profile_add_interests);
        spinnerProfileGender = findViewById(R.id.text_user_profile_gender);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setupProfile();
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.edit_profile);
        menuItem.setActionView(R.layout.custom_settings_profile);
        saveProfileButton = menuItem.getActionView().findViewById(R.id.profile_save_button);
        saveProfileButton.setVisibility(View.GONE);
        ImageButton editProfileButton = menuItem.getActionView().findViewById(R.id.profile_edit_button);
        editProfileButton.setOnClickListener(v -> {
            setLayoutReadOnly(layoutPersonalProfile, true);
            saveProfileButton.setVisibility(View.VISIBLE);
            removeInterestFromLayout();
        });
        saveProfileButton.setOnClickListener(v -> {
            setLayoutReadOnly(layoutPersonalProfile, false);
            saveProfileButton.setVisibility(View.GONE);
            for (int i = 0; i < flowLayoutInterests.getChildCount(); i++) {
                View itemView = flowLayoutInterests.getChildAt(i);
                ImageButton buttonRemoveInterest = itemView.findViewById(R.id.remove_button_interest);
                buttonRemoveInterest.setVisibility(View.GONE);
            }
            updateProfile();
        });
        return true;
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

    private void setLayoutReadOnly(ViewGroup layout, boolean focusable) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ImageButton) {
                if (focusable) {
                    child.setVisibility(View.VISIBLE);
                } else {
                    child.setVisibility(View.INVISIBLE);
                }
            } else {
                child.setEnabled(focusable);
            }
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Profile");
    }

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
        setupFlowLayout(interestsList, View.VISIBLE);
        removeInterestFromLayout();

    }
}
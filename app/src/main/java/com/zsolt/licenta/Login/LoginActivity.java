package com.zsolt.licenta.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zsolt.licenta.MainMenu.MainMenuActivity;
import com.zsolt.licenta.R;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private Button buttonLogin, buttonSignUp;
    private TextView textForgotPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private boolean isProfileSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupViews();
        setupFirebase();
        login();
        signUp();
        forgotPassword();
        setupLogin();
        setSupportActionBar(toolbar);
    }

    private void setupLogin() {
        if (firebaseUser != null) {
            databaseReference.child("Users").child(firebaseUser.getUid()).get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    Intent mainActivity = new Intent(LoginActivity.this, MainMenuActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Set up your profile first", Toast.LENGTH_SHORT).show();
                    Intent setupProfile = new Intent(LoginActivity.this, WelcomeUsersActivity.class);
                    startActivity(setupProfile);
                }
            });
        }
    }


    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    private void forgotPassword() {
        textForgotPassword.setOnClickListener(view -> {
            editEmail.setError(null);
            String email = editEmail.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill in your Email address", Toast.LENGTH_SHORT).show();
                editEmail.setError("Please don't leave this field empty");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError("Please enter a valid email");
            } else {
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordReset(email);
                    }
                });
            }
        });
    }

    private void setupViews() {
        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textForgotPassword = findViewById(R.id.textForgotPassword);
        toolbar = findViewById(R.id.toolbar_login);
    }


    private void signUp() {
        buttonSignUp.setOnClickListener(view -> {
            Intent signUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpActivity);
        });
    }

    private void login() {
        buttonLogin.setOnClickListener(view -> {
            editEmail.setError(null);
            editPassword.setError(null);
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                editEmail.setError("This field can't be empty");
                editPassword.setError("This field can't be empty");
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                        Intent mainActivity = new Intent(LoginActivity.this, MainMenuActivity.class);
                        startActivity(mainActivity);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Unsuccessful login", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void passwordReset(String email) {

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "An Email has been sent to reset your password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "This Email address doesn't exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
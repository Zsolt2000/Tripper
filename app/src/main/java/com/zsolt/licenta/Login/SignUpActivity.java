package com.zsolt.licenta.Login;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.zsolt.licenta.R;

import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    private TextView editEmailSignUp, editPasswordSignUp, editRepeatPasswordSignUp;
    private Button buttonSignUp;
    private FirebaseAuth auth;
    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        setupViews();
        signUp();
        setupActionBar();

    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Sign up");
    }

    private void signUp() {
        buttonSignUp.setOnClickListener(view -> {
            String email, password, repeatPassword;
            email = editEmailSignUp.getText().toString();
            password = editPasswordSignUp.getText().toString();
            repeatPassword = editRepeatPasswordSignUp.getText().toString();
            if (validCredentials(email, password, repeatPassword)) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Something failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupViews() {
        editEmailSignUp = findViewById(R.id.editEmailSignUp);
        editPasswordSignUp = findViewById(R.id.editPasswordSignUp);
        editRepeatPasswordSignUp = findViewById(R.id.editRepeatPassword);
        buttonSignUp = findViewById(R.id.buttonSignUpActivity);
        toolbar = findViewById(R.id.toolbar_sign_up);
    }

    private boolean validCredentials(String email, String password, String repeatedPassword) {
        if (email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            Toast.makeText(this, "Don't leave the fields empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid Email address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$").matcher(password).matches()) {
            Toast.makeText(this, "Please follow the password format", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(repeatedPassword)) {
            Toast.makeText(this, "Please match the passwords", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
package com.zsolt.licenta.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zsolt.licenta.R;

public class WelcomeUsersActivity extends AppCompatActivity {
    private TextView textWelcome, textAppInfo, textSetUpProfile;
    private Button buttonSetUpProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_users);
        textWelcome=findViewById(R.id.text_welcome);
        textAppInfo=findViewById(R.id.text_app_info);
        textSetUpProfile=findViewById(R.id.text_setup_profile);
        buttonSetUpProfile=findViewById(R.id.button_setup_profile);
        buttonSetUpProfile.setOnClickListener(view -> {
            Intent activitySetupProfile=new Intent(WelcomeUsersActivity.this,SetupProfileActivity.class);
            startActivity(activitySetupProfile);
        });

    }
}
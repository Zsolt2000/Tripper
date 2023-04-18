package com.zsolt.licenta.Login;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.zsolt.licenta.AddTripActivity;
import com.zsolt.licenta.MainMenu.MainMenuActivity;
import com.zsolt.licenta.R;

public class LoginActivity extends AppCompatActivity {
        private EditText editEmail, editPassWord;
        private Button buttonLogin, buttonSignUp;
        private TextView textForgotPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail=findViewById(R.id.editTextEmail);
        editPassWord=findViewById(R.id.editTextPassword);
        buttonLogin=findViewById(R.id.buttonLogin);
        buttonSignUp=findViewById(R.id.buttonSignUp);
        textForgotPassWord=findViewById(R.id.textForgotPassword);

        openMainActivity();

    }


    private void openMainActivity() {
        buttonLogin.setOnClickListener(view -> {
            Intent mainActivity= new Intent(LoginActivity.this,MainMenuActivity.class);
            startActivity(mainActivity);
        });
    }



}
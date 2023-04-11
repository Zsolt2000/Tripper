package com.zsolt.licenta.Login;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.zsolt.licenta.MainMenu.MainMenuActivity;
import com.zsolt.licenta.R;

public class LoginActivity extends AppCompatActivity {
        private TextView textEmail, textPassWord;
        private Button buttonLogin, buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textEmail=findViewById(R.id.editTextEmail);
        textPassWord=findViewById(R.id.editTextPassword);
        buttonLogin=findViewById(R.id.buttonLogin);
        buttonSignUp=findViewById(R.id.buttonSignUp);

        openMainActivity();
    }

    private void openMainActivity() {
        buttonLogin.setOnClickListener(view -> {
            Intent mainActivity= new Intent(LoginActivity.this,MainMenuActivity.class);
            startActivity(mainActivity);
        });
    }

}
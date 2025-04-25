package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView textGoToLogin = findViewById(R.id.textGoToLogin);

        btnSignUp.setOnClickListener(v -> {
            // Dummy signup success
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        textGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }
}

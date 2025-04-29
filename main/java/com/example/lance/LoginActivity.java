package com.example.lance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button btnLogin;
    TextView textGoToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editLoginUsername);
        editPassword = findViewById(R.id.editLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textGoToSignUp = findViewById(R.id.textGoToSignUp);

        btnLogin.setOnClickListener(v -> {
            String inputUsername = editUsername.getText().toString().trim();
            String inputPassword = editPassword.getText().toString().trim();

            if (TextUtils.isEmpty(inputUsername)) {
                editUsername.setError("Username is required");
                return;
            }
            if (TextUtils.isEmpty(inputPassword)) {
                editPassword.setError("Password is required");
                return;
            }

            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            String savedUsername = prefs.getString("username", null);
            String savedPassword = prefs.getString("password", null);

            if (inputUsername.equals(savedUsername) && inputPassword.equals(savedPassword)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        textGoToSignUp.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class))
        );
    }
}

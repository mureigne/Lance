package com.example.lance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText editUsername, editName, editPhone, editEmail, editPassword;
    Button btnSignUp;
    TextView textGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editUsername = findViewById(R.id.editUsername);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editSignUpEmail);
        editPassword = findViewById(R.id.editSignUpPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        textGoToLogin = findViewById(R.id.textGoToLogin);

        btnSignUp.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || username.length() < 4) {
                editUsername.setError("Username must be at least 4 characters");
                return;
            }
            if (TextUtils.isEmpty(name)) {
                editName.setError("Name is required");
                return;
            }
            if (!Patterns.PHONE.matcher(phone).matches()) {
                editPhone.setError("Enter a valid phone number");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError("Enter a valid email");
                return;
            }
            if (password.length() < 6) {
                editPassword.setError("Password must be at least 6 characters");
                return;
            }

            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.putString("name", name);
            editor.putString("phone", phone);
            editor.putString("email", email);
            editor.putString("password", password); // NOTE: insecure storage for real apps
            editor.apply();

            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        textGoToLogin.setOnClickListener(v ->
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class))
        );
    }
}

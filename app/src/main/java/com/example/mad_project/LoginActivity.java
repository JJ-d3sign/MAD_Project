package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // --- Add this import ---

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogin;
    private TextView tvSignup;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        auth = FirebaseAuth.getInstance();

        // --- Add this block to check for a logged-in user ---
        // This will run as soon as the app opens
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, skip LoginActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Close this activity so the user can't press "back" to it
            return; // Stop the rest of onCreate from running
        }
        // --- End of new block ---

        btnLogin.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }
}
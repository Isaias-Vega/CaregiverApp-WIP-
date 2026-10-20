package com.cs360.caregiverapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailAddressEdit;
    private TextInputEditText passwordEdit;
    private Button buttonSignIn;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializes the database
        dbHelper = new DatabaseHelper(this);

        //UI Components
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        buttonSignIn = findViewById(R.id.buttonLogin);

        emailAddressEdit = findViewById(R.id.emailAddressEdit);
        passwordEdit = findViewById(R.id.passwordEdit);

        //Create account button moves user to RegisterActivity
        buttonCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        //Sign in button moves user to homepage on success
        buttonSignIn.setOnClickListener(v -> authenticateUser());


    }

    //Checks the users login credential using a background thread. On success routed to homepage
    private void authenticateUser(){
        String email = Objects.requireNonNull(emailAddressEdit.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEdit.getText()).toString().trim();

        if (email.isEmpty()){
            emailAddressEdit.setError("Please enter your email address.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailAddressEdit.setError("Please enter a valid email.");
            return;
        }

        if (password.isEmpty()){
            passwordEdit.setError("Please enter your password.");
            return;
        }

        //Starting a background thread for log in
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

                boolean success = dbHelper.loginUser(email, password);

                //Return to the main thread to update the ui
                runOnUiThread(() -> {
                    if(success){
                        //Opens the inventory page
                        Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);

                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                Log.e("LoginActivity", "Authentication error", e);

                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Error during log in.", Toast.LENGTH_SHORT).show();
                });
            }
        });

        executor.shutdown();
    }
}
package com.cs360.caregiverapp;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText fullNameEdit;
    private TextInputEditText emailEdit;
    private TextInputEditText phoneNumberEdit;
    private TextInputEditText passwordEdit;
    private TextInputEditText confirmPasswordEdit;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        fullNameEdit = findViewById(R.id.fullNameEdit);
        emailEdit = findViewById(R.id.registerEmailEdit);
        phoneNumberEdit = findViewById(R.id.registerPhoneEdit);
        passwordEdit = findViewById(R.id.registerPasswordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);

        Button registerButton = findViewById(R.id.registerButton);
        Button loginRedirectButton = findViewById(R.id.loginRedirectButton);
        TextView backToLogin = findViewById(R.id.backToLogin);

        registerButton.setOnClickListener(v -> {
            try {
                validateRegistration();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });

        loginRedirectButton.setOnClickListener(v -> finish());
        backToLogin.setOnClickListener(v -> finish());
    }

    private void validateRegistration() throws NoSuchAlgorithmException, InvalidKeySpecException {

        //Takes user input and stores it as a string
        String name = Objects.requireNonNull(fullNameEdit.getText()).toString().trim();
        String email = Objects.requireNonNull(emailEdit.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEdit.getText()).toString();
        String confirmPassword = Objects.requireNonNull(confirmPasswordEdit.getText()).toString();
        String phoneNumber = Objects.requireNonNull(phoneNumberEdit.getText().toString());

        if (name.isEmpty()) {
            fullNameEdit.setError("Please enter your name");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Please enter a valid email");
            return;
        }

        if (!phoneNumber.isEmpty() && !PhoneUtility.isValidPhoneNumber(phoneNumber)){
            phoneNumberEdit.setError("Please enter a valid phone number.");
            return;
        }

        if (password.length() < 8) {
            passwordEdit.setError("Password must have at least 8 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEdit.setError("Passwords do not match");
            return;
        }

        // Firebase account creation will go here later.
        boolean success = dbHelper.registerUser(name, email, password, phoneNumber);

        //If successfully added return to log in screen
        if (success){
            Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
        }
    }
}


package com.cs360.caregiverapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class AddPatientActivity extends AppCompatActivity {
    private Button buttonSave;
    private Button buttonCancel;
    private TextInputEditText patientDOBEdit;
    private MaterialAutoCompleteTextView relationshipDropdown;
    private EditText fullNameEdit;
    private EditText phoneNumberEdit;
    private EditText homeAddressEdit;
    private EditText caregiverNotesEdit;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        //UI Components
        buttonSave = findViewById(R.id.savePatientButton);
        buttonCancel = findViewById(R.id.cancelButton);
        patientDOBEdit = findViewById(R.id.patientDobEdit);
        relationshipDropdown = findViewById(R.id.patientRelationshipEdit);

        fullNameEdit = findViewById(R.id.patientNameEdit);
        phoneNumberEdit = findViewById(R.id.patientPhoneEdit);
        homeAddressEdit = findViewById(R.id.patientAddressEdit);
        caregiverNotesEdit = findViewById(R.id.patientNotesEdit);

        //Initializes the database
        dbHelper = new DatabaseHelper(this);

        //Handles cancel button
        buttonCancel.setOnClickListener(v -> finish());

        //Handles save button
        buttonSave.setOnClickListener(v -> {
            if (savePatient()){
                Toast.makeText(this, "Patient saved successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //Handles patient DOB edit
        patientDOBEdit.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(
                    AddPatientActivity.this,
                    (view, year, month, day) -> {
                        @SuppressLint("DefaultLocale") String selectedDate = String.format(
                                "%02d/%02d/%04d",
                                month + 1,
                                day,
                                year);
                        patientDOBEdit.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        //Handles relationship dropdown
        String[] relationships = {
                "Parent",
                "Spouse",
                "Grandparent",
                "Sibling",
                "Other Family Member",
                "Professional Client",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, relationships);
        relationshipDropdown.setAdapter(adapter);
    }

    private boolean savePatient(){
        String fullName = fullNameEdit.getText().toString().trim();
        String dateOfBirth = patientDOBEdit.getText().toString().trim();
        String relationship = relationshipDropdown.getText().toString().trim();
        String phoneNumber = phoneNumberEdit.getText().toString().trim();
        String homeAddress = homeAddressEdit.getText().toString();
        String notes = caregiverNotesEdit.getText().toString();

        if (fullName.isEmpty()){
            fullNameEdit.setError("Please enter the patients full name.");
            return false;
        }

        if (dateOfBirth.isEmpty()){
            patientDOBEdit.setError("Enter the patients date of birth.");
            return false;
        }

        if (relationship.isEmpty()){
            relationshipDropdown.setError("Please select your relationship to the patient.");
            return false;
        }

        if (!phoneNumber.isEmpty() && !PhoneUtility.isValidPhoneNumber(phoneNumber)){
            phoneNumberEdit.setError("Please enter a valid phone number.");
            return false;
        }

        return dbHelper.addPatient(fullName, dateOfBirth, relationship, phoneNumber, homeAddress, notes);
    }


}

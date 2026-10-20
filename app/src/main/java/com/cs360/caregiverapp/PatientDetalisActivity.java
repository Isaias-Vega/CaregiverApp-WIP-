package com.cs360.caregiverapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PatientDetalisActivity extends AppCompatActivity {
    private long patientId;
    private TextView patientName;
    private TextView relationship;
    private TextView dateOfBirth;
    private TextView phoneNumber;
    private TextView homeAddress;
    private TextView caregiverNotes;
    private Button backButton;
    private Button editPatientDetails;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        dbHelper = new DatabaseHelper(this);

        //UI components
        patientName = findViewById(R.id.detailPatientName);
        relationship = findViewById(R.id.detailRelationship);
        dateOfBirth = findViewById(R.id.detailDateOfBirth);
        phoneNumber = findViewById(R.id.detailPhoneNumber);
        homeAddress = findViewById(R.id.detailHomeAddress);
        caregiverNotes = findViewById(R.id.detailCaregiverNotes);
        backButton = findViewById(R.id.backButton);
        editPatientDetails = findViewById(R.id.editPatientButton);

        //Gets the patient id from the homepage activity
        patientId = getIntent().getLongExtra("patient_id", -1L);

        //Checks if a valid patient id was given
        if (patientId == -1L) {
            Toast.makeText(
                    this,
                    "Patient not found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }

        //handles back button
        backButton.setOnClickListener(v -> finish());

        //Handles edit details button
        editPatientDetails.setOnClickListener(v -> showEditPatientDialog());

        //Loads the patients info
        loadPatientDetails();
    }

    private void loadPatientDetails() {

        // Run the SQLite query on a background thread.
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            Patient patient = dbHelper.getPatientDetails(patientId);

            runOnUiThread(() -> {

                // Check whether the patient exists.
                if (patient == null) {
                    Toast.makeText(this, "Patient not found.", Toast.LENGTH_SHORT).show();

                    finish();
                    return;
                }

                // Display the patient's information.
                patientName.setText(patient.getFullName());
                relationship.setText("Relationship: " + patient.getRelationship());
                dateOfBirth.setText(patient.getDateOfBirth());
                phoneNumber.setText(displayOptional(patient.getPhoneNumber()));
                homeAddress.setText(displayOptional(patient.getHomeAddress()));
                caregiverNotes.setText(displayOptional(patient.getCaregiverNotes()));
            });
        });

        executor.shutdown();
    }

    private String displayOptional(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Not provided";
        }

        return value;
    }

    //Shows an Alert Dialog which can edit patient information
    private void showEditPatientDialog(){
        Patient patient = dbHelper.getPatientDetails(patientId);

        if(patient == null){
            Toast.makeText(this, "Patient not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_patient, null);

        // Initialize dialog fields.
        TextInputEditText nameEdit = dialogView.findViewById(R.id.editPatientName);
        TextInputEditText dobEdit = dialogView.findViewById(R.id.editPatientDob);
        MaterialAutoCompleteTextView relationshipEdit = dialogView.findViewById(R.id.editPatientRelationship);
        TextInputEditText phoneEdit = dialogView.findViewById(R.id.editPatientPhone);
        TextInputEditText addressEdit = dialogView.findViewById(R.id.editPatientAddress);
        TextInputEditText notesEdit = dialogView.findViewById(R.id.editPatientNotes);

        //Populates fields with existing information
        nameEdit.setText(patient.getFullName());
        dobEdit.setText(patient.getDateOfBirth());
        relationshipEdit.setText(patient.getRelationship());
        phoneEdit.setText(patient.getPhoneNumber());
        addressEdit.setText(patient.getHomeAddress());
        notesEdit.setText(patient.getCaregiverNotes());

        //Configure relationship dropdown
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
        relationshipEdit.setAdapter(adapter);

        dobEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        @SuppressLint("DefaultLocale") String selectedDate = String.format(
                                "%02d/%02d/%04d",
                                month + 1,
                                day,
                                year);
                        dobEdit.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        //Creates the alert dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Edit Patient Information")
                .setView(dialogView)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = nameEdit.getText().toString().trim();
                String dob = dobEdit.getText().toString().trim();
                String relationship = relationshipEdit.getText().toString().trim();
                String phone = phoneEdit.getText().toString().trim();
                String address = addressEdit.getText().toString().trim();
                String notes = notesEdit.getText().toString().trim();

                // Validate required information.
                if (name.isEmpty()) {
                    nameEdit.setError("Name is required.");
                    return;
                }

                if (dob.isEmpty()) {
                    dobEdit.setError("Date of birth is required.");
                    return;
                }

                if (relationship.isEmpty()) {
                    relationshipEdit.setError("Select a relationship.");
                    return;
                }

                // Validate optional fields.
                if (!phone.isEmpty() && !PhoneUtility.isValidPhoneNumber(phone)) {
                    phoneEdit.setError("Invalid phone number.");
                    return;
                }

                if (!address.isEmpty() && address.length() < 5) {
                    addressEdit.setError("Enter a valid address.");
                    return;
                }

                if (notes.length() > 1000) {
                    notesEdit.setError("Maximum 1000 characters.");
                    return;
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    boolean updated = false;

                    try{
                        updated = dbHelper.updatePatient(patientId, name, dob, relationship, phone, address, notes);

                    }catch (Exception e){
                        Log.e("PatientDetails", "Update failed.", e);
                    }

                    boolean success = updated;

                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()){
                            return;
                        }

                        if(success){
                            Toast.makeText(this, "Patient updated successfully", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();

                            //Refreshes the patient details
                            loadPatientDetails();
                        }else{

                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                            Toast.makeText(this, "Unable to update patient.", Toast.LENGTH_SHORT).show();
                        }
                    });

                });

                executor.shutdown();

            });
        });

        dialog.show();
    }

}

package com.cs360.caregiverapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomepageActivity extends AppCompatActivity {
    private Button buttonAddPatient;
    private RecyclerView patientRecyclerView;
    private PatientAdapter patientAdapter;
    private TextView patientSummaryText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        dbHelper = new DatabaseHelper(this);

        //UI Components
        buttonAddPatient = findViewById(R.id.addPatientButton);
        patientRecyclerView = findViewById(R.id.patientRecyclerView);
        patientSummaryText = findViewById(R.id.patientSummaryText);

        //Sets new layout
        patientRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Gets Patient list
        List<PatientItem> patients = dbHelper.getAllItems();

        // Creates the adapter with the patient list.
        patientAdapter = new PatientAdapter(
                patients,
                new PatientAdapter.OnPatientClickListener() {
                    @Override
                    public void onViewPatientClick(PatientItem patient) {
                        // Open the selected patient's details.
                        Intent intent = new Intent(
                                HomepageActivity.this,
                                PatientDetalisActivity.class
                        );

                        intent.putExtra("patient_id", patient.getPatientId());
                        startActivity(intent);
                    }
                }
        );

        //Connects RecyclerView to InventoryAdapter
        patientRecyclerView.setAdapter(patientAdapter);


        //Handles Add patient button
        buttonAddPatient.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, AddPatientActivity.class);
            startActivity(intent);
        });
    }
}

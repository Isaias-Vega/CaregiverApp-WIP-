package com.cs360.caregiverapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private List<PatientItem> patients;
    private OnPatientClickListener listener;

    public PatientAdapter(List<PatientItem> patients, OnPatientClickListener listener){
        this.patients = patients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientAdapter.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create a view with patient list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        PatientItem patient = patients.get(position);

        holder.patientName.setText(patient.getName());
        holder.status.setText(patient.getStatus());
        holder.location.setText(patient.getLocation());
        holder.battery.setText(patient.getBattery());
        holder.lastUpdate.setText(patient.getLocation());
        holder.viewPatient.setOnClickListener(v -> {listener.onViewPatientClick(patient);});

    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public interface OnPatientClickListener{
        void onViewPatientClick(PatientItem patient);
    }

    public void updateItems(List<PatientItem> updatedPatients){
        this.patients = updatedPatients;
        notifyDataSetChanged();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder{
        TextView patientName;
        TextView status;
        TextView location;
        TextView battery;
        TextView lastUpdate;
        Button viewPatient;

        public PatientViewHolder(@NonNull View patientView){
            super(patientView);

            patientName = patientView.findViewById(R.id.patientName);
            status = patientView.findViewById(R.id.patientStatus);
            location = patientView.findViewById(R.id.patientLocation);
            battery = patientView.findViewById(R.id.patientBattery);
            lastUpdate = patientView.findViewById(R.id.patientLastUpdate);
            viewPatient = patientView.findViewById(R.id.viewPatientButton);

        }
    }
}

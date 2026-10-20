package com.cs360.caregiverapp;


public class Patient {

    private long patientId;
    private String fullName;
    private String dateOfBirth;
    private String relationship;
    private String phoneNumber;
    private String homeAddress;
    private String caregiverNotes;

    public Patient(long patientId, String fullName,
                   String dateOfBirth, String relationship,
                   String phoneNumber, String homeAddress,
                   String caregiverNotes) {

        this.patientId = patientId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.relationship = relationship;
        this.phoneNumber = phoneNumber;
        this.homeAddress = homeAddress;
        this.caregiverNotes = caregiverNotes;
    }

    public long getPatientId() {
        return patientId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public String getCaregiverNotes() {
        return caregiverNotes;
    }
}


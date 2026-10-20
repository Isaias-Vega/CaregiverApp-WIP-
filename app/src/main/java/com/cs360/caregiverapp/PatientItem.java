package com.cs360.caregiverapp;

public class PatientItem {
    private final long patientId;
    private String name;
    private String status;
    private String location;
    private String battery;
    private String lastUpdate;

    public PatientItem(long patientId, String name, String status, String location, String battery, String lastUpdate){
        this.patientId = patientId;
        this.name = name;
        this.status = status;
        this.location = location;
        this.battery = battery;
        this.lastUpdate = lastUpdate;
    }

    //Getters and setter for parameters
    public long getPatientId(){
        return patientId;
    }

    public String getName(){
        return name;
    }

    public void setName(){
        this.name = name;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBattery(){
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

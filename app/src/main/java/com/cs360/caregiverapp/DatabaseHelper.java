package com.cs360.caregiverapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CareGiverDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_PATIENTS_TABLE =
            "CREATE TABLE patients (" +
            "patient_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "full_name TEXT NOT NULL, " +
            "date_of_birth TEXT NOT NULL, " +
            "relationship TEXT NOT NULL, " +
            "phone_number TEXT, " +
            "home_address TEXT, " +
            "caregiver_notes TEXT)";

    private static final String CREATE_CAREGIVERS_TABLE =
            "CREATE TABLE caregivers (" +
                    "caregiver_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "full_name TEXT NOT NULL, " +
                    "email TEXT, " +
                    "password_hash TEXT, " +
                    "password_salt TEXT, " +
                    "phone_number TEXT)";

    private static final String CREATE_CAREGIVER_PATIENTS_TABLE =
            "CREATE TABLE caregiver_patients (" +
                    "caregiver_id INTEGER NOT NULL, " +
                    "patient_id INTEGER NOT NULL, " +
                    "role TEXT NOT NULL DEFAULT 'caregiver', " +
                    "PRIMARY KEY (caregiver_id, patient_id), " +
                    "FOREIGN KEY (caregiver_id) REFERENCES cargivers(caregiver_id) " +
                    "ON DELETE CASCADE, " +
                    "FOREIGN KEY (patient_id) REFERENCES patients(patient_id) " +
                    "ON DELETE CASCADE)";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PATIENTS_TABLE);
        db.execSQL(CREATE_CAREGIVERS_TABLE);
        db.execSQL(CREATE_CAREGIVER_PATIENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    //----------- Account Functionality --------------//
    public boolean registerUser(String fullName, String email, String password, String phoneNumber) throws NoSuchAlgorithmException, InvalidKeySpecException{
        email = email.trim().toLowerCase(Locale.ROOT);
        fullName = fullName.trim();

        //Returns false if the email already exists
        if(emailExists(email)){
            return false;
        }

        byte[] salt = PasswordUtility.generateSalt();

        byte[] hash = PasswordUtility.hashPassword(password, salt);

        //Encode both byte arrays into strings
        String encodedSalt = Base64.encodeToString(salt, Base64.NO_WRAP);
        String encodedHash = Base64.encodeToString(hash, Base64.NO_WRAP);

        ContentValues values = new ContentValues();
        //Stores values for insert
        values.put("full_name", fullName);
        values.put("email", email);
        values.put("password_hash", encodedHash);
        values.put("password_salt", encodedSalt);
        values.put("phone_number", phoneNumber);

        //Opens a writable db
        SQLiteDatabase db = this.getWritableDatabase();

        //Inserts the values into the caregivers table
        long result = db.insert("caregivers", null, values);

        //Returns true if the insertion was successful
        return result != -1;
    }

    public boolean loginUser(String email, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SQLiteDatabase db = this.getReadableDatabase();

        //Searches database for username, password_hash, and password_salt
        Cursor cursor = db.query(
                "caregivers",
                new String[]{"password_hash", "password_salt"},
                "email = ?",
                new String[]{email},
                null,
                null,
                null
        );

        //Checks if found
        if (!cursor.moveToFirst()) {
            return false;
        }

        //Gets the stored password hash
        String encodedHash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
        String encodedSalt = cursor.getString(cursor.getColumnIndexOrThrow("password_salt"));

        //Decode the stored values
        byte[] storedHash = Base64.decode(encodedHash, Base64.NO_WRAP);
        byte[] storedSalt = Base64.decode(encodedSalt, Base64.NO_WRAP);

        //Hashes the entered password with the stored salt
        byte[] calculateHash = PasswordUtility.hashPassword(password, storedSalt);

        //Compares the calculated hash with the stored hash
        return MessageDigest.isEqual(calculateHash, storedHash);

    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        email = email.trim().toLowerCase(Locale.ROOT);

        try (Cursor cursor = db.query(
                    "caregivers",
                    new String[]{"caregiver_id"},
                    "email = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            )) {
                //Returns true if a matching record is found
                return cursor.moveToFirst();
            }
    }

    //-------- Patient Functionality -----------//

    //Adds a patient to the db
    public boolean addPatient(String fullName, String dateOfBirth, String relationship, String phoneNumber, String homeAddress, String caregiverNotes){
        //Input validation
        if (fullName == null || fullName.trim().isEmpty()){
            return false;
        }

        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()){
            return false;
        }

        if (relationship == null || relationship.trim().isEmpty()){
            return false;
        }


        ContentValues values = new ContentValues();
        //Stores values for input
        values.put("full_name", fullName);
        values.put("date_of_birth", dateOfBirth);
        values.put("relationship", relationship);

        //Stores optional information
        putOptional(values, "phone_number", phoneNumber);
        putOptional(values, "home_address", homeAddress);
        putOptional(values, "caregiver_notes", caregiverNotes);

        //Opens a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //Inserts the patient into the database
        long result = db.insert("patients", null, values);

        return result != -1;
    }

    //TODO: Implement Update and Delete functions after getting homepage setup
    public boolean updatePatient(long patientId, String fullName, String dateOfBirth, String relationship, String phoneNumber, String homeAddress, String caregiverNotes){
        //Validates if a patient was found
        if (patientId <= 0){
            return false;
        }

        //Input validation
        if (fullName == null || fullName.trim().isEmpty()){
            return false;
        }

        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()){
            return false;
        }

        if (relationship == null || relationship.trim().isEmpty()){
            return false;
        }

        //Validates phone number only if it is provided
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (!PhoneUtility.isValidPhoneNumber(phoneNumber)){
                return false;
            }
        }

        //Validates address only if it is provided
        if (homeAddress != null && !homeAddress.trim().isEmpty()){
            if (homeAddress.trim().length() < 5){
                return false;
            }
        }

        //Validates caregiver notes only if it is provided
        if (caregiverNotes != null && !caregiverNotes.trim().isEmpty()){
            if (caregiverNotes.trim().length() > 1000){
                return false;
            }
        }

        ContentValues values = new ContentValues();
        //Stores values for input
        values.put("full_name", fullName.trim());
        values.put("date_of_birth", dateOfBirth.trim());
        values.put("relationship", relationship.trim());

        //Stores optional information
        putOptional(values, "phone_number", phoneNumber);
        putOptional(values, "home_address", homeAddress);
        putOptional(values, "caregiver_notes", caregiverNotes);

        //Opens Writeable db
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.update(
                "patients",
                values,
                "patient_id = ?",
                new String[]{String.valueOf(patientId)}
        );

        return result > 0;
    }

    private void putOptional(ContentValues values, String column, String value){
        //Store NULL if the option field is empty
        if(value == null || value.trim().isEmpty()){
            values.putNull(column);
        }else{
            values.put(column, value.trim());
        }
    }

    public List<PatientItem> getAllItems(){
        List<PatientItem> patients = new ArrayList<>();

        //Opens a readable db
        SQLiteDatabase db = this.getReadableDatabase();

        //Queries the patient table
        try(Cursor cursor = db.query(
                "patients",
                new String[]{"patient_id", "full_name"},
                null,
                null,
                null,
                null,
                "full_name ASC"
        )) {
            //Loops through the database records and stores the values
            while (cursor.moveToNext()){
                long patientId = cursor.getLong(cursor.getColumnIndexOrThrow("patient_id"));
                String patientName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));

                //Temporary monitoring values
                String status = "Not Monitored";
                String location = "Location Unavailable";
                String battery = "N/A";
                String lastUpdate = "No updates";

                //Creates an InventoryItem
                PatientItem patient = new PatientItem(
                        patientId,
                        patientName,
                        status,
                        location,
                        battery,
                        lastUpdate
                );
                //Adds patient to patients list
                patients.add(patient);
            }
        }

        return patients;
    }

    public Patient getPatientDetails(long patientId){
        //Opens a readable db
        SQLiteDatabase db = this.getReadableDatabase();

        //Query the patients table using the patient id
        try(Cursor cursor = db.query(
                "patients",
                null,
                "patient_id = ?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                null
        )){
            // Check whether the patient exists.
            if (cursor.moveToFirst()) {

                // Retrieve information from the database.
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("patient_id"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                String dateOfBirth = cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth"));
                String relationship = cursor.getString(cursor.getColumnIndexOrThrow("relationship"));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                String homeAddress = cursor.getString(cursor.getColumnIndexOrThrow("home_address"));
                String caregiverNotes = cursor.getString(cursor.getColumnIndexOrThrow("caregiver_notes"));

                // Create and return the patient object.
                return new Patient(
                        id,
                        fullName,
                        dateOfBirth,
                        relationship,
                        phoneNumber,
                        homeAddress,
                        caregiverNotes
                );
            }
        }

        // Return null if the patient was not found.
        return null;
    }

    public boolean deletePatient(long patientId) {
        //Opens a writeable db
        SQLiteDatabase db = this.getWritableDatabase();

        //Deletes the patient found with patient id
        int deleted = db.delete(
                "patiens",
                "patiend_id = ?",
                new String[]{String.valueOf(patientId)}
        );

        //Returns true if a row was deleted
        return deleted > 0;
    }

}

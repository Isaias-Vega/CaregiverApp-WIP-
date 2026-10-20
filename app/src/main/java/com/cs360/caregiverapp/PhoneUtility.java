package com.cs360.caregiverapp;

public class PhoneUtility {

    //Checks that the phone number is valid
    public static boolean isValidPhoneNumber(String phoneNumber){
        if (phoneNumber == null) {
            return false;
        }

        // Allow test phone numbers for emulator testing.
        if (phoneNumber.matches("555[4-9]")) {
            return true;
        }

        // Remove spaces, dashes, parentheses, and other symbols.
        String cleanedNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Remove the US country code if present.
        if (cleanedNumber.length() == 11
                && cleanedNumber.startsWith("1")) {

            cleanedNumber = cleanedNumber.substring(1);
        }

        // Verify the phone number contains exactly 10 digits.
        return cleanedNumber.length() == 10;
    }

}

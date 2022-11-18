package com.example.schedule;

import java.time.LocalDate;
import java.time.LocalTime;

public class Staff {
    private final String socialSecurityNumber;
    private final String name;
    private final String email;
    private final String phoneNumber;

    Staff(String ssn, String n, String e, String p){
        socialSecurityNumber = ssn;
        name = n;
        email = e;
        phoneNumber = p;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return this.name; // Value to be displayed in the Spinner
    }
}

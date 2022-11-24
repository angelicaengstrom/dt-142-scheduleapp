package com.example.schedule;

import androidx.annotation.NonNull;

/**
 * En klass som beskriver en arbetare från restaurangen Antons Skafferi
 */
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

    /** Överskriver toString metoden och sätter hur klassen ska skrivas ut.
     *  Detta används bland annat i en Spinner för att skriva ut en läsbara arbetare som är lediga under en viss dag
     */
    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}

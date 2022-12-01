package com.example.schedule;

/**
 * En klass som beskriver en förfrågning från ett skift till en användare
 */
public class Request {
    private String ssn;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
}

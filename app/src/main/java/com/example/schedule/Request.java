package com.example.schedule;

/**
 * En klass som beskriver en förfrågning från ett skift till en användare
 */
public class Request {
    private final String userId;
    private final int shiftId;

    Request(String uid, int sid){
        userId = uid;
        shiftId = sid;
    }

    public String getUserId() {
        return userId;
    }

    public int getShiftId() {
        return shiftId;
    }
}

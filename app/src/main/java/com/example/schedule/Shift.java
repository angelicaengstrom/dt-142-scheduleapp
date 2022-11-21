package com.example.schedule;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Shift {
    private final int id;
    private final Calendar date;
    private final LocalTime startTime;
    private final LocalTime stopTime;
    private final String userId;

    Shift(int id, Calendar date, LocalTime starttime, LocalTime stoptime, String uid){
        this.id = id;
        this.date = date;
        this.startTime = starttime;
        this.stopTime = stoptime;
        this.userId = uid;
    }

    public int getId(){
        return this.id;
    }

    public String getUserId(){ return this.userId; }

    public String getDateString(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
        return dateFormat.format(date.getTime());
    }

    public int getDate(int Calendar){
        return date.get(Calendar);
    }

    public String getStartTime(){
        return this.startTime.toString();
    }

    public String getStopTime(){
        return this.stopTime.toString();
    }

    public String getShift(){
        if (stopTime.isBefore(LocalTime.of(16, 0))) {
            return "Lunchpass";
        }
        return "Kvällspass";
    }

    public boolean hasNotPassed(){
        return !date.before(Calendar.getInstance());
    }

    public boolean isLate(){
        if (stopTime.isBefore(LocalTime.of(16, 0))) {
            return false;
        }
        return true;
    }
}

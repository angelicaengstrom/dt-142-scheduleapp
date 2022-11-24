package com.example.schedule;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * En klass som beskriver en arbetares skift från restaurangen Antons Skafferi
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class Shift {
    private final int id;
    private final Calendar date;
    private final LocalTime startTime;
    private final LocalTime stopTime;
    private final String userId;
    /**
     * Hur medlemmen date ska formateras
     */
    private final SimpleDateFormat dateFormat;

    @SuppressLint("SimpleDateFormat")
    Shift(int id, Calendar date, LocalTime starttime, LocalTime stoptime, String uid){
        this.id = id;
        this.date = date;
        this.startTime = starttime;
        this.stopTime = stoptime;
        this.userId = uid;
        this.dateFormat = new SimpleDateFormat("d MMM");
    }

    public int getId(){
        return this.id;
    }

    public String getUserId(){ return this.userId; }

    /**
     * Hämtar datumet som String enligt ett format via medlemmen dateFormat
     */
    public String getDateString(){
        return dateFormat.format(date.getTime());
    }

    /**
     * Hämtar datum från medlemmen date
     * @param calendar är vad från date som ska hämtas (ex. Calendar.YEAR ger året)
     */
    public int getDate(int calendar){
        return date.get(calendar);
    }

    public String getStartTime(){
        return this.startTime.toString();
    }

    public String getStopTime(){
        return this.stopTime.toString();
    }

    /**
     * Hämtar om passinformation beroende på isLate()
     * @return Kvällspass eller Lunchpass
     */
    public String getShift(){
        if(isLate()) {
            return "Kvällspass";
        }
        return "Lunchpass";
    }

    /**
     * Hämtar om skiftets datum inte har passerat dagens datum
     * @return true eller false
     */
    public boolean hasNotPassed(){
        return !date.before(Calendar.getInstance());
    }

    /**
     * Hämtar om skiftet är ett sent eller tidigt pass
     * @return true eller false
     */
    public boolean isLate(){
        return !stopTime.isBefore(LocalTime.of(16, 0));
    }
}

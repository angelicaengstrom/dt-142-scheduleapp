package com.example.schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import com.example.schedule.json.Shift2;
import com.example.schedule.json.ShiftAPI;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

//Körs inte på maintråden
public class RetrofitFetch extends AsyncTask<Pair<String, HomeActivity>, Void, Void> {
    public List<Shift2> shiftList = new ArrayList<>();
    public Handler handler;

    @SafeVarargs
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected final Void doInBackground(Pair<String, HomeActivity>... params) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date cal = new Date();
        String date = dateFormat.format(cal);
        params[0].second.getComingShifts().clear();

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("http://10.82.231.15:8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ShiftAPI shiftAPI = retrofit.create(ShiftAPI.class);
        Call<List<Shift2>> call = shiftAPI.comingUserShift(params[0].first, date);
        call.enqueue(new Callback<List<Shift2>>() {
            @Override
            public void onResponse(Call<List<Shift2>> call, Response<List<Shift2>> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                shiftList = response.body();
                for(Shift2 s : shiftList){
                    String[] dateComponents = s.getDate().split("-");

                    Calendar c = Calendar.getInstance();
                    int year = Integer.parseInt(dateComponents[0]);
                    int month = Integer.parseInt(dateComponents[1]) - 1;
                    int day = Integer.parseInt(dateComponents[2]);
                    c.set(year, month, day);
                    int startHour = Integer.parseInt(s.getBeginTime().substring(0,2));
                    int stopHour = Integer.parseInt(s.getEndTime().substring(0,2));
                    Shift s1 = new Shift(s.getId(), c, LocalTime.of(startHour,0), LocalTime.of(stopHour,0), s.getEmployee().getSsn());
                    params[0].second.getComingShifts().add(s1);
                    //comingShifts.add(s1);
                }
            }
            @Override
            public void onFailure(Call<List<Shift2>> call, Throwable t) {
            }
        });
        return null;
    }

    protected void onPostExecute(Void result){
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}

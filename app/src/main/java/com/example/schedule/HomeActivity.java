package com.example.schedule;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;

import com.example.schedule.databinding.ActivityHomeBinding;
import com.example.schedule.json.Employee;
import com.example.schedule.json.EmployeeAPI;
import com.example.schedule.json.Request2;
import com.example.schedule.json.RequestAPI;
import com.example.schedule.json.Shift2;
import com.example.schedule.json.ShiftAPI;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    public Fragment homeFragment;
    public Fragment scheduleFragment;
    RecyclerView requestRecyclerView;
    public Fragment moreFragment;
    HTTPRequests httpRequests = HTTPRequests.getInstance();
    public String amountOfShifts;
    private final ArrayList<Shift> comingShifts = new ArrayList<>();
    private final ArrayList<Shift> allShifts = new ArrayList<>();
    private final HashMap<String, Staff> staff = new HashMap<>();
    List<Pair<String,Shift>> requestToMe = new ArrayList<>();
    private Staff me;
    String ssn;
    final int MILLISECONDS = 1000;
    public static Handler handler = new Handler();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        homeFragment = new HomeFragment();
        scheduleFragment = new ScheduleFragment();
        moreFragment = new MoreFragment();

        //Hämta inloggad användare
        ssn = getIntent().getStringExtra("id");
        if (ssn != null) {
            insertMe(ssn);
        }else{
            closeApp();
        }
    }

    public void setMe(Staff staff){ this.me = staff; }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("NonConstantResourceId")
    public void startApp(){
        requestRecyclerView = findViewById(R.id.requestRecyclerView);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.home:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            insertComingUserShifts(ssn);
                            getRequestToUser(ssn);
                            homeFragment = new HomeFragment();
                            replaceFragment(homeFragment);
                            handler.postDelayed(this, MILLISECONDS);
                        }
                    }, MILLISECONDS);
                    break;
                case R.id.schedule:
                    replaceFragment(scheduleFragment);
                    handler.removeCallbacksAndMessages(null);
                    break;
                case R.id.more:
                    replaceFragment(moreFragment);
                    handler.removeCallbacksAndMessages(null);
                    break;
            }
            return true;
        });
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
    }

    public void closeApp(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        Intent mainAct = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(mainAct);
    }

    public ArrayList<Shift> getComingShifts(){
        return comingShifts;
    }

    public ArrayList<Shift> getAllShifts() { return allShifts; }

    public HashMap<String, Staff> getStaff() { return staff; }

    void insertMe(String id){
        httpRequests.insertMe(id, HomeActivity.this);
    }

    Staff getMe(){ return me; }

    public List<Pair<String, Shift>> getRequestToMe(){ return requestToMe; }

    private void insertComingUserShifts(String user){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date cal = new Date();
        String date = dateFormat.format(cal);
        httpRequests.insertComingUserShifts(user, HomeActivity.this, date);
    }

    public List<Pair<String,Shift>> getShiftsAtDate(String date){
        return httpRequests.getShiftsAtDate(date, HomeActivity.this);
    }

    public List<Pair<String,Shift>> getRequestToUser(String user){
        return httpRequests.getRequestToUser(user, HomeActivity.this);
    }

    public ArrayList<Staff> getNonWorkingStaff(String date, boolean isLate){
        return httpRequests.getNonWorkingStaff(date, isLate);
    }
}
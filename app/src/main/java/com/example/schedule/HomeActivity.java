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
    public String amountOfShifts;
    private final ArrayList<Shift> comingShifts = new ArrayList<>();
    private final ArrayList<Shift> allShifts = new ArrayList<>();
    private final HashMap<String, Staff> staff = new HashMap<>();
    List<Pair<String,Shift>> requestToMe = new ArrayList<>();
    private Staff me;
    String ssn;
    Retrofitter<ShiftAPI> shiftAPIRetrofitter = new Retrofitter<>();
    Retrofitter<EmployeeAPI> employeeAPIRetrofitter = new Retrofitter<>();
    Retrofitter<RequestAPI> requestAPIRetrofitter = new Retrofitter<>();
    //databas
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
        EmployeeAPI employeeAPI = employeeAPIRetrofitter.create(EmployeeAPI.class);
        Call<List<Employee>> call = employeeAPI.getEmployeeWithId(id);
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                List<Employee> employee = response.body();
                if(!employee.isEmpty()) {
                    Employee e = employee.get(0);
                    me = new Staff(e.getSsn(), e.getFirstName() + " " + e.getLastName(), e.getEmail(), e.getPhoneNumber());
                    startApp();
                }else{
                    closeApp();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
            }
        });
    }

    Staff getMe(){ return me; }

    public List<Pair<String, Shift>> getRequestToMe(){ return requestToMe; }

    private void insertComingUserShifts(String user){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date cal = new Date();
        String date = dateFormat.format(cal);
        //ArrayList<Shift> tmp = new ArrayList<>();

        ShiftAPI shiftAPI = shiftAPIRetrofitter.create(ShiftAPI.class);

        Call<List<Shift2>> call = shiftAPI.comingUserShift(user, date);
        call.enqueue(new Callback<List<Shift2>>() {
            @Override
            public void onResponse(Call<List<Shift2>> call, Response<List<Shift2>> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                List<Shift2> shifts = response.body();
                comingShifts.clear();

                for(Shift2 s : shifts){
                    String[] dateComponents = s.getDate().split("-");

                    Calendar c = Calendar.getInstance();
                    int year = Integer.parseInt(dateComponents[0]);
                    int month = Integer.parseInt(dateComponents[1]) - 1;
                    int day = Integer.parseInt(dateComponents[2]);
                    c.set(year, month, day);
                    int startHour = Integer.parseInt(s.getBeginTime().substring(0,2));
                    int stopHour = Integer.parseInt(s.getEndTime().substring(0,2));
                    Shift shift = new Shift(s.getId(), c, LocalTime.of(startHour,0), LocalTime.of(stopHour,0), s.getEmployee().getSsn());
                    //tmp.add(shift);
                    comingShifts.add(shift);
                    amountOfShifts = Integer.toString(comingShifts.size());
                }
            }
            @Override
            public void onFailure(Call<List<Shift2>> call, Throwable t) {
            }
        });


    }

    public List<Pair<String,Shift>> getShiftsAtDate(String date){
        List<Pair<String,Shift>> temp = new ArrayList<>();

        ShiftAPI shiftAPI = shiftAPIRetrofitter.create(ShiftAPI.class);

        Call<List<Shift2>> call = shiftAPI.allShiftAtDate(date);
        call.enqueue(new Callback<List<Shift2>>() {
            @Override
            public void onResponse(Call<List<Shift2>> call, Response<List<Shift2>> response) {
                if(!response.isSuccessful()){
                    return;
                }
                List<Shift2> shifts = response.body();

                for(Shift2 s : shifts) {
                    String[] dateComponents = s.getDate().split("-");

                    Calendar c = Calendar.getInstance();
                    int year = Integer.parseInt(dateComponents[0]);
                    int month = Integer.parseInt(dateComponents[1]) - 1;
                    int day = Integer.parseInt(dateComponents[2]);
                    c.set(year, month, day);
                    int startHour = Integer.parseInt(s.getBeginTime().substring(0,2));
                    int stopHour = Integer.parseInt(s.getEndTime().substring(0,2));
                    Shift s1 = new Shift(s.getId(), c, LocalTime.of(startHour,0), LocalTime.of(stopHour,0), s.getEmployee().getSsn());

                    temp.add(new Pair<>(s.getEmployee().getFirstName() + " " + s.getEmployee().getLastName(), s1));

                }
                RecyclerView recyclerView = findViewById(R.id.shiftRecyclerView);
                recyclerView.setAdapter(new ShiftRecyclerViewAdapter(HomeActivity.this, temp));
                //Fungerar denna?
                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
            }

            @Override
            public void onFailure(Call<List<Shift2>> call, Throwable t) {

            }
        });
        return temp;
    }

    public List<Pair<String,Shift>> getRequestToUser(String user){
        RequestAPI requestAPI = requestAPIRetrofitter.create(RequestAPI.class);

        Call<List<Request2>> call = requestAPI.getRequestTo(user);
        call.enqueue(new Callback<List<Request2>>() {
            @Override
            public void onResponse(Call<List<Request2>> call, Response<List<Request2>> response) {
                if(!response.isSuccessful()){
                    return;
                }
                List<Request2> requests = response.body();
                requestToMe.clear();
                for(Request2 r : requests){
                    Shift2 s = r.getShift();
                    String[] dateComponents = s.getDate().split("-");

                    Calendar c = Calendar.getInstance();
                    int year = Integer.parseInt(dateComponents[0]);
                    int month = Integer.parseInt(dateComponents[1]) - 1;
                    int day = Integer.parseInt(dateComponents[2]);
                    c.set(year, month, day);
                    int startHour = Integer.parseInt(s.getBeginTime().substring(0,2));
                    int stopHour = Integer.parseInt(s.getEndTime().substring(0,2));
                    Shift s1 = new Shift(s.getId(), c, LocalTime.of(startHour,0), LocalTime.of(stopHour,0), s.getEmployee().getSsn());
                    requestToMe.add(new Pair<String,Shift>(r.getShift().getEmployee().getFirstName() + " " + r.getShift().getEmployee().getLastName(), s1));
                }
            }

            @Override
            public void onFailure(Call<List<Request2>> call, Throwable t) {

            }
        });
        return requestToMe;
    }

    public ArrayList<Staff> getNonWorkingStaff(String date, boolean isLate){
        ArrayList<Staff> temp = new ArrayList<>();

        EmployeeAPI employeeAPI = employeeAPIRetrofitter.create(EmployeeAPI.class);

        Call<List<Employee>> call = isLate ? employeeAPI.getFreeDinnerEmployeeAt(date) : employeeAPI.getFreeLunchEmployeeAt(date);

        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                List<Employee> employees = response.body();

                for(Employee e : employees) {
                    Staff s = new Staff(e.getSsn(),e.getFirstName() + " " + e.getLastName(), e.getEmail(), e.getPhoneNumber());
                    temp.add(s);
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {

            }
        });
        return temp;
    }
}
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
    private final ArrayList<Request> request = new ArrayList<>();
    List<Pair<String,Shift>> requestToMe = new ArrayList<>();
    private Staff me;
    String ssn;

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

        //replaceFragment(homeFragment);

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

    public void startApp(){
        requestRecyclerView = findViewById(R.id.requestRecyclerView);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.home:
                    //insertComingUserShifts(ssn);
                    //replaceFragment(homeFragment);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            insertComingUserShifts(ssn);
                            getRequestToUser(ssn);
                            /*
                            requestRecyclerView.setAdapter(new RequestRecyclerViewAdapter(HomeActivity.this, requests));
                            requestRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            */
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
        //sharedPreferences.edit().putString("user", null).commit();
                    /*
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user", null);
                    editor.apply();*/
        startActivity(mainAct);
    }

    public ArrayList<Shift> getComingShifts(){
        return comingShifts;
    }

    public ArrayList<Shift> getAllShifts() { return allShifts; }

    public HashMap<String, Staff> getStaff() { return staff; }

    void insertMe(String id){
        String samuel = "10.82.231.15";
        //samuel = "89.233.229.182";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + samuel + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EmployeeAPI employeeAPI = retrofit.create(EmployeeAPI.class);
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

        String samuel = "10.82.231.15";
        //samuel = "89.233.229.182";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + samuel + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ShiftAPI shiftAPI = retrofit.create(ShiftAPI.class);
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
                    Shift s1 = new Shift(s.getId(), c, LocalTime.of(startHour,0), LocalTime.of(stopHour,0), s.getEmployee().getSsn());
                    comingShifts.add(s1);
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

        String samuel = "10.82.231.15";
        //samuel = "89.233.229.182";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + samuel + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ShiftAPI shiftAPI = retrofit.create(ShiftAPI.class);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                /*
                scheduleFragment = new ScheduleFragment();
                replaceFragment(scheduleFragment);*/
            }

            @Override
            public void onFailure(Call<List<Shift2>> call, Throwable t) {

            }
        });

        /*
        for(Shift s : allShifts){
            if(s.getDateString().contains(date)){
                String name = staff.get(s.getUserId()).getName();
                temp.add(new Pair<>(name, s));
            }
        }*/
        return temp;
    }

    private void insertRequest(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        String samuel = "10.82.231.15";
        //samuel = "89.233.229.182";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + samuel + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        RequestAPI requestAPI = retrofit.create(RequestAPI.class);

        Call<List<Request2>> call = requestAPI.getRequestTo(ssn);
        call.enqueue(new Callback<List<Request2>>() {
            @Override
            public void onResponse(Call<List<Request2>> call, Response<List<Request2>> response) {
                if(!response.isSuccessful()){
                    return;
                }
                List<Request2> requests = response.body();
                for(Request2 r : requests){
                    request.add(new Request(r.getToEmployee().getSsn(), r.getShift().getId()));
                }
            }

            @Override
            public void onFailure(Call<List<Request2>> call, Throwable t) {

            }
        });
        /*
        Request r1 = new Request("199905063641",4);
        request.add(r1);
        Request r2 = new Request("200201010337", 3);
        request.add(r2);*/
        /*
        Request r3 = new Request("199905063641", 5);
        request.add(r3);*/
    }

    public List<Pair<String,Shift>> getRequestToUser(String user){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        String samuel = "10.82.231.15";
        //samuel = "89.233.229.182";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + samuel + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        RequestAPI requestAPI = retrofit.create(RequestAPI.class);

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
                /*
                RecyclerView recyclerView = findViewById(R.id.requestRecyclerView);
                recyclerView.setAdapter(new RequestRecyclerViewAdapter(HomeActivity.this, requestToMe));
                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

                TextView requestLabelTxt = findViewById(R.id.txtRequests);
                String[] requestComponents = requestLabelTxt.getText().toString().split(" ");
                requestLabelTxt.setText(requestComponents[0] + " (" + requests.size() + ")");*/
            }

            @Override
            public void onFailure(Call<List<Request2>> call, Throwable t) {

            }
        });
        return requestToMe;
    }

    public ArrayList<Staff> getNonWorkingStaff(String date){
        ArrayList<Staff> temp = new ArrayList<>();
        String samuel = "10.82.231.15";
        //String marcus = "10.82.242.112";
        //samuel = "89.233.229.182";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + samuel + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EmployeeAPI employeeAPI = retrofit.create(EmployeeAPI.class);
        Call<List<Employee>> call = employeeAPI.getFreeEmployeeAt(date);
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

        /*

        for(Pair<String, Shift> p : shifts){
            for(Staff s : temp){
                if(p.first.contains(s.getName())){
                    temp.remove(s);
                    break;
                }
            }
        }*/
        return temp;
    }
}
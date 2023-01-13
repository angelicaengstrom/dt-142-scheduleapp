package com.example.schedule;

import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedule.json.Employee;
import com.example.schedule.json.EmployeeAPI;
import com.example.schedule.json.Request2;
import com.example.schedule.json.RequestAPI;
import com.example.schedule.json.Shift2;
import com.example.schedule.json.ShiftAPI;
import com.example.schedule.json.UpdateResponse;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HTTPRequests {
    Retrofitter<ShiftAPI> shiftAPIRetrofitter;
    Retrofitter<EmployeeAPI> employeeAPIRetrofitter;
    Retrofitter<RequestAPI> requestAPIRetrofitter;
    private static volatile HTTPRequests instance;

    private HTTPRequests(){
        shiftAPIRetrofitter = new Retrofitter<>();
        employeeAPIRetrofitter = new Retrofitter<>();
        requestAPIRetrofitter = new Retrofitter<>();
    }

    public static HTTPRequests getInstance() {
        HTTPRequests result = instance;
        if(result == null){
            synchronized (HTTPRequests.class){
                result = instance;
                if(result == null){
                    instance = result = new HTTPRequests();
                }
            }
        }
        return result;
    }

    void insertMe(String id, HomeActivity context){
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
                    context.setMe(new Staff(e.getSsn(), e.getFirstName() + " " + e.getLastName(), e.getEmail(), e.getPhoneNumber()));
                    context.startApp();
                }else{
                    context.closeApp();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
            }
        });
    }

    public void insertComingUserShifts(String user, HomeActivity context, String date){
        ShiftAPI shiftAPI = shiftAPIRetrofitter.create(ShiftAPI.class);

        Call<List<Shift2>> call = shiftAPI.comingUserShift(user, date);
        call.enqueue(new Callback<List<Shift2>>() {
            @Override
            public void onResponse(Call<List<Shift2>> call, Response<List<Shift2>> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                List<Shift2> shifts = response.body();
                context.getComingShifts().clear();

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
                    context.getComingShifts().add(shift);
                    context.amountOfShifts = Integer.toString(context.getComingShifts().size());
                }
            }
            @Override
            public void onFailure(Call<List<Shift2>> call, Throwable t) {
            }
        });
    }

    public List<Pair<String,Shift>> getShiftsAtDate(String date, HomeActivity context) {
        List<Pair<String, Shift>> temp = new ArrayList<>();

        ShiftAPI shiftAPI = shiftAPIRetrofitter.create(ShiftAPI.class);

        Call<List<Shift2>> call = shiftAPI.allShiftAtDate(date);
        call.enqueue(new Callback<List<Shift2>>() {
            @Override
            public void onResponse(Call<List<Shift2>> call, Response<List<Shift2>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<Shift2> shifts = response.body();

                for (Shift2 s : shifts) {
                    String[] dateComponents = s.getDate().split("-");

                    Calendar c = Calendar.getInstance();
                    int year = Integer.parseInt(dateComponents[0]);
                    int month = Integer.parseInt(dateComponents[1]) - 1;
                    int day = Integer.parseInt(dateComponents[2]);
                    c.set(year, month, day);
                    int startHour = Integer.parseInt(s.getBeginTime().substring(0, 2));
                    int stopHour = Integer.parseInt(s.getEndTime().substring(0, 2));
                    Shift s1 = new Shift(s.getId(), c, LocalTime.of(startHour, 0), LocalTime.of(stopHour, 0), s.getEmployee().getSsn());

                    temp.add(new Pair<>(s.getEmployee().getFirstName() + " " + s.getEmployee().getLastName(), s1));

                }
                RecyclerView recyclerView = context.findViewById(R.id.shiftRecyclerView);
                recyclerView.setAdapter(new ShiftRecyclerViewAdapter(context, temp));
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onFailure(Call<List<Shift2>> call, Throwable t) {

            }
        });
        return temp;
    }

    public List<Pair<String,Shift>> getRequestToUser(String user, HomeActivity context){
        RequestAPI requestAPI = requestAPIRetrofitter.create(RequestAPI.class);

        Call<List<Request2>> call = requestAPI.getRequestTo(user);
        call.enqueue(new Callback<List<Request2>>() {
            @Override
            public void onResponse(Call<List<Request2>> call, Response<List<Request2>> response) {
                if(!response.isSuccessful()){
                    return;
                }
                List<Request2> requests = response.body();
                context.requestToMe.clear();
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
                    context.requestToMe.add(new Pair<String,Shift>(r.getShift().getEmployee().getFirstName() + " " + r.getShift().getEmployee().getLastName(), s1));
                }
            }

            @Override
            public void onFailure(Call<List<Request2>> call, Throwable t) {

            }
        });
        return context.requestToMe;
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

    void fetchEmployee(String id, MainActivity context){
        Retrofitter<EmployeeAPI> retrofitter = new Retrofitter<>();

        EmployeeAPI employeeAPI = retrofitter.create(EmployeeAPI.class);
        Call<List<Employee>> call = employeeAPI.getEmployeeWithId(id);

        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(@NonNull Call<List<Employee>> call, @NonNull Response<List<Employee>> response) {
                if(!response.isSuccessful()) {
                    context.showErrorMessage(context.getString(R.string.nogot_gick_snett));
                    return;
                }
                List<Employee> employee = response.body();
                if(Objects.requireNonNull(employee).size() == 1) {
                    context.saveData();
                    context.startHomeActivity();
                }else{
                    context.showErrorMessage(context.getString(R.string.nogot_gick_snett));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Employee>> call, @NonNull Throwable t) {
                context.showErrorMessage(context.getString(R.string.server_fail));
            }
        });
    }

    void acceptRequest(String userID, View view){
        RequestAPI requestAPI = requestAPIRetrofitter.create(RequestAPI.class);

        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setSsn(userID);
        updateResponse.setId(view.getId());

        Call<String> call = requestAPI.acceptRequest(updateResponse);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    return;
                }
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    void deleteRequest(String userID, View view){
        RequestAPI requestAPI = requestAPIRetrofitter.create(RequestAPI.class);

        //TEMPORÄR
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setSsn(userID);
        updateResponse.setId(view.getId());

        Call<String> call = requestAPI.deleteRequest(updateResponse);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    return;
                }
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void sendRequest(ShiftRecyclerViewAdapter.ShiftViewHolder holder, String ssn) {

        RequestAPI requestAPI = requestAPIRetrofitter.create(RequestAPI.class);

        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setSsn(ssn);
        updateResponse.setId(holder.shiftId);

        Call<String> call = requestAPI.sendRequest(updateResponse);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}

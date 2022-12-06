package com.example.schedule.json;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EmployeeAPI {
    @GET("employee")
    Call<List<Employee>> getEmployeeWithId(@Query("id") String id);

    @GET("employee")
    Call<List<Employee>> getAllEmployees();

    @GET("employee/lunch/available")
    Call<List<Employee>> getFreeLunchEmployeeAt(@Query("date") String date);

    @GET("employee/dinner/available")
    Call<List<Employee>> getFreeDinnerEmployeeAt(@Query("date") String date);
}
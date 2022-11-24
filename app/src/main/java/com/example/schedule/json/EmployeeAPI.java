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

    @GET("employee/available")
    Call<List<Employee>> getFreeEmployeeAt(@Query("date") String date);
}
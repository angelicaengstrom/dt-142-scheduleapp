package com.example.schedule.json;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShiftAPI {
    @GET("shift/upcoming-shifts")
    Call<List<Shift2>> comingUserShift(@Query("id") String id, @Query("date") String date);

    @GET("shift")
    Call<List<Shift2>> allShiftAtDate(@Query("date") String date);

    @GET("shift")
    Call<List<Shift2>> allShift();

    @Headers("content-type: application/json")
    @PUT("shift/change-employee")
    Call<String> updateShift(@Body UpdateResponse updateResponse);
}
package com.example.schedule.json;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RequestAPI {
    @GET("request")
    Call<List<Request2>> getRequestTo(@Query("receiverId") String id);

    //DELETE REQUEST
    @Headers("content-type: application/json")
    @PUT("request")
    Call<String> deleteRequest(@Body UpdateResponse updateResponse);

    //SEND REQUEST
    @Headers("content-type: application/json")
    @POST("request")
    Call<String> sendRequest(@Body UpdateResponse updateResponse);

    @Headers("content-type: application/json")
    @PUT("shift/change-employee")
    Call<String> acceptRequest(@Body UpdateResponse updateResponse);
}

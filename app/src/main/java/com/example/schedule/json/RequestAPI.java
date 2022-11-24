package com.example.schedule.json;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestAPI {
    @GET("request")
    Call<List<Request2>> getRequestTo(@Query("receiverId") String id);
}

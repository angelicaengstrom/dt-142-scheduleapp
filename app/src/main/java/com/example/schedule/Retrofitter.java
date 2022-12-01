package com.example.schedule;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofitter<T> {
    private static final String IP = "31.209.47.252";
    private final Retrofit retrofit;

    Retrofitter(){
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + IP + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public T create(Class<T> interfaceClass){
        return retrofit.create(interfaceClass);
    }
}

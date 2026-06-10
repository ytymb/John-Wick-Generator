package com.example.android;


import android.app.Application;

import com.example.android.server.LoginApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StdApp extends Application {

    public static final String LOG_TAG = "student_app_tag";

    private static LoginApi api;
    private static StdApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://android-for-students.ru/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(LoginApi.class);
    }

    public static LoginApi getApi() {
        return api;
    }

    public static StdApp getInstance() {
        return instance;
    }
}
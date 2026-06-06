package com.example.android.server;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("coursework/login.php")
    Call<LoginResponse> login(
            @Field("lgn") String login,
            @Field("pwd") String password,
            @Field("g") String group
    );


}

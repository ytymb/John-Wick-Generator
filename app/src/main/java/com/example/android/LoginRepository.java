package com.example.android;


import android.util.Log;

import com.example.android.server.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository implements LoginContract.LoginRepository {

    @Override
    public void login(String login, String password, String group, LoginContract.OnLoginLoaded listener) {
        Log.d(StdApp.LOG_TAG, "LoginRepository: Запрос авторизации для " + login);

        StdApp.getApi().login(login, password, group).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse body = response.body();
                    Log.d(StdApp.LOG_TAG, "LoginRepository: result_code = " + body.getResultCode());

                    if (body.getResultCode() == 0 || body.getResultCode() == 1) {
                        listener.onSuccess(body);
                    } else {
                        String errorMsg = body.getMessageText() != null
                                ? body.getMessageText()
                                : "Ошибка авторизации";
                        listener.onFailed(errorMsg);
                    }
                } else {
                    listener.onFailed("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(StdApp.LOG_TAG, "LoginRepository onFailure: " + t.getMessage());

                if (t instanceof java.net.UnknownHostException || t instanceof java.io.IOException) {
                    listener.onFailed("Проблема с интернет-соединением. Проверьте настройки сети.");
                } else {
                    listener.onFailed("Ошибка сети: " + t.getMessage());
                }
            }
        });
    }
}
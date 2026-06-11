package com.example.android.Login;


import android.util.Log;

import com.example.android.Core.StdApp;
import com.example.android.server.LoginResponse;

import java.io.IOException;
import java.net.UnknownHostException;

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
                Log.d(StdApp.LOG_TAG, "LoginRepository: onResponse, code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse body = response.body();

                    Log.d(StdApp.LOG_TAG, "LoginRepository: result_code=" + body.getResultCode());
                    Log.d(StdApp.LOG_TAG, "LoginRepository: message_type=" + body.getMessageType());
                    Log.d(StdApp.LOG_TAG, "LoginRepository: message_text=" + body.getMessageText());
                    Log.d(StdApp.LOG_TAG, "LoginRepository: variant=" + body.getVariant());
                    Log.d(StdApp.LOG_TAG, "LoginRepository: title=" + body.getTitle());
                    Log.d(StdApp.LOG_TAG, "LoginRepository: task=" + body.getTask());

                    int resultCode = body.getResultCode();

                    if (resultCode == 1) {
                        Log.d(StdApp.LOG_TAG, "LoginRepository: Успешная авторизация");
                        listener.onSuccess(body);
                    } else if (resultCode == 0) {
                        if (body.getTitle() != null || body.getTask() != null || body.getVariant() != null) {
                            Log.d(StdApp.LOG_TAG, "LoginRepository: Успех (result_code=0 с данными)");
                            listener.onSuccess(body);
                        } else {
                            Log.e(StdApp.LOG_TAG, "LoginRepository: Ошибка авторизации (result_code=0 без данных)");
                            String errorMsg = body.getMessageText() != null
                                    ? body.getMessageText()
                                    : "Введены неверные данные";
                            listener.onFailed(errorMsg);
                        }
                    } else {
                        Log.e(StdApp.LOG_TAG, "LoginRepository: Ошибка авторизации (result_code=" + resultCode + ")");
                        String errorMsg = body.getMessageText() != null
                                ? body.getMessageText()
                                : "Ошибка авторизации";
                        listener.onFailed(errorMsg);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown";
                        Log.e(StdApp.LOG_TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(StdApp.LOG_TAG, "Can't read error body");
                    }
                    listener.onFailed("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(StdApp.LOG_TAG, "LoginRepository onFailure: " + t.getMessage());

                if (t instanceof UnknownHostException || t instanceof IOException) {
                    listener.onFailed("Проблема с интернет-соединением. Проверьте настройки сети.");
                } else {
                    listener.onFailed("Ошибка сети: " + t.getMessage());
                }
            }
        });
    }
}
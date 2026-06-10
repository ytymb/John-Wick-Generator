package com.example.android;

import android.content.Context;

import com.example.android.server.LoginResponse;

public class LoginContract {

    public interface LoginView {
        void showLoading();
        void hideLoading();
        void showError(String errorMessage);
        void showSuccess(LoginResponse response);
        Context getContext();
    }

    public interface LoginPresenter {
        void onLoginClicked(String login, String password, String group);
    }

    public interface LoginRepository {
        void login(String login, String password, String group, OnLoginLoaded listener);
    }

    public interface OnLoginLoaded {
        void onSuccess(LoginResponse response);
        void onFailed(String error);
    }
}
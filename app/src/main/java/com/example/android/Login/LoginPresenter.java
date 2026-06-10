package com.example.android;


import android.util.Log;

import com.example.android.server.LoginResponse;

public class LoginPresenter implements LoginContract.LoginPresenter {

    private LoginContract.LoginView view;
    private LoginContract.LoginRepository repository;

    public LoginPresenter(LoginContract.LoginView view) {
        this.view = view;
        this.repository = new LoginRepository();
    }

    @Override
    public void onLoginClicked(String login, String password, String group) {
        Log.d(StdApp.LOG_TAG, "LoginPresenter: onLoginClicked");

        if (login.isEmpty() || password.isEmpty() || group.isEmpty()) {
            view.showError(view.getContext().getString(R.string.error_fill_all_fields));
            return;
        }

        view.showLoading();

        repository.login(login, password, group, new LoginContract.OnLoginLoaded() {
            @Override
            public void onSuccess(LoginResponse response) {
                view.hideLoading();
                view.showSuccess(response);
            }

            @Override
            public void onFailed(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }
}
package com.example.android.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.Core.StdApp;
import com.example.android.Task.TaskActivity;
import com.example.android.Utils.ThemeUtilit;
import com.example.android.databinding.ActivityLoginBinding;
import com.example.android.server.LoginResponse;


public class LoginActivity extends AppCompatActivity implements LoginContract.LoginView {

    private ActivityLoginBinding binding;
    private LoginContract.LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(StdApp.LOG_TAG, "LoginActivity: onCreate");
        ThemeUtilit.applyTheme(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new LoginPresenter(this);

        binding.btnLogin.setOnClickListener(v -> {
            presenter.onLoginClicked(
                    binding.etLogin.getText().toString().trim(),
                    binding.etPassword.getText().toString().trim(),
                    binding.etGroup.getText().toString().trim()
            );
        });
    }

    @Override
    public void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnLogin.setEnabled(true);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(LoginResponse response) {
        Log.d(StdApp.LOG_TAG, "LoginActivity: Успешная авторизация, вариант " + response.getVariant());

        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("variant", response.getVariant());
        intent.putExtra("app_name", response.getTitle());
        intent.putExtra("task_description", response.getTask());
        intent.putExtra("data", response.getData());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(StdApp.LOG_TAG, "LoginActivity: onDestroy");
    }

    @Override
    public Context getContext() {
        return this;
    }
}
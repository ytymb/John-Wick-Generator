package com.example.android.Task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.Main.MainActivity;
import com.example.android.Core.StdApp;
import com.example.android.Utils.ThemeUtilit;
import com.example.android.databinding.ActivityTaskBinding;

public class TaskActivity extends AppCompatActivity {

    private ActivityTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(StdApp.LOG_TAG, "TaskActivity: onCreate");
        ThemeUtilit.applyTheme(this);

        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String variant = getIntent().getStringExtra("variant");
        String appName = getIntent().getStringExtra("app_name");
        String taskDescription = getIntent().getStringExtra("task_description");

        binding.tvVariant.setText("Вариант: " + variant);
        binding.tvAppName.setText("Приложение: " + appName);
        binding.tvTaskDescription.setText(taskDescription);

        binding.btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(StdApp.LOG_TAG, "TaskActivity: onDestroy");
    }
}
package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android.databinding.ActivityTaskBinding;

public class TaskActivity extends AppCompatActivity {

    private ActivityTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(StdApp.LOG_TAG, "TaskActivity: onCreate");

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
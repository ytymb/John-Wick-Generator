package com.example.android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements MainContract.MainView{

    private ActivityMainBinding binding;
    private MainContract.MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(StdApp.LOG_TAG, "MainActivity: onCreate");

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        presenter = new MainPresenter(this);

        binding.btnGenerate.setOnClickListener(v -> {
            presenter.onGenerateClicked(
                    binding.etWidth.getText().toString().trim(),
                    binding.etHeight.getText().toString().trim(),
                    binding.cbYoung.isChecked(),
                    binding.cbGrayscale.isChecked()
            );
        });
        binding.btnShare.setOnClickListener(v -> {
            presenter.onShareClicked();
        });

        binding.btnSave.setOnClickListener(v -> {
            presenter.onSaveClicked();
        });
    }

    @Override
    public void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnGenerate.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnGenerate.setEnabled(true);
    }

    @Override
    public void showImage(String url) {
        Log.d(StdApp.LOG_TAG, "MainActivity: Загружаем изображение: " + url);

        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.ivPhoto, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(StdApp.LOG_TAG, "Picasso: Изображение успешно загружено!");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(StdApp.LOG_TAG, "Picasso: Ошибка загрузки", e);
                        Log.e(StdApp.LOG_TAG, "Picasso: Тип ошибки: " + e.getClass().getName());
                        Log.e(StdApp.LOG_TAG, "Picasso: Сообщение: " + e.getMessage());

                        showError("Не удалось загрузить изображение. URL: " + url);
                    }
                });
    }


    @Override
    public void showShareDialog(String imageUrl) {
        // Загружаем bitmap для шаринга
        new Thread(() -> {
            Bitmap bitmap = PhotoUtils.downloadBitmap(imageUrl);
            if (bitmap != null) {
                runOnUiThread(() -> {
                    PhotoUtils.shareImage(this, bitmap);
                });
            } else {
                runOnUiThread(() -> {
                    showError("Не удалось загрузить изображение для шаринга");
                });
            }
        }).start();
    }

    @Override
    public void showSaveSuccess() {
        Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(StdApp.LOG_TAG, "MainActivity: onDestroy");
    }
}
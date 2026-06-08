package com.example.android;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class MainRepository implements MainContract.MainRepository {

    @Override
    public void loadImage(String url, MainContract.OnImageLoaded listener) {
        Log.d(StdApp.LOG_TAG, "MainRepository: Загрузка изображения по URL: " + url);

        if (url != null && !url.isEmpty()) {
            listener.onSuccess(url);
        } else {
            listener.onFailed("Некорректный URL");
        }
    }

    @Override
    public void saveImageToGallery(String imageUrl, MainContract.OnImageSaved listener) {
        Log.d(StdApp.LOG_TAG, "MainRepository: Сохранение изображения: " + imageUrl);

        new Thread(() -> {
            Bitmap bitmap = PhotoUtils.downloadBitmap(imageUrl);

            if (bitmap != null) {
                String imageName = "keanu_" + System.currentTimeMillis() + ".jpg";
                Uri savedUri = PhotoUtils.saveToGallery(StdApp.getInstance(), bitmap, imageName);

                if (savedUri != null) {
                    Log.d(StdApp.LOG_TAG, "MainRepository: Изображение сохранено: " + savedUri);
                    // Запускаем callback на главном потоке
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        listener.onSuccess();
                    });
                } else {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        listener.onFailed("Не удалось сохранить изображение");
                    });
                }
            } else {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    listener.onFailed("Не удалось загрузить изображение для сохранения");
                });
            }
        }).start();
    }
}
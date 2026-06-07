package com.example.android;

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
}
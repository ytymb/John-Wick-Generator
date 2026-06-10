//package com.example.android;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.util.Log;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class MainRepository implements MainContract.MainRepository {
//
//    @Override
//    public void loadImage(String url, MainContract.OnImageLoaded listener) {
//        Log.d(StdApp.LOG_TAG, "MainRepository: Загрузка изображения по URL: " + url);
//
//        new Thread(() -> {
//            HttpURLConnection connection = null;
//            try {
//                URL imageUrl = new URL(url);
//                connection = (HttpURLConnection) imageUrl.openConnection();
//                connection.setConnectTimeout(10000);
//                connection.setReadTimeout(10000);
//                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//
//                int responseCode = connection.getResponseCode();
//                String contentType = connection.getContentType();
//
//                Log.d(StdApp.LOG_TAG, "MainRepository: Response code: " + responseCode);
//                Log.d(StdApp.LOG_TAG, "MainRepository: Content-Type: " + contentType);
//
//                if (responseCode != HttpURLConnection.HTTP_OK) {
//                    Log.e(StdApp.LOG_TAG, "MainRepository: HTTP ошибка: " + responseCode);
//                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                        listener.onFailed("HTTP ошибка: " + responseCode + ". Проверьте VPN.");
//                    });
//                    return;
//                }
//
//                if (contentType == null || !contentType.contains("image")) {
//                    Log.e(StdApp.LOG_TAG, "MainRepository: Сервер вернул не изображение! Content-Type: " + contentType);
//                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                        listener.onFailed("Сервер вернул не изображение. Проверьте VPN и параметры.");
//                    });
//                    return;
//                }
//
//                // Проверяем, что можно декодировать как Bitmap
//                InputStream inputStream = connection.getInputStream();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeStream(inputStream, null, options);
//
//                if (options.outWidth == -1 || options.outHeight == -1) {
//                    Log.e(StdApp.LOG_TAG, "MainRepository: Не удалось декодировать изображение");
//                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                        listener.onFailed("Не удалось декодировать изображение");
//                    });
//                    return;
//                }
//
//                Log.d(StdApp.LOG_TAG, "MainRepository: Изображение валидно. Размер: " + options.outWidth + "x" + options.outHeight);
//
//                // Всё ОК — передаём URL для загрузки через Picasso
//                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                    listener.onSuccess(url);
//                });
//
//            } catch (Exception e) {
//                Log.e(StdApp.LOG_TAG, "MainRepository: Ошибка: " + e.getMessage(), e);
//                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                    listener.onFailed("Ошибка: " + e.getMessage());
//                });
//            } finally {
//                if (connection != null) {
//                    connection.disconnect();
//                }
//            }
//        }).start();
//    }
//
//    @Override
//    public void saveImageToGallery(String imageUrl, MainContract.OnImageSaved listener) {
//        Log.d(StdApp.LOG_TAG, "MainRepository: Сохранение изображения: " + imageUrl);
//
//        new Thread(() -> {
//            Bitmap bitmap = PhotoUtils.downloadBitmap(imageUrl);
//
//            if (bitmap != null) {
//                String imageName = "keanu_" + System.currentTimeMillis() + ".jpg";
//                Uri savedUri = PhotoUtils.saveToGallery(StdApp.getInstance(), bitmap, imageName);
//
//                if (savedUri != null) {
//                    Log.d(StdApp.LOG_TAG, "MainRepository: Изображение сохранено: " + savedUri);
//                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                        listener.onSuccess();
//                    });
//                } else {
//                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                        listener.onFailed("Не удалось сохранить изображение");
//                    });
//                }
//            } else {
//                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
//                    listener.onFailed("Не удалось загрузить изображение для сохранения");
//                });
//            }
//        }).start();
//    }
//}

package com.example.android;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class MainRepository implements MainContract.MainRepository {

    @Override
    public void loadImage(String url, MainContract.OnImageLoaded listener) {
        Log.d(StdApp.LOG_TAG, "MainRepository: Загрузка изображения по URL: " + url);

        // Просто передаём URL дальше - Glide сам загрузит
        // Не пытаемся декодировать здесь
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            listener.onSuccess(url);
        });
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
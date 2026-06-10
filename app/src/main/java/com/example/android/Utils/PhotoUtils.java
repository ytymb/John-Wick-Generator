package com.example.android.Utils;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import com.example.android.Core.StdApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class PhotoUtils {

    public static Bitmap downloadBitmap(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return android.graphics.BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(StdApp.LOG_TAG, "Error downloading bitmap", e);
            return null;
        }
    }

    public static Uri saveToGallery(Context context, Bitmap bitmap, String imageName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/JohnWickGenerator");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(StdApp.LOG_TAG, "Error saving to gallery", e);
                return null;
            }
        }

        return uri;
    }

    public static void shareImage(Context context, Bitmap bitmap) {
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs();

            File file = new File(cachePath, "keanu_share.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Поделиться фото"));

        } catch (IOException e) {
            Log.e(StdApp.LOG_TAG, "Error sharing image", e);
            Toast.makeText(context, "Ошибка при создании файла для шаринга", Toast.LENGTH_SHORT).show();
        }
    }

    public static String saveToCache(Context context, Bitmap bitmap) {
        try {
            // Создаем папку кэша, если её нет
            File cacheDir = context.getCacheDir();
            // Имя файла с текущим временем
            File file = new File(cacheDir, "hist_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream out = new FileOutputStream(file);
            // Сжимаем в JPEG с качеством 80%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();

            return file.getAbsolutePath(); // Возвращаем путь
        } catch (Exception e) {
            Log.e(StdApp.LOG_TAG, "Ошибка сохранения в кэш", e);
            return null;
        }
    }
}
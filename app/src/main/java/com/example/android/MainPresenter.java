package com.example.android;

import android.util.Log;

public class MainPresenter implements MainContract.MainPresenter {

    private MainContract.MainView view;
    private MainContract.MainRepository repository;
    private static String currentImageUrl;

    public MainPresenter(MainContract.MainView view) {
        this.view = view;
        this.repository = new MainRepository();
    }

    @Override
    public void onGenerateClicked(String widthStr, String heightStr, boolean isYoung, boolean isGrayscale) {
        Log.d(StdApp.LOG_TAG, "MainPresenter: onLoadClicked");

        if (widthStr.isEmpty()) {
            view.showError("Введите ширину");
            return;
        }

        int width;
        try {
            width = Integer.parseInt(widthStr);
            if (width <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            view.showError("Некорректная ширина");
            return;
        }

        StringBuilder url = new StringBuilder("https://placekeanu.com/").append(width);

        if (!heightStr.isEmpty()) {
            try {
                int height = Integer.parseInt(heightStr);
                if (height > 0) {
                    url.append("/").append(height);
                }
            } catch (NumberFormatException e) {
                Log.d(StdApp.LOG_TAG, "MainPresenter: Некорректная высота, игнорируем");
            }
        }

        String options = "";
        if (isYoung) options += "y";
        if (isGrayscale) options += "g";

        if (!options.isEmpty()) {
            url.append("/").append(options);
        }

        String finalUrl = url.toString();
        Log.d(StdApp.LOG_TAG, "MainPresenter: Сформирован URL: " + finalUrl);

        this.currentImageUrl = finalUrl;
        view.showLoading();

        repository.loadImage(finalUrl, new MainContract.OnImageLoaded() {
            @Override
            public void onSuccess(String imageUrl) {
                view.hideLoading();
                view.showImage(imageUrl);
            }

            @Override
            public void onFailed(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }

    @Override
    public void onShareClicked() {
        if (currentImageUrl == null || currentImageUrl.isEmpty()) {
            view.showError("Сначала загрузите фото");
            return;
        }
        view.showShareDialog(currentImageUrl);
    }

    @Override
    public void onSaveClicked() {
        if (currentImageUrl == null || currentImageUrl.isEmpty()) {
            view.showError("Сначала загрузите фото");
            return;
        }
        view.showLoading();
        repository.saveImageToGallery(currentImageUrl, new MainContract.OnImageSaved() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.showSaveSuccess();
            }

            @Override
            public void onFailed(String error) {
                view.hideLoading();
                view.showError(error);
            }
        });
    }
}
package com.example.android.Main;

import android.util.Log;

import com.example.android.R;
import com.example.android.Core.StdApp;

import java.util.ArrayList;
import java.util.List;

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
        Log.d(StdApp.LOG_TAG, "MainPresenter: onGenerateClicked");

        if (widthStr.isEmpty()) {
            view.showError(view.getContext().getString(R.string.error_fill_all_fields));
            return;
        }

        int width;
        try {
            width = Integer.parseInt(widthStr);
        } catch (NumberFormatException e) {
            view.showError(view.getContext().getString(R.string.error_number_format));
            return;
        }

        if (width < 10 || width > 5000) {
            view.showError(view.getContext().getString(R.string.error_invalid_width));
            return;
        }

        int height = width;
        if (!heightStr.isEmpty()) {
            try {
                height = Integer.parseInt(heightStr);
            } catch (NumberFormatException e) {
                view.showError(view.getContext().getString(R.string.error_number_format));
                return;
            }
            if (height < 10 || height > 5000) {
                view.showError(view.getContext().getString(R.string.error_invalid_height));
                return;
            }
        }

        StringBuilder url = new StringBuilder("https://placekeanu.com/").append(width);

        if (!heightStr.isEmpty()) {
            url.append("/").append(height);
        }

        List<String> options = new ArrayList<>();
        if (isYoung) options.add("y");
        if (isGrayscale) options.add("g");

        if (!options.isEmpty()) {
            url.append("/").append(String.join(".", options));
        }

        currentImageUrl = url.toString();
        Log.d(StdApp.LOG_TAG, "MainPresenter: Сформирован URL: " + currentImageUrl);

        view.showLoading();

        repository.loadImage(currentImageUrl, new MainContract.OnImageLoaded() {
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
package com.example.android;

public class MainContract {

    public interface MainView {
        void showLoading();
        void hideLoading();
        void showImage(String url);
        void showError(String message);
    }

    public interface MainPresenter {
        void onLoadClicked(String width, String height, boolean isYoung, boolean isGrayscale);
    }

    public interface MainRepository {
        void loadImage(String url, OnImageLoaded listener);
    }

    public interface OnImageLoaded {
        void onSuccess(String imageUrl);
        void onFailed(String error);
    }
}
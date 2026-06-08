package com.example.android;

public class MainContract {

    public interface MainView {
        void showLoading();
        void hideLoading();
        void showImage(String url);
        void showError(String message);
        void showShareDialog(String imageUrl);
        void showSaveSuccess();
    }

    public interface MainPresenter {
        void onGenerateClicked(String width, String height, boolean isYoung, boolean isGrayscale);
        void onShareClicked();
        void onSaveClicked();
    }

    public interface MainRepository {
        void loadImage(String url, OnImageLoaded listener);
        void saveImageToGallery(String imageUrl, OnImageSaved listener);
    }

    public interface OnImageLoaded {
        void onSuccess(String imageUrl);
        void onFailed(String error);
    }

    public interface OnImageSaved {
        void onSuccess();
        void onFailed(String error);
    }
}
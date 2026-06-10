package com.example.android.History;

public class HistoryItem {
    private String filePath;

    public HistoryItem(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
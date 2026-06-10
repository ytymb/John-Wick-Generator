package com.example.android.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static HistoryManager instance;
    private List<HistoryItem> historyList = new ArrayList<>();
    private static final int MAX_HISTORY = 21;

    public static HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    public void addItem(HistoryItem item) {
        historyList.add(0, item);

        while (historyList.size() > MAX_HISTORY) {
            historyList.remove(historyList.size() - 1);
        }
    }

    public List<HistoryItem> getHistoryList() {
        return historyList;
    }
    public void clearHistory() {
        historyList.clear();
    }
}
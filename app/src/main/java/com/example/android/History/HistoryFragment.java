package com.example.android.History;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;

import java.io.File;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;
    private Button btnClearHistory;
    private HistoryAdapter adapter;
    private HistoryManager historyManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvHistory = view.findViewById(R.id.rvHistory);
        tvEmptyHistory = view.findViewById(R.id.tvClearHistory);
        btnClearHistory = view.findViewById(R.id.btnClearHistory);
        historyManager = HistoryManager.getInstance();

        adapter = new HistoryAdapter();
        rvHistory.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvHistory.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            openFullImageDialog(item);
        });

        btnClearHistory.setOnClickListener(v -> {
            clearHistory();
        });

        loadHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        List<HistoryItem> history = historyManager.getHistoryList();
        adapter.setItems(history);
        checkEmpty();
    }

    private void clearHistory() {
        for (HistoryItem item : historyManager.getHistoryList()) {
            File file = new File(item.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }

        historyManager.getHistoryList().clear();
        adapter.setItems(historyManager.getHistoryList());
        checkEmpty();

        Toast.makeText(getContext(), "История очищена", Toast.LENGTH_SHORT).show();
    }

    private void checkEmpty() {
        if (historyManager.getHistoryList().isEmpty()) {
            rvHistory.setVisibility(View.GONE);
            tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            tvEmptyHistory.setVisibility(View.GONE);
        }
    }

    private void openFullImageDialog(HistoryItem item) {
        File file = new File(item.getFilePath());
        if (!file.exists()) {
            Toast.makeText(getContext(), "Файл не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmap == null) {
            Toast.makeText(getContext(), "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageView imageView = new ImageView(requireContext());
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        new AlertDialog.Builder(requireContext())
                .setView(imageView)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
package com.example.android.Generator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.caverock.androidsvg.SVG;
import com.example.android.Main.MainContract;
import com.example.android.Main.MainPresenter;
import com.example.android.R;
import com.example.android.Core.StdApp;
import com.example.android.Utils.PhotoUtils;
import com.example.android.databinding.FragmentGeneratorBinding;
import com.example.android.History.HistoryItem;
import com.example.android.History.HistoryManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeneratorFragment extends Fragment implements MainContract.MainView {

    private HistoryManager historyManager;
    private Bitmap currentBitmap;
    private FragmentGeneratorBinding binding;
    private MainContract.MainPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGeneratorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(StdApp.LOG_TAG, "GeneratorFragment: onViewCreated");

        presenter = new MainPresenter(this);

        binding.btnGenerate.setOnClickListener(v -> {
            presenter.onGenerateClicked(
                    binding.etWidth.getText().toString().trim(),
                    binding.etHeight.getText().toString().trim(),
                    binding.cbYoung.isChecked(),
                    binding.cbGrayscale.isChecked()
            );
        });

        binding.btnShare.setOnClickListener(v -> {
            if (currentBitmap == null) {
                showError("Сначала загрузите фото");
                return;
            }
            PhotoUtils.shareImage(requireContext(), currentBitmap);
        });

        binding.btnSave.setOnClickListener(v -> {
            if (currentBitmap == null) {
                showError("Сначала загрузите фото");
                return;
            }

            new Thread(() -> {
                String imageName = "keanu_" + System.currentTimeMillis() + ".jpg";
                android.net.Uri uri = PhotoUtils.saveToGallery(requireContext(), currentBitmap, imageName);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (uri != null) {
                            Toast.makeText(requireContext(), R.string.success_saved, Toast.LENGTH_LONG).show();
                        } else {
                            showError("Не удалось сохранить изображение");
                        }
                    });
                }
            }).start();
        });

        historyManager = HistoryManager.getInstance();
    }

    @Override
    public void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnGenerate.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnGenerate.setEnabled(true);
    }

    @Override
    public void showImage(String url) {
        Log.d(StdApp.LOG_TAG, "GeneratorFragment: Загружаем SVG: " + url);
        String currentUrl = url;

        binding.tvPlaceholder.setVisibility(View.VISIBLE);
        binding.tvPlaceholder.setText("Загрузка...");

        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                InputStream inputStream = connection.getInputStream();
                SVG svg = SVG.getFromInputStream(inputStream);

                float svgWidth = svg.getDocumentWidth();
                float svgHeight = svg.getDocumentHeight();

                if (svgWidth <= 0 || svgHeight <= 0) {
                    svgWidth = 1000;
                    svgHeight = 1000;
                }

                int width = (int) svgWidth;
                int height = (int) svgHeight;

                android.graphics.Picture picture = svg.renderToPicture(width, height);
                android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                        width, height, android.graphics.Bitmap.Config.ARGB_8888);
                android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
                canvas.drawPicture(picture);

                if (url.contains("g")) {
                    bitmap = makeGrayscale(bitmap);
                    Log.d(StdApp.LOG_TAG, "Применён grayscale фильтр");
                }

                currentBitmap = bitmap;

                final android.graphics.Bitmap finalBitmap = bitmap;
                requireActivity().runOnUiThread(() -> {
                    binding.ivPhoto.setImageBitmap(finalBitmap);
                    binding.tvPlaceholder.setVisibility(View.GONE);
                    Log.d(StdApp.LOG_TAG, "SVG отображён как Bitmap!");
                    addToHistory();
                });

                connection.disconnect();

            } catch (Exception e) {
                Log.e(StdApp.LOG_TAG, "Ошибка загрузки SVG", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.tvPlaceholder.setText("Ошибка: " + e.getMessage());
                        binding.tvPlaceholder.setVisibility(View.VISIBLE);
                    });
                }
            }
        }).start();
    }

    private Bitmap makeGrayscale(Bitmap source) {
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        android.graphics.ColorMatrix matrix = new android.graphics.ColorMatrix();
        matrix.setSaturation(0);

        paint.setColorFilter(new android.graphics.ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(source, 0, 0, paint);

        return result;
    }

    @Override
    public void showShareDialog(String imageUrl) {
        new Thread(() -> {
            Bitmap bitmap = PhotoUtils.downloadBitmap(imageUrl);
            if (bitmap != null) {
                requireActivity().runOnUiThread(() -> {
                    PhotoUtils.shareImage(requireContext(), bitmap);
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    showError("Не удалось загрузить изображение чтобы поделиться");
                });
            }
        }).start();
    }

    @Override
    public void showSaveSuccess() {
        Toast.makeText(requireContext(), getString(R.string.success_saved), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(StdApp.LOG_TAG, "GeneratorFragment: onDestroyView");
        binding = null;
    }

    private void addToHistory() {
        if (currentBitmap == null) {
            Log.e(StdApp.LOG_TAG, "Нечего добавлять в историю - bitmap null");
            return;
        }

        new Thread(() -> {
            String filePath = PhotoUtils.saveToCache(requireContext(), currentBitmap);

            if (filePath != null) {
                HistoryItem item = new HistoryItem(filePath);

                historyManager.addItem(item);

                Log.d(StdApp.LOG_TAG, "Фото добавлено в историю: " + filePath);
            } else {
                Log.e(StdApp.LOG_TAG, "Не удалось сохранить фото в кэш");
            }
        }).start();
    }
}
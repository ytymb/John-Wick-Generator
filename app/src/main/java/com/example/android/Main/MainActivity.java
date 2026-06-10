package com.example.android.Main;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.android.Generator.GeneratorFragment;
import com.example.android.R;
import com.example.android.Utils.ThemeUtilit;
import com.example.android.ViewPager.ViewPagerAdapter;
import com.example.android.databinding.ActivityMainBinding;
import com.example.android.History.HistoryFragment;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtilit.applyTheme(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new GeneratorFragment(), "Генератор");
        adapter.addFragment(new HistoryFragment(), "История");

        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        updateThemeIcon(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            ThemeUtilit.toggleTheme(this);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateThemeIcon(Menu menu) {
        MenuItem themeItem = menu.findItem(R.id.action_theme);
        if (themeItem != null) {
            int mode = AppCompatDelegate.getDefaultNightMode();
            boolean isDark = mode == AppCompatDelegate.MODE_NIGHT_YES;
            themeItem.setIcon(isDark ? R.drawable.ic_black_theme : R.drawable.ic_white_theme);
        }
    }
}
package com.honley.kitaitu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.honley.kitaitu.databinding.ActivityMainBinding;
import com.honley.kitaitu.fragment.Appeal.AppealFragment;
import com.honley.kitaitu.fragment.Main.MainFragment;
import com.honley.kitaitu.fragment.News.NewsFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        if (token == null || token.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainer.getId(), new MainFragment())
                .commit();
        binding.navigation.setSelectedItemId(R.id.main);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.main, new MainFragment());
        fragmentMap.put(R.id.news, new NewsFragment());
        fragmentMap.put(R.id.appeal, new AppealFragment());

        binding.navigation.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left
            );

            transaction.replace(binding.fragmentContainer.getId(), fragment).commit();

            return true;
        });
    }
}

package com.honley.kitaitu;

import static com.honley.kitaitu.util.HttpRequest.createAvatar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.honley.kitaitu.databinding.ActivitySettingsBinding;
import com.honley.kitaitu.util.HttpRequest;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPref = getSharedPreferences("data", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        binding.back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.image.setOnClickListener(view -> pickImageLauncher.launch("image/*"));

        binding.send.setOnClickListener(view -> {
            if (selectedImageUri != null) {
                createAvatar(token, selectedImageUri, SettingActivity.this, new HttpRequest.ApiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(SettingActivity.this, "Аватарка успешно установлена", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println(error);
                        Toast.makeText(SettingActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.image.setImageURI(uri);
                }
            }
    );
}
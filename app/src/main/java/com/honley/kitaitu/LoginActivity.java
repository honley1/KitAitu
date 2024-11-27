package com.honley.kitaitu;

import static com.honley.kitaitu.util.HttpRequest.loginPhone;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import com.honley.kitaitu.databinding.ActivityLoginBinding;
import com.honley.kitaitu.util.HttpRequest;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.continueButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.button_on_click_background);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.button_background);
                    break;
            }
            return false;
        });

        binding.continueButton.setOnClickListener(view -> openDialog());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void openDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirm_phone);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button confirmButton = dialog.findViewById(R.id.btnConfirm);
        Button cancelButton = dialog.findViewById(R.id.btnChange);

        confirmButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.button_on_click_background);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.button_background);
                    break;
            }
            return false;
        });

        cancelButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.button_on_click_background);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.button_background);
                    break;
            }
            return false;
        });

        confirmButton.setOnClickListener(v -> {
            String phone = binding.phone.getText().toString();

            if (!phone.isEmpty()) {
                loginPhone(phone, new HttpRequest.ApiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(LoginActivity.this, "Введите SMS-код, который был отправлен на ваш номер телефона.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, AuthActivity.class);
                                intent.putExtra("phone", phone);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Номер недействителен", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Error parsing JSON object", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LoginActivity.this, "Номер недействителен", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Укажите номер", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.setCancelable(false);
        dialog.show();
    }
}

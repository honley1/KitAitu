package com.honley.kitaitu;

import static com.honley.kitaitu.util.HttpRequest.loginSmsCode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.honley.kitaitu.databinding.ActivityRegisterBinding;
import com.honley.kitaitu.util.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EditText etDigit1 = binding.etDigit1;
        EditText etDigit2 = binding.etDigit2;
        EditText etDigit3 = binding.etDigit3;
        EditText etDigit4 = binding.etDigit4;
        EditText etDigit5 = binding.etDigit5;
        EditText etDigit6 = binding.etDigit6;

        addTextWatchers();

        binding.loginButton.setOnTouchListener((v, event) -> {
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

        binding.loginButton.setOnClickListener(view -> {
            Intent intent = getIntent();
            String phone = intent.getStringExtra("phone");

            if (!isFieldBlanked(
                    etDigit1.getText().toString(),
                    etDigit2.getText().toString(),
                    etDigit3.getText().toString(),
                    etDigit4.getText().toString(),
                    etDigit5.getText().toString(),
                    etDigit6.getText().toString())) {

                String smsCode = etDigit1.getText().toString() + etDigit2.getText().toString() + etDigit3.getText().toString() + etDigit4.getText().toString() + etDigit5.getText().toString() + etDigit6.getText().toString();

                loginSmsCode(phone, smsCode, new HttpRequest.ApiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                String token = new JSONObject(jsonObject.getString("data")).getString("token");

                                Toast.makeText(AuthActivity.this, "Вы успешно зашли в аккаунт", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("token", token);
                                editor.apply();

                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(AuthActivity.this, "SMS-код недействителен", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(AuthActivity.this, "Error parsing JSON object", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AuthActivity.this, "Не получилось войти в аккаунт", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(AuthActivity.this, "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTextWatchers() {
        binding.etDigit1.addTextChangedListener(createTextWatcher(binding.etDigit2));
        binding.etDigit2.addTextChangedListener(createTextWatcher(binding.etDigit3));
        binding.etDigit3.addTextChangedListener(createTextWatcher(binding.etDigit4));
        binding.etDigit4.addTextChangedListener(createTextWatcher(binding.etDigit5));
        binding.etDigit5.addTextChangedListener(createTextWatcher(binding.etDigit6));
    }

    private TextWatcher createTextWatcher(EditText nextEditText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && nextEditText != null) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private boolean isFieldBlanked(String digit1, String digit2, String digit3, String digit4, String digit5, String digit6) {
        return digit1.isEmpty() || digit2.isEmpty() || digit3.isEmpty() || digit4.isEmpty() || digit5.isEmpty() || digit6.isEmpty();
    }
}
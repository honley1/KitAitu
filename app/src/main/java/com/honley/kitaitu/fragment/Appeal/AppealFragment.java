package com.honley.kitaitu.fragment.Appeal;

import static com.honley.kitaitu.util.HttpRequest.createAppeal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.honley.kitaitu.R;
import com.honley.kitaitu.databinding.FragmentAppealBinding;
import com.honley.kitaitu.fragment.Main.MainFragment;
import com.honley.kitaitu.util.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AppealFragment extends Fragment {

    private FragmentAppealBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppealBinding.inflate(inflater, container, false);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        binding.send.setOnTouchListener((v, event) -> {
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

        binding.send.setOnClickListener(view -> {
            String content = binding.content.getText().toString();

            if (content.isEmpty()) {
                Toast.makeText(requireActivity(), "Поле не может быть пустым", Toast.LENGTH_SHORT).show();
            }
            else if (content.length() < 3) {
                Toast.makeText(requireActivity(), "Пожалуйста, добавьте немного больше информации", Toast.LENGTH_SHORT).show();
            }
            else if (content.length() > 150) {
                Toast.makeText(requireActivity(), "Превышен лимит символов. Уменьшите текст до 150 символов", Toast.LENGTH_SHORT).show();
            }
            else {
                createAppeal(token, content, new HttpRequest.ApiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(requireActivity(), "Ваше обращение отправлено успешно!", Toast.LENGTH_SHORT).show();

                                binding.content.setText("");

                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                fragmentTransaction.setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_right,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_left
                                );

                                fragmentTransaction.replace(R.id.fragment_container, new MainFragment());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            } else {
                                Toast.makeText(requireActivity(), "Не удалось отправить обращение.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(requireActivity(), "Error parsing JSON object", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(requireActivity(), "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return binding.getRoot();
    }
}

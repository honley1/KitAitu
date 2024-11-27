package com.honley.kitaitu.fragment.Main;

import static android.content.Context.MODE_PRIVATE;
import static com.honley.kitaitu.util.HttpRequest.getFile;
import static com.honley.kitaitu.util.HttpRequest.getStudentCard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.honley.kitaitu.LoginActivity;
import com.honley.kitaitu.R;
import com.honley.kitaitu.SettingActivity;
import com.honley.kitaitu.databinding.FragmentMainBinding;
import com.honley.kitaitu.fragment.Announcement.AnnouncementFragment;
import com.honley.kitaitu.fragment.Appeal.AppealFragment;
import com.honley.kitaitu.fragment.News.NewsFragment;
import com.honley.kitaitu.util.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        binding.studentCard.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.edittext_on_click_background);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.edittext_background);
                    break;
            }
            return false;
        });

        binding.studentCard.setOnClickListener(view -> openDialog());

        binding.appeal.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.button_background);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.button_on_click_background);
                    break;
            }
            return false;
        });

        binding.news.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.on_click_main_color_button);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.main_color_button);
                    break;
            }
            return false;
        });

        binding.announcement.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.on_click_main_color_button);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.main_color_button);
                    break;
            }
            return false;
        });

        binding.news.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left
            );

            fragmentTransaction.replace(R.id.fragment_container, new NewsFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        binding.appeal.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left
            );

            fragmentTransaction.replace(R.id.fragment_container, new AppealFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        binding.announcement.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left
            );

            fragmentTransaction.replace(R.id.fragment_container, new AnnouncementFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        binding.avatar.setOnClickListener(view -> {
            showFullScreenImage();
        });

        binding.settings.setOnClickListener(view -> startActivity(new Intent(requireActivity(), SettingActivity.class)));

        try {
            String studentCard = sharedPref.getString("studentCard", null);
            JSONObject studentCardJson = new JSONObject(studentCard);

            String fileName = studentCardJson.getString("phone") + ".png";

            String lastNameDef = studentCardJson.getString("lastName");
            String firstNameDef = studentCardJson.getString("firstName");
            String middleNameDef = studentCardJson.getString("middleName");

            String fullNameDef = lastNameDef + " " + firstNameDef + " " + middleNameDef;

            String groupDef = studentCardJson.getString("group");

            binding.fullName.setText(fullNameDef);
            binding.group.setText(groupDef);
        } catch (JSONException e) {
            System.out.println(e);
        }

        getStudentCard(token, new HttpRequest.ApiCallback() {
            @Override
            public void onSuccess(String result) {
                if (!isAdded()) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("studentCard", data.toString());
                    editor.apply();

                    String fileName = data.getString("phone") + ".png";

                    if (!fileName.equals("null")) {
                        getFile(fileName, requireActivity(), new HttpRequest.ApiCallbackFile() {
                            @Override
                            public void onSuccess(File result) {
                                Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
                                binding.avatar.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onError(String error) {
                            }
                        });
                    }

                    String lastName = data.getString("lastName");
                    String firstName = data.getString("firstName");
                    String middleName = data.getString("middleName");

                    String fullName = lastName + " " + firstName + " " + middleName;
                    String group = data.getString("group");

                    binding.fullName.setText(fullName);
                    binding.group.setText(group);
                } catch (JSONException e) {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), "Error parsing JSON object", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) {
                    return;
                }
                if (error.contains("Unauthorized")) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void openDialog() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_student_card);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView avatar = dialog.findViewById(R.id.avatar);

        TextView fullNameInfo = dialog.findViewById(R.id.fullNameInfo);
        TextView groupInfo = dialog.findViewById(R.id.groupInfo);
        TextView dobInfo = dialog.findViewById(R.id.dobInfo);
        TextView iinInfo = dialog.findViewById(R.id.iinInfo);
        TextView sexInfo = dialog.findViewById(R.id.sexInfo);

        Button shareButton = dialog.findViewById(R.id.share);
        Button cancelButton = dialog.findViewById(R.id.cancel);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        String studentCard = sharedPref.getString("studentCard", null);

        try {
            JSONObject data = new JSONObject(studentCard);

            String fileName = data.getString("avatar");

            if (!fileName.equals("null")) {
                getFile(fileName, requireActivity(), new HttpRequest.ApiCallbackFile() {
                    @Override
                    public void onSuccess(File result) {
                        Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
                        avatar.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(requireActivity(), "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            String lastName = data.getString("lastName");
            String firstName = data.getString("firstName");
            String middleName = data.getString("middleName");

            String fullName = lastName + "\n" + firstName + "\n" + middleName;

            String group = data.getString("group");

            String dob = data.getString("dob");

            String iin = data.getString("iin");

            String sex = data.getString("sex");

            fullNameInfo.setText(fullName);
            groupInfo.setText(group);
            dobInfo.setText(dob);
            iinInfo.setText(iin);
            sexInfo.setText(sex);
        } catch (JSONException e) {
            Toast.makeText(requireActivity(), "Error parsing JSON object", Toast.LENGTH_SHORT).show();
        }

        shareButton.setOnTouchListener((v, event) -> {
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

        shareButton.setOnClickListener(v -> {
            String fullName = fullNameInfo.getText().toString();
            String group = groupInfo.getText().toString();
            String dob = dobInfo.getText().toString();
            String iin = iinInfo.getText().toString();
            String sex = sexInfo.getText().toString();

            String shareText = "Student Card Information:\n\n" +
                    "ФИО: " + fullName + "\n" +
                    "Группа: " + group + "\n" +
                    "Дата рождения: " + dob + "\n" +
                    "ИИН: " + iin + "\n" +
                    "Пол: " + sex;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Поделится"));
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.setCancelable(false);
        dialog.show();
    }

    private void showFullScreenImage() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_full_screen_image);
        dialog.setCancelable(true);

        ImageView fullScreenImageView = dialog.findViewById(R.id.fullScreenImageView);

        Drawable drawable = binding.avatar.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            fullScreenImageView.setImageBitmap(bitmap);
            fullScreenImageView.setBackgroundResource(R.drawable.rounded_corners);
        } else {
            Toast.makeText(getContext(), "Изображение недоступно", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        fullScreenImageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}

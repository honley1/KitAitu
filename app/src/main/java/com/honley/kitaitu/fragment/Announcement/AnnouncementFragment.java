package com.honley.kitaitu.fragment.Announcement;

import static android.content.Context.MODE_PRIVATE;
import static com.honley.kitaitu.util.HttpRequest.getAnnouncements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.honley.kitaitu.LoginActivity;
import com.honley.kitaitu.databinding.FragmentAnnouncementBinding;
import com.honley.kitaitu.util.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementFragment extends Fragment {

    private FragmentAnnouncementBinding binding;
    private List<AnnouncementItem> announcementList;
    private AnnouncementAdapter announcementAdapter;

    private ShimmerFrameLayout shimmerFrameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnnouncementBinding.inflate(inflater, container, false);

        shimmerFrameLayout = binding.shimmerViewContainer;

        shimmerFrameLayout.startShimmer();

        loadData();

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("data", MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        binding.announcementRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        announcementList = new ArrayList<>();

        getAnnouncements(token, new HttpRequest.ApiCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(String result) {
                if (!isAdded()) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject newsObject = dataArray.getJSONObject(i);

                            String title = newsObject.getString("title");
                            String content = newsObject.getString("content");
                            String createdAt = newsObject.getString("createdAt");

                            AnnouncementItem announcementItem = new AnnouncementItem(title, content, createdAt);
                            announcementList.add(announcementItem);
                        }

                        requireActivity().runOnUiThread(() -> announcementAdapter.notifyDataSetChanged());
                    } else {
                        Toast.makeText(requireActivity(), "Не удалось получить новости.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    if (!isAdded()) {
                        return;
                    }
                    Toast.makeText(requireActivity(), "Error parsing JSON object", Toast.LENGTH_SHORT).show();
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
        announcementAdapter = new AnnouncementAdapter(getActivity(), announcementList);
        binding.announcementRecyclerView.setAdapter(announcementAdapter);

        return binding.getRoot();
    }

    private void loadData() {
        new Handler().postDelayed(() -> {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }, 3000);
    }
}

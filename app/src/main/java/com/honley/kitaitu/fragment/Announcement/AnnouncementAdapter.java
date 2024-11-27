package com.honley.kitaitu.fragment.Announcement;

import static android.content.Context.MODE_PRIVATE;

import static com.honley.kitaitu.fragment.News.NewsAdapter.formatDate;
import static com.honley.kitaitu.util.HttpRequest.getAnnouncementByTitle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.honley.kitaitu.R;
import com.honley.kitaitu.util.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>{

    private final List<AnnouncementItem> announcementList;
    private final Context context;

    public AnnouncementAdapter(Context context, List<AnnouncementItem> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public AnnouncementAdapter.AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        SharedPreferences sharedPref = context.getSharedPreferences("data", MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        AnnouncementItem announcementItem = announcementList.get(position);

        holder.title.setText(announcementItem.getTitle());
        holder.content.setText(announcementItem.getContent());
        holder.createdAt.setText(formatDate(announcementItem.getCreatedAt()));

        holder.detailsButton.setOnClickListener(view -> openDialog(announcementItem.getTitle(), announcementItem.getContent(), announcementItem.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView createdAt;
        ImageButton detailsButton;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.announcementTitle);
            content = itemView.findViewById(R.id.announcementContent);
            createdAt = itemView.findViewById(R.id.announcementCreatedAt);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    private void openDialog(String title, String content, String createdAt) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_announcement);

        TextView announcementTitle = dialog.findViewById(R.id.announcementTitle);
        TextView announcementContent = dialog.findViewById(R.id.announcementContent);
        TextView announcementCreatedAt = dialog.findViewById(R.id.announcementCreatedAt);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        announcementTitle.setText(title);
        announcementContent.setText(content);
        announcementCreatedAt.setText(formatDate(createdAt));

        dialog.show();
    }
}

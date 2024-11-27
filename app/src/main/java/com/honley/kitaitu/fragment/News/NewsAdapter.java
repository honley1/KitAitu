package com.honley.kitaitu.fragment.News;

import static android.content.Context.MODE_PRIVATE;
import static com.honley.kitaitu.util.HttpRequest.getFile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.honley.kitaitu.R;
import com.honley.kitaitu.util.HttpRequest;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<NewsItem> newsList;
    private final Context context;

    public NewsAdapter(Context context, List<NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        SharedPreferences sharedPref = context.getSharedPreferences("data", MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        NewsItem newsItem = newsList.get(position);

        getFile(newsItem.getImage(), context, new HttpRequest.ApiCallbackFile() {
            @Override
            public void onSuccess(File result) {
                Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
                holder.imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onError(String error) {
            }
        });

        holder.title.setText(newsItem.getTitle());
        holder.content.setText(newsItem.getContent());
        holder.createdAt.setText(formatDate(newsItem.getCreatedAt()));

        holder.detailsButton.setOnClickListener(view -> openDialog(newsItem.getImage(), newsItem.getTitle(), newsItem.getContent(), newsItem.getCreatedAt()));
    }


    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView content;
        TextView createdAt;
        ImageButton detailsButton;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.newsImage);
            title = itemView.findViewById(R.id.newsTitle);
            content = itemView.findViewById(R.id.newsContent);
            createdAt = itemView.findViewById(R.id.newsCreatedAt);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }
    }

    public static String formatDate(String isoDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        try {
            Date date = isoFormat.parse(isoDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    private void openDialog(String fileName, String title, String content, String createdAt) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_news);

        ImageView newsImage = dialog.findViewById(R.id.newsImage);
        TextView newsTitle = dialog.findViewById(R.id.newsTitle);
        TextView newsContent = dialog.findViewById(R.id.newsContent);
        TextView newsCreatedAt = dialog.findViewById(R.id.newsCreatedAt);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getFile(fileName, context, new HttpRequest.ApiCallbackFile() {
            @Override
            public void onSuccess(File result) {
                Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
                newsImage.setImageBitmap(bitmap);
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });

        newsTitle.setText(title);
        newsContent.setText(content);
        newsCreatedAt.setText(createdAt);
//        getNewsByTitle(token, title, new HttpRequest.ApiCallback() {
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    JSONObject data = new JSONObject(new JSONObject(result).getString("data"));
//
//
//                } catch (JSONException e){
//                    Toast.makeText(context, "Error parsing JSON object", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//            }
//        });

        dialog.show();
    }
}
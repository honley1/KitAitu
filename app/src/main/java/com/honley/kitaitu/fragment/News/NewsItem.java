package com.honley.kitaitu.fragment.News;

public class NewsItem {
    private String image;
    private String title;
    private String content;
    private String createdAt;

    public NewsItem(String title, String image, String content, String createdAt) {
        this.title = title;
        this.image = image;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

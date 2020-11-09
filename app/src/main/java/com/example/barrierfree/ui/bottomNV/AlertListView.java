package com.example.barrierfree.ui.bottomNV;

import android.graphics.drawable.Drawable;

public class AlertListView {
    private String title;
    private String content;
    private String date;
    private Drawable image;

    public AlertListView() {
    }

    public Drawable getImage() { return image; }

    public void setImage(Drawable image) { this.image = image; }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}

package com.example.barrierfree.ui.bottomNV;

public class AlertListView {
    public String title;
    public String content;
    public String date;
    public int image;

    public AlertListView(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public AlertListView() {
    }


    public int getImage() { return image; }

    public void setImage(int image) { this.image = this.image; }

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

package com.example.hyeminj.syolo;

public class image_item {
    private String image;
    private String title;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public image_item(String id, String image, String title) {
        this.id = id;
        this.image = image;
        this.title = title;
    }
}
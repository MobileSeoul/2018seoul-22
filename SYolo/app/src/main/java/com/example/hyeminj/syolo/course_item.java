package com.example.hyeminj.syolo;

public class course_item {
    private String image;
    private String title;
    private String content;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public course_item(String id, String content, String image, String title) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.title = title;
    }
}

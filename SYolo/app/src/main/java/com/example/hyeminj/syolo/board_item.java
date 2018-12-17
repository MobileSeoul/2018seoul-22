package com.example.hyeminj.syolo;

public class board_item {
    String id;
    String content;
    String date;
    String title;
    String data_key;
    String real_id;
    String login;
    public board_item(String id, String content, String date, String title, String data_key,String real_id, String login) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.title = title;
        this.data_key = data_key;
        this.real_id = real_id;
        this.login = login;
    }

    public String getReal_id(){
        return real_id;
    }
    public void setReal_id(String real_id){
        this.real_id = real_id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData_key() {
        return data_key;
    }

    public void setData_key(String data_key) {
        this.data_key = data_key;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getLogin(){
        return login;
    }
    public void getLogin(String login){
        this.login = login;
    }
}
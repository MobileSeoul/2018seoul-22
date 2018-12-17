package com.example.hyeminj.syolo;

public class comment_item {
    String id;
    String content;
    String date;
    String data_key_now;
    String real_id;
    String login_type;
    public comment_item(String id, String content, String date, String data_key_now, String real_id, String login_type) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.data_key_now = data_key_now;
        this.real_id = real_id;
        this.login_type = login_type;
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

    public String getLogin_type(){
        return login_type;
    }

    public void setLogin_type(String login_type){
        this.login_type = login_type;
    }
}
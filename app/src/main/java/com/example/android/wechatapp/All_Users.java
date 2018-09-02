package com.example.android.wechatapp;

public class All_Users {

    private String id;
    private String user_name ;
    private String user_status ;
    private String user_thumb_image;

    public All_Users(String id, String user_name, String user_status,String user_thumb_image) {

        this.id = id;
        this.user_name = user_name;
        this.user_status = user_status;
        this.user_thumb_image = user_thumb_image;
    }

    public All_Users(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

}

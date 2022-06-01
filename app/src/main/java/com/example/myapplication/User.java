package com.example.myapplication;

public class User {
    public String username, password, mobile , category;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User(){

    }

    public User(String username, String mobile, String category) {
        this.username = username;
        this.mobile = mobile;
        this.category = category;
    }
}

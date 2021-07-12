package com.example.protectpet;

public class User {

    public String image,username;

    public User(){

    }
    public User(String image, String username) {
        this.image = image;
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }
}

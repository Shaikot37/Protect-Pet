package com.example.protectpet;

import java.util.Date;

public class Comments extends BlogCommentId{

    private String message, user_id, image;
    private Date timestamp;
    private String username;
    private String imageURL;
    private String bio;

    public Comments(){

    }

    public Comments(String message, String user_id, Date timestamp, String username, String imageURL, String bio) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.imageURL = imageURL;
        this.username = username;
        this.bio = bio;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}

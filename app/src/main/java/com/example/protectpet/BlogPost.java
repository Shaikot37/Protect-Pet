package com.example.protectpet;


import java.util.Date;

public class BlogPost extends BlogPostId {

    public String user_id, image_url, desc, image_thumb, post_category, location, address;
    public Date timestamp;

    public BlogPost() {}

    public BlogPost(String user_id, String image_url, String desc, String image_thumb, Date timestamp, String post_category, String location, String address) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
        this.post_category = post_category;
        this.location = location;
        this.address = address;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost_category() {
        return post_category;
    }

    public void setPost_category(String post_category) { this.post_category = post_category; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}

package com.malproject.youssufradi.movieapp;

/**
 * Created by y_sam on 12/1/2016.
 */

public class Info {
    private String author;
    private String details;

    public Info(String author, String details) {
        this.author = author;
        this.details = details;
    }

    public String getAuthor() {
        return author;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

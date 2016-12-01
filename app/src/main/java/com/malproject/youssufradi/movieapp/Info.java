package com.malproject.youssufradi.movieapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by y_sam on 12/1/2016.
 */

public class Info implements Parcelable {
    private String author;
    private String details;

    public Info(String author, String details) {
        this.author = author;
        this.details = details;
    }

    protected Info(Parcel in) {
        author = in.readString();
        details = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(details);
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

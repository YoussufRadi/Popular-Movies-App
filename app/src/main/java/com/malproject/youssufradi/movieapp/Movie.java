package com.malproject.youssufradi.movieapp;

/**
 * Created by y_sam on 11/25/2016.
 */

public class Movie {

    private String title;
    private String poster;
    private String plot;
    private String userRating;
    private String releaseDate;

    public Movie(String title, String poster, String plot, String userRating, String releaseDate) {
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}

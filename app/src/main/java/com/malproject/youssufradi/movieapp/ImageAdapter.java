package com.malproject.youssufradi.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by y_sam on 11/25/2016.
 */

public class ImageAdapter extends BaseAdapter {

    private ArrayList<Movie> movies;
    private Context context;

    public ImageAdapter(Context context, ArrayList<Movie> movies) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int i) {
        return movies.get(i);
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }


    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View imageView = inflater.inflate(R.layout.movie_image, viewGroup, false);
        ImageView image = (ImageView) imageView.findViewById(R.id.movie_display);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w500/" + movies.get(i).getPoster()).into(image);
        return imageView;
    }
}

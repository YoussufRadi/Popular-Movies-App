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

public class ImageAdaptor extends BaseAdapter {

    private ArrayList<String> movies;
    private Context context;

    public ImageAdaptor(Context context, ArrayList<String> movies) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return movies.get(i);
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
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + movies.get(i)).into(image);
        return imageView;
    }
}

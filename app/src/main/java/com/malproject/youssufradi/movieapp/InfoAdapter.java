package com.malproject.youssufradi.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by y_sam on 12/1/2016.
 */

public class InfoAdapter extends BaseAdapter {
    private ArrayList<Info> info;
    private Context context;

    public InfoAdapter(Context context, ArrayList<Info> info) {
        this.info = info;
        this.context = context;
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Info getItem(int i) {
        return info.get(i);
    }

    public ArrayList<Info> getMovies() {
        return info;
    }


    public void setMovies(ArrayList<Info> info) {
        this.info = info;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View detailView = inflater.inflate(R.layout.info_detail, viewGroup, false);
        TextView author = (TextView) detailView.findViewById(R.id.review_author);
        TextView content = (TextView) detailView.findViewById(R.id.review_content);
        author.setText(info.get(i).getAuthor());
        Log.d(TAG, info.get(i).getAuthor());
        content.setText(info.get(i).getDetails());
        return detailView;
    }

}

package com.malproject.youssufradi.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class DetailFragment extends Fragment {

    private Movie detailMovie;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            detailMovie = (Movie) intent.getParcelableExtra("Movie");
            Log.d(TAG, detailMovie+"");
            if(detailMovie == null)
                return rootView;
            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(detailMovie.getTitle());
            ((TextView) rootView.findViewById(R.id.movie_plot))
                    .setText(detailMovie.getPlot());
            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(detailMovie.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.movie_date))
                    .setText(detailMovie.getReleaseDate());
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + detailMovie.getPoster()).into((ImageView) rootView.findViewById(R.id.movie_poster));
        }
        return rootView;
    }

}
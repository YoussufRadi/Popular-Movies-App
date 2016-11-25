package com.malproject.youssufradi.movieapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private ImageAdaptor mImageAdaptor;

    public MainFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> fake = new ArrayList<String>();
        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        mImageAdaptor = new ImageAdaptor(getActivity(),fake);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mImageAdaptor);
        return rootView;
    }

}

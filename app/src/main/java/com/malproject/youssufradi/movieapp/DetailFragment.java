package com.malproject.youssufradi.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DetailFragment extends Fragment {

    private Movie detailMovie;
    private ArrayList<String> reviews;
    private ArrayList<String> trailers;

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
            ((TextView) rootView.findViewById(R.id.movie_overview))
                    .setText(detailMovie.getPlot());
            ((TextView) rootView.findViewById(R.id.movie_average_rating))
                    .setText(detailMovie.getUserRating());
            ((TextView) rootView.findViewById(R.id.movie_release_date))
                    .setText(detailMovie.getReleaseDate());
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500/" + detailMovie.getPoster()).into((ImageView) rootView.findViewById(R.id.movie_poster));
            Toast.makeText(getActivity(),detailMovie.getPoster(),Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    class FetchDataFromApi extends AsyncTask<String,Void,ArrayList<String>> {

        private final String LOG_TAG = MainFragment.FetchDataFromApi.class.getSimpleName();
        private boolean rev = true;
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + detailMovie.getId() + "/"+ params[0]+"?api_key=8c79e11ad73fea8dbe678fee8de573e6";

                if(params[0].equals("reviews"))
                    rev = true;

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
//                    .appendQueryParameter(QUERY_PARAM, BuildConfig. )
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJSON(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private ArrayList<String> getDataFromJSON(String jsonStr) throws JSONException {

            ArrayList<String> dataToDisplay = new ArrayList<String>();
            JSONObject json = new JSONObject(jsonStr);
            JSONArray array = json.getJSONArray("results");

            for(int i = 0; i < array.length(); i++){
                JSONObject movieObject = array.getJSONObject(i);
                Log.v(LOG_TAG,movieObject.toString());
                if(rev)
                    reviews.add(movieObject.getString("id").toString());
                else
                    trailers.add(movieObject.getString("id").toString());
            }
            return reviews;
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            if(result == null) {
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
//            mImageAdaptor = new ImageAdaptor(getActivity(),result);
//            gridView.setAdapter(mImageAdaptor);
        }
    }


}
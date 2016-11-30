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
import android.widget.AdapterView;
import android.widget.GridView;

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

public class MainFragment extends Fragment {

    private ImageAdaptor mImageAdaptor;
    private GridView gridView;

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

//        ArrayList<String> fake = new ArrayList<String>();
//        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
//        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
//        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
//        fake.add("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        new FetchDataFromApi().execute("popular");
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movieDetails = mImageAdaptor.getItem(i);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Movie",movieDetails);
                Log.d(TAG, movieDetails.getTitle() +"");
                startActivity(intent);
            }
        });
        return rootView;
    }

    /**
     * Created by y_sam on 11/25/2016.
     */

    class FetchDataFromApi extends AsyncTask<String,Void,ArrayList<Movie>> {

        private final String LOG_TAG = FetchDataFromApi.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "?api_key=8c79e11ad73fea8dbe678fee8de573e6";
                final String QUERY_PARAM = "api_key";

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
                moviesJsonStr = buffer.toString();
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
                return getMoviesFromJSON(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private ArrayList<Movie> getMoviesFromJSON(String moviesJsonStr) throws JSONException {

            ArrayList<Movie> moviesToDisplay = new ArrayList<Movie>();
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            for(int i = 0; i < moviesArray.length(); i++){
                JSONObject movieObject = moviesArray.getJSONObject(i);
                Log.v(LOG_TAG,movieObject.toString());
                moviesToDisplay.add(new Movie(movieObject.getString("title"),
                        movieObject.getString("poster_path"),
                        movieObject.getString("overview"),
                        movieObject.getString("vote_average"),
                        movieObject.getString("release_date")));
            }
            return moviesToDisplay;
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            mImageAdaptor = new ImageAdaptor(getActivity(),result);
            gridView.setAdapter(mImageAdaptor);
        }
    }


}

package com.malproject.youssufradi.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.malproject.youssufradi.movieapp.data.MovieContract;
import com.malproject.youssufradi.movieapp.data.MovieDbHelper;

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

    private ImageAdapter mImageAdapter;
    private GridView gridView;
    private ArrayList<Movie> movies;
    private ArrayList<Movie> favourites;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("Movies"))
            movies = savedInstanceState.<Movie>getParcelableArrayList("Movies");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Movies", movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String param = prefs.getString(getString(R.string.sort_value), getString(R.string.sort_string));
            Log.d(TAG, param + "");
            new FetchDataFromApi().execute(param);

        }

        return super.onOptionsItemSelected(item);
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
        updateSearch();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movieDetails = mImageAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Movie",movieDetails);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void getDataFromDB(){
        SQLiteDatabase db = new MovieDbHelper(getActivity()).getWritableDatabase();
        Cursor movieCursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        if(movieCursor.moveToFirst()){
            int movieId = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int title = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            int poster = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
            int plot = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT);
            int date = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE);
            int rating = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
            do {
                String COLUMN_MOVIE_ID = movieCursor.getString(movieId);
                String COLUMN_TITLE = movieCursor.getString(title);
                String COLUMN_POSTER = movieCursor.getString(poster);
                String COLUMN_PLOT = movieCursor.getString(plot);
                String COLUMN_DATE = movieCursor.getString(date);
                String COLUMN_RATING = movieCursor.getString(rating);
                Log.d(COLUMN_MOVIE_ID + COLUMN_TITLE + COLUMN_POSTER,COLUMN_PLOT + COLUMN_RATING + COLUMN_DATE);
                favourites.add(new Movie(COLUMN_MOVIE_ID,COLUMN_TITLE,COLUMN_POSTER,COLUMN_PLOT,COLUMN_RATING,COLUMN_DATE));
            } while(movieCursor.moveToNext());
        }
        movieCursor.close();
        db.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateSearch();
    }

    public void updateSearch(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortKey = prefs.getString(getString(R.string.sort_value), "popular");
        Toast.makeText(getActivity(),sortKey,Toast.LENGTH_SHORT).show();
        if(sortKey.equals("favourites")){
            getDataFromDB();
            mImageAdapter = new ImageAdapter(getActivity(),favourites);
            gridView.setAdapter(mImageAdapter);
        }
        else
            new FetchDataFromApi().execute(sortKey);
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

            ArrayList<Movie> moviesToDisplay = movies = new ArrayList<Movie>();
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            for(int i = 0; i < moviesArray.length(); i++){
                JSONObject movieObject = moviesArray.getJSONObject(i);
                moviesToDisplay.add(new Movie(movieObject.getString("id"),
                        movieObject.getString("title"),
                        movieObject.getString("poster_path"),
                        movieObject.getString("overview"),
                        movieObject.getString("vote_average"),
                        movieObject.getString("release_date")));
            }
            return moviesToDisplay;
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            if(result == null) {
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
            mImageAdapter = new ImageAdapter(getActivity(),result);
            gridView.setAdapter(mImageAdapter);
        }
    }


}

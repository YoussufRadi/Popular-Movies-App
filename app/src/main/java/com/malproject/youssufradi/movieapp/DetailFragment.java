package com.malproject.youssufradi.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.malproject.youssufradi.movieapp.data.MovieContract;
import com.malproject.youssufradi.movieapp.data.MovieDbHelper;
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
    private ArrayList<Info> reviews;
    private ArrayList<Info> trailers;
    private ListView reviewList;
    private ListView trailerList;
    private InfoAdapter reviewInfoAdapter;
    private InfoAdapter trailerInfoAdapter;
    View rootView;


    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("trailers")) {
            trailers = savedInstanceState.<Info>getParcelableArrayList("trailers");
        }
        if(savedInstanceState != null && savedInstanceState.containsKey("reviews")) {
            reviews = savedInstanceState.<Info>getParcelableArrayList("reviews");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trailers",trailers);
        outState.putParcelableArrayList("reviews",reviews);
        super.onSaveInstanceState(outState);
    }

    public void updateMovie(Movie movie){
        this.detailMovie = movie;
        updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent.getParcelableExtra("Movie") != null) {
            detailMovie = (Movie) intent.getParcelableExtra("Movie");
            Log.d(TAG, detailMovie+"");
            if(detailMovie == null)
                return rootView;
            updateView();
        }
        return rootView;
    }

    public void updateView(){
        ((TextView) rootView.findViewById(R.id.movie_title))
                .setText(detailMovie.getTitle());
        ((TextView) rootView.findViewById(R.id.movie_overview))
                .setText(detailMovie.getPlot());
        ((TextView) rootView.findViewById(R.id.movie_average_rating))
                .setText(detailMovie.getUserRating());
        ((TextView) rootView.findViewById(R.id.movie_release_date))
                .setText(detailMovie.getReleaseDate());
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500/" + detailMovie.getPoster()).into((ImageView) rootView.findViewById(R.id.movie_poster));
        reviewList = (ListView) rootView.findViewById(R.id.review_list);
        trailerList = (ListView) rootView.findViewById(R.id.trailer_list);
        ImageButton favourite = (ImageButton) rootView.findViewById(R.id.movie_favorite_button);
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInDB(detailMovie.getId(),detailMovie.getTitle(),detailMovie.getPoster(),
                        detailMovie.getPlot(),detailMovie.getReleaseDate(),detailMovie.getUserRating());
            }
        });
        //new FetchDataFromApi().execute("reviews");
        new FetchDataFromApi().execute("videos");
        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Info info = trailerInfoAdapter.getItem(i);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+info.getDetails()));
                startActivity(intent);
            }
        });
    }


    public void insertInDB(String movieId, String title, String poster, String plot, String date, String rating){
        SQLiteDatabase db = new MovieDbHelper(getActivity()).getWritableDatabase();
        Cursor movieCursor = db.rawQuery("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movieId,null);
        boolean cursor = movieCursor.getCount() == 0;
        ContentValues movie = DetailFragment.createMovieValues(movieId,title,poster,plot,date,rating);
        if(cursor) {
            long movieID = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movie);
            if(movieID != -1)
                Toast.makeText(getActivity(),"Movie added to favourites", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
        movieCursor.close();
        db.close();
    }

    static ContentValues createMovieValues(String movieId, String title, String poster, String plot, String date, String rating) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, poster);
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, plot);
        movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, date);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
        return movieValues;
    }


    class FetchDataFromApi extends AsyncTask<String,Void,ArrayList<Info>> {

        private final String LOG_TAG = MainFragment.FetchDataFromApi.class.getSimpleName();
        private boolean rev = false;
        @Override
        protected ArrayList<Info> doInBackground(String... params) {
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
                Log.i("movie detail : ", detailMovie.getId());
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

        private ArrayList<Info> getDataFromJSON(String jsonStr) throws JSONException {

            ArrayList<String> dataToDisplay = new ArrayList<String>();
            JSONObject json = new JSONObject(jsonStr);
            JSONArray array = json.getJSONArray("results");
            if(rev)
                reviews = new ArrayList<Info>();
            else
                trailers = new ArrayList<Info>();
            for(int i = 0; i < array.length(); i++){
                JSONObject movieObject = array.getJSONObject(i);
                if(rev)
                    reviews.add(new Info(movieObject.getString("author").toString(), movieObject.getString("content").toString()));
                else
                    trailers.add(new Info(movieObject.getString("name").toString(),movieObject.getString("key").toString()));
            }
            if(rev)
                return reviews;
            else
                return trailers;
        }

        protected void onPostExecute(ArrayList<Info> result) {
            if(result == null) {
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
            if(rev) {
                reviewInfoAdapter = new InfoAdapter(getActivity(), reviews);
                reviewList.setAdapter(reviewInfoAdapter);
            }
            else {
                trailerInfoAdapter = new InfoAdapter(getActivity(), trailers);
                trailerList.setAdapter(trailerInfoAdapter);
            }
        }
    }


}
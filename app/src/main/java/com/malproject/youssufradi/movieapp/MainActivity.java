package com.malproject.youssufradi.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setDetailFratgment(Movie movieDetails){
        if(getResources().getString(R.string.isTablet).equals("true")){
            ((DetailFragment)getSupportFragmentManager().findFragmentById(R.id.fragment2)).updateMovie(movieDetails);
        }else
        startActivity(new Intent(this, DetailActivity.class).putExtra("Movie",movieDetails));
    }
}

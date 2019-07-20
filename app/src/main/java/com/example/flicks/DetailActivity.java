package com.example.flicks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flicks.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class DetailActivity extends YouTubeBaseActivity {
    private static final String YOUTUBE_API_KEY ="AIzaSyB6gdjIGiP9tYtxFGCFUqGmDS7TxrwoqpY";
    private static final String TRAILERS_API_KEY = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    TextView tvTitle;
    TextView tvOverview;
    TextView tvLanguage;
    TextView etDate;
    RatingBar ratingBar;
    Movie movie;
    YouTubePlayerView youTubePlayerView;
    double rvoteAverage;
    double voteAverage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvLanguage = findViewById(R.id.tvLanguage);
        etDate = findViewById(R.id.etDate);
        ratingBar= findViewById(R.id.ratingBar);
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        String title = getIntent().getStringExtra("title");
        tvTitle.setText(title);
        String overview = getIntent().getStringExtra("overview");
        tvOverview.setText(overview);
        ratingBar.setRating((float) movie.getRvoteAverage());
        tvLanguage.setText(movie.getOriginalLanguage());
        etDate.setText(movie.getReleaseDate());

        youTubePlayerView = findViewById(R.id.player);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(TRAILERS_API_KEY, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray results = response.getJSONArray("results");
                    if (results.length() == 0) {
                        return;
                    }
                    JSONObject movieTrailer = results.getJSONObject(0);
                    String youtubeKey = movieTrailer.getString("key");
                    initializeYoutube(youtubeKey);
                }  catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    private void initializeYoutube(String youtubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("smile", "on init Succes");
                if ( voteAverage >5  ) {  youTubePlayer.loadVideo("youtubeKey");}
                else { youTubePlayer.cueVideo("youtubeKey");}

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("smile", "on init failure");

            }
        });
    }
}

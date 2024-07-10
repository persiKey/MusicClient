package com.example.musicclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivityBackgroundTask implements Runnable{
    static String TOKEN_TYPE = "Bearer ";
    public static String TOKEN = TOKEN_TYPE
        + "BQDZDkO1Ifi88zbzT6_YusVxFWF2Oq_vmX7KC1zlLsxLUBcXuqH0gumalEAA-47sdeOH7aRHX50ErQIj1Zupx3VgMnP4B5yUtrCu-xddbBxg5R5B37s";

    private static String TAG = "MainActivityBackgroundTask";
    private List<Album> albumList;
    private AlbumAdapter albumAdapter;
    MainActivityBackgroundTask(AlbumAdapter adapter, List<Album> albums){
        albumList = albums;
        albumAdapter = adapter;
    }
    @Override
    public void run() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Backend service = retrofit.create(Backend.class);

        Call<NewReleasesResponse> res = service.getNewReleases(TOKEN, 10);
        res.enqueue(new Callback<NewReleasesResponse>() {
            @Override
            public void onResponse(Call<NewReleasesResponse> call, Response<NewReleasesResponse> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Success");
                    HandleSuccess(response.body());
                }
                else {
                    Log.e(TAG, "Bad response");

                    try {
                        Log.i(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<NewReleasesResponse> call, Throwable throwable) {
                Log.e(TAG, "Fail");
            }

        });

    }

    void HandleSuccess(NewReleasesResponse response)
    {
        int counter = 0;
        for (Item i : response.getAlbums().getItems()) {

            try {
                URL url = new URL(i.getImages().get(0).getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                Album album = new Album(i.getName());
                album.cover = myBitmap;
                albumList.add(album);

                Handler mainHandler = new Handler(Looper.getMainLooper());

                int finalCounter = counter;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        albumAdapter.notifyItemChanged( 3 + finalCounter);
                    }
                });
                counter++;

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

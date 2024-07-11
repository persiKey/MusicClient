package com.example.musicclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.musicclient.datalayer.Album;
import com.example.musicclient.datalayer.AlbumsSSOT;
import com.example.musicclient.datalayer.Backend;
import com.example.musicclient.datalayer.responses.Item;
import com.example.musicclient.datalayer.responses.NewReleasesResponse;
import com.example.musicclient.ui.AlbumAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivityBackgroundTask implements Runnable {
    static String TOKEN_TYPE = "Bearer ";
    public static String TOKEN = TOKEN_TYPE +
            "BQB31X0MpaArNeRX4DCoUjCWLFo_10jaML9dRatDLE1POCixp_yTCRoFnrMmN9xShTvMgvqEwIem8VA6xC2CaOABjjZ3zVml0cylkrYnnG2bZamtYRw";

    private static String TAG = "MainActivityBackgroundTask";
    private AlbumAdapter albumAdapter;

    MainActivityBackgroundTask(AlbumAdapter adapter) {
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
        try {
            Response<NewReleasesResponse> response = res.execute();
            if (response.isSuccessful()) {
                Log.i(TAG, "Success");
                HandleSuccess(response.body());
            } else {
                Log.e(TAG, "Bad response");
                Log.i(TAG, response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e(TAG, "Fail");
            throw new RuntimeException(e);
        }

    }

    void HandleSuccess(NewReleasesResponse response) {
        int counter = 0;
        for (Item i : response.getAlbums().getItems()) {

            try {
                URL url = new URL(i.getImages().get(2).getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                Album album = new Album(i.getName());
                album.cover = myBitmap;
                AlbumsSSOT.GetInstance().insert(album);

                Handler mainHandler = new Handler(Looper.getMainLooper());

                int finalCounter = counter;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        albumAdapter.notifyItemChanged(finalCounter);
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

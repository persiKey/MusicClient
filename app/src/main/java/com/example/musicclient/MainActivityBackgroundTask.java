package com.example.musicclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.musicclient.datalayer.Album;
import com.example.musicclient.datalayer.AlbumsSSOT;
import com.example.musicclient.datalayer.Backend;
import com.example.musicclient.datalayer.responses.Item;
import com.example.musicclient.datalayer.responses.NewReleasesResponse;
import com.example.musicclient.ui.NewReleasesAdapter;
import com.example.mylivedata.MyLiveData;

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
    static final int ALBUM_LIMIT = 10;
    static final int INDEX_OF_THE_BIGGEST_IMAGE = 2;
    static String TOKEN_TYPE = "Bearer ";
    public static String TOKEN = TOKEN_TYPE +
            "BQCtvww0Tp83v2LeEyWukLrI2HGSZ_P_RtX4H7KRaED_ue6ZucR4rphgrZ4EwnOVhM-N3tsEajRTrcl4xzNP6-d_IkT1gOqszXSECIe8YySxKrP5eUE";

    private static String TAG = "MainActivityBackgroundTask";

//    private MutableLiveData<Album> newAlbum;
    private MyLiveData<Album> newAlbum;

    MainActivityBackgroundTask(MyLiveData<Album> newAlbum) {
        this.newAlbum = newAlbum;
    }

    @Override
    public void run() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Backend service = retrofit.create(Backend.class);

        Call<NewReleasesResponse> request = service.getNewReleases(TOKEN, ALBUM_LIMIT);
        try {
            Response<NewReleasesResponse> response = request.execute();
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
                URL url = new URL(i.getImages().get(INDEX_OF_THE_BIGGEST_IMAGE).getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                Album album = new Album(i.getName());
                album.cover = myBitmap;
                album.counter = counter;

                newAlbum.postData(album);

                counter++;

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

package com.example.musicclient;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivityBackgroundTask implements Runnable{
    static String TOKEN_TYPE = "Bearer ";
    static String TOKEN = TOKEN_TYPE
        + "BQAMNjR7pGqcjm626u_5R8U9tNv28AxxhEct4ERo_sT0U9o0PmHMnAc6qdH7NiaXYzAVGn1jAYkAOCESHvoWz4Cmt3uIM9iKnu41nnBpz-K5iBUFdMQ";

    private static String TAG = "MainActivityBackgroundTask";
    private List<Album> albumList;
    MainActivityBackgroundTask(List<Album> albums){
        albumList = albums;
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

        // get the top 10
        // download images
        // add to view holder

        // notify
    }

    void HandleSuccess(NewReleasesResponse response)
    {

    }
}

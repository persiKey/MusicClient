package com.example.musicclient;

import android.util.Log;

import java.util.List;

public class MainActivityBackgroundTask implements Runnable{

    private static String TAG = "MainActivityBackgroundTask";
    private List<Album> albumList;
    MainActivityBackgroundTask(List<Album> albums){
        albumList = albums;
    }
    @Override
    public void run() {
        Log.i(TAG, "Run");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // get the top 10
        // download images
        // add to view holder

        // notify
    }
}

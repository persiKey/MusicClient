package com.example.musicclient.datalayer;

import android.graphics.Bitmap;

public class Album {

    public Bitmap cover;
    public String name;
    public int counter;

    public Album(String album_name)
    {
        name = album_name;
    }
}

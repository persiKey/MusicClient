package com.example.musicclient;

import android.graphics.Bitmap;
import android.media.Image;

public class Album {
    public Bitmap cover;
    public String name;

    public Album(String album_name)
    {
        name = album_name;
    }
}

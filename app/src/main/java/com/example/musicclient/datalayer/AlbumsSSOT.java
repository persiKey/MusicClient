package com.example.musicclient.datalayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlbumsSSOT {
    static volatile AlbumsSSOT INSTANCE;
    List<Album> Albums;

    private AlbumsSSOT() {
        Albums = new ArrayList<>();
    }


    static public AlbumsSSOT GetInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlbumsSSOT();
        }

        return INSTANCE;
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(Albums);
    }

    public void insert(Album album) {
        Albums.add(album);
    }
}

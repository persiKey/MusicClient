package com.example.musicclient.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicclient.R;
import com.example.musicclient.datalayer.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    List<Album> localAlbums;

    public AlbumAdapter(List<Album> albums)
    {
        localAlbums = albums;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleview_item, parent, false);

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.getTextView().setText(localAlbums.get(position).name);
        if(localAlbums.get(position).cover != null) {
            holder.getImageView().setImageBitmap(localAlbums.get(position).cover);
        }
    }


    @Override
    public int getItemCount() {
        return localAlbums.size();
    }
}

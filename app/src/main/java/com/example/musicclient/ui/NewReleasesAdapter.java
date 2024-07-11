package com.example.musicclient.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicclient.R;
import com.example.musicclient.datalayer.Album;

import java.util.List;

public class NewReleasesAdapter extends RecyclerView.Adapter<NewReleasesViewHolder> {
    List<Album> localAlbums;

    public NewReleasesAdapter(List<Album> albums)
    {
        localAlbums = albums;
    }

    @NonNull
    @Override
    public NewReleasesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_releases_recyclerview_item, parent, false);

        return new NewReleasesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewReleasesViewHolder holder, int position) {
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

package com.example.musicclient;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView textView;


    public AlbumViewHolder(@NonNull View itemView) {
        super(itemView);


        imageView = itemView.findViewById(R.id.album_image);
        textView = itemView.findViewById(R.id.album_name);

        itemView.setOnClickListener(view -> {
            Toast.makeText(
                    itemView.getContext(),
                    "AlBOOOM " + textView.getText() + "!",
                    Toast.LENGTH_LONG).show();
        });
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }
}

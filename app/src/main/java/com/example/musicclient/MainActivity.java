package com.example.musicclient;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private HandlerThread backgroundWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        List<Album> Albums = new ArrayList<Album>();


        AlbumAdapter albumAdapter = new AlbumAdapter(Albums);
        RecyclerView recyclerView = findViewById(R.id.album_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(albumAdapter);

        if(savedInstanceState == null)
        {
            backgroundWorker = new HandlerThread("Main Activity background worked");
            backgroundWorker.start();
            Handler myHandler = new Handler(backgroundWorker.getLooper());
            myHandler.post(new MainActivityBackgroundTask(albumAdapter, Albums));
        }


    }

}
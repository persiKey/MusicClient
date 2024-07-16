package com.example.musicclient;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicclient.datalayer.Album;
import com.example.musicclient.datalayer.AlbumsSSOT;
import com.example.musicclient.ui.NewReleasesAdapter;
import com.example.mylivedata.MyLiveData;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity_" + hashCode();

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

        NewReleasesAdapter newReleasesAdapter = new NewReleasesAdapter(AlbumsSSOT.GetInstance().getAlbums());
        RecyclerView newReleasesRecyclerView = findViewById(R.id.new_releases_recycler);
        newReleasesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        newReleasesRecyclerView.setAdapter(newReleasesAdapter);

        backgroundWorker = new HandlerThread("Main Activity background worker");
        backgroundWorker.start();
        Handler myHandler = new Handler(backgroundWorker.getLooper());

        final Observer<Album> albumObserver = new Observer<Album>() {

            @Override
            public void onChanged(final Album newAlbum) {

                AlbumsSSOT.GetInstance().insert(newAlbum);

                newReleasesAdapter.notifyItemChanged(newAlbum.counter);
            }
        };

        MyLiveData<Album> newAlbumLiveData = new MyLiveData<Album>();

        newAlbumLiveData.observe(albumObserver);

        myHandler.post(new MainActivityBackgroundTask(newAlbumLiveData));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundWorker.quit();
    }
}
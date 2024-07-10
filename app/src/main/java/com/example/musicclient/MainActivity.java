package com.example.musicclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        if(savedInstanceState == null)
        {
            backgroundWorker = new HandlerThread("Main Activity background worked");
            backgroundWorker.start();
            Handler myHandler = new Handler(backgroundWorker.getLooper());
            myHandler.post(new MainActivityBackgroundTask(Albums));
        }

        Albums.add(new Album("Tesing"));
        Albums.add(new Album("TESTING"));
        Albums.add(new Album("TeStInG"));


        AlbumAdapter albumAdapter = new AlbumAdapter(Albums);
        RecyclerView recyclerView = findViewById(R.id.album_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(albumAdapter);


        Button button = findViewById(R.id.button_my);
        button.setOnClickListener(view -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.spotify.com/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Backend service = retrofit.create(Backend.class);

            Call<ArtistResponse> res = service.getData("4aawyAB9vmqN3uQ7FjRGTy", "Bearer ");
            res.enqueue(new Callback<ArtistResponse>() {
                @Override
                public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                    //Log.d("RESPONSE", response.body().getMessage());

                    try {
                        if (response.isSuccessful()) {
                            Log.i("RESPONSE", response.body().getName());

                            AsyncTask<String,Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(String... urls) {
                                    try {
                                        URL url = new URL(urls[0]);
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        InputStream input = connection.getInputStream();
                                        Bitmap myBitmap = BitmapFactory.decodeStream(input);

                                        Album album = new Album(response.body().getName());
                                        album.cover = myBitmap;

                                        Albums.add(album);

                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return null;
                                }
                            };

                            task.execute(response.body().getImages().get(0).getUrl());
                            albumAdapter.notifyDataSetChanged();

                        }
                        else {
                            Log.w("RESPONSE", response.errorBody().string());
                        }
                        Log.w("RESPONSE", response.toString());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<ArtistResponse> call, Throwable throwable) {
                    Log.e("RESPONSE", "fail");
                }
            });
        });
    }
}
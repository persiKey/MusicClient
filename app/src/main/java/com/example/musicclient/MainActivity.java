package com.example.musicclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    static String TOKEN = "BQDIc8trZEQUM2aVvDYKwKm2A5tH4zxmExcd9l1w8ky0jrNf8usr9mBGjRhPZtgt1cBcCMrywz2taLUjrs3MSri68-keow31YNsQPG6zPP4FmkT6bBQ";
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

        Album albums[] = {
                new Album("Tesing"),
                new Album("TESTING"),
                new Album("TeStInG"),
        };

        List<Album> Albums = new ArrayList<Album>();
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
//                    .baseUrl("https://jsonplaceholder.typicode.com/")
                    .baseUrl("https://api.spotify.com/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitTest service = retrofit.create(RetrofitTest.class);

            Call<ArtistResponse> res = service.getData("4aawyAB9vmqN3uQ7FjRGTy", "Bearer " + TOKEN);
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
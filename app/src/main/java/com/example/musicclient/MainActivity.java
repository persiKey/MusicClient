package com.example.musicclient;

import android.os.Bundle;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    static String TOKEN = "BQB2FNefexJpBc7oLcnvqcLHSO71rspyriHz-G8nW_xg4mgtlWon-e66RCZyNHhrBy_AZhOaEblrFG3tegxxx7fH7EORGfF2-kvqA1u_m0t_IRBV07A";
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

        AlbumAdapter albumAdapter = new AlbumAdapter(albums);
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

            Call<ArtistResponse> res = service.getData("0TnOYISbd1XYRBk9myaseg", "Bearer " + TOKEN);
            res.enqueue(new Callback<ArtistResponse>() {
                @Override
                public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                    //Log.d("RESPONSE", response.body().getMessage());

                    try {
                        if (response.isSuccessful()) {
                            Log.i("RESPONSE", response.body().getName());


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
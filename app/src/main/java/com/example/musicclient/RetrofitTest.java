package com.example.musicclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface RetrofitTest {

    @GET("artists/{id}")
    Call<ResponseBody> getData(@Path("id") String id);

    @GET("albums/{id}")
    Call<ArtistResponse> getData(@Path("id") String id, @Header("Authorization") String token);
}

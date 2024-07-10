package com.example.musicclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Backend {

    @GET("browse/new-releases")
    Call<NewReleasesResponse> getNewReleases(@Header("Authorization") String token,
                                      @Query("limit") int limit);

    @GET("albums/{id}")
    Call<ArtistResponse> getAlbumInfo(@Path("id") String id, @Header("Authorization") String token);


    @GET("albums/{id}")
    Call<ArtistResponse> getData(@Path("id") String id, @Header("Authorization") String token);
}

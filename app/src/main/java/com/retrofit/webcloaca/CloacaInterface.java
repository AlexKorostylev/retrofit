package com.retrofit.webcloaca;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CloacaInterface {
    @GET("/API/and")
    Call<LinkModel> loadLink(
            @Query("project") String project,
            @Query("country") String country,
            @Query("apid") String apid,
            @Query("gaid") String gaid,
            @Query("deep") String deep
    );
}

package com.retrofit.webcloaca;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CloacaInterface {
    @GET("API/and?project=com.site.getpost&country=ru&apid=111111&gaid=2222222&deep=9999999\"")
    Call<LinkModel> loadLink();
}

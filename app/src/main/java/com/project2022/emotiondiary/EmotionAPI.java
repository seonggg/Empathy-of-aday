package com.project2022.emotiondiary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EmotionAPI {
    @POST("/texts/")
    Call<TextItem> text_text(@Body TextItem text);

    @PATCH("/texts/{pk}/")
    Call<TextItem> patch_text(@Path("pk") int pk, @Body TextItem text);

    @DELETE("/texts/{pk}/")
    Call<TextItem> delete_text(@Path("pk") int pk);

    @GET("/texts/")
    Call<List<TextItem>> get_text();

    @GET("/texts/{pk}/")
    Call<TextItem> get_text_pk(@Path("pk") int pk);
}

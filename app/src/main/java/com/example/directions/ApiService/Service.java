package com.example.directions.ApiService;

import com.example.directions.ListViewFunction.FavoritesDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    // 즐겨찾기 목록
    @GET("user/all")
    Call<List<FavoritesDTO>> favoriteslist(@Query("userid") int userid,
                                           @Query("title") String title,
                                           @Query("address") String address,
                                           @Query("lat") double lat,
                                           @Query("lon") double lon);
    //즐겨찾기 추가하기
    @FormUrlEncoded
    @POST("user/save/{userid}")
    Call<FavoritesDTO> intsertfavorites(
            @Path("userid") int userid,
            @Field("title") String title,
            @Field("address") String address,
            @Field("lat") double lat,
            @Field("lon") double lon
    );

    //즐겨찾기 삭제하기
    @DELETE("user/delete/{title}")
    Call<FavoritesDTO> deletefavorites(@Path("title") String title);
}
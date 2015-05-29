package com.aurozhkov.vkwall.services;

import com.aurozhkov.vkwall.network.WallResponse;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ApiService {

    @GET("/wall.get?extended=1")
    public Observable<WallResponse> getUserWall(@Query("owner_id") long userId,
                                                @Query("offset") int offset,
                                                @Query("count") int count);
}

package com.example.serenade.serenade.retrofit;

import com.example.serenade.serenade.bean.Lyric;
import com.example.serenade.serenade.bean.SongBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Serenade on 17/6/10.
 */

public interface RetrofitApi {

    @GET("213-2")
    BaseCall<BaseResponse<Lyric>> queryLyric(@Query("musicid") String musicId);

    @GET("213-1")
    BaseCall<BaseResponse<SongBean>> querySong(@Query("keyword") String musicName);

    @GET("213-1")
    Observable<BaseResponse<SongBean>> get(@Query("keyword") String musicName);

}

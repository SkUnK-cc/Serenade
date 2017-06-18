package com.example.serenade.serenade.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Serenade on 17/6/10.
 */

public class RetrofitHelper {
    private static final int DEFAULT_TIMEOUT = 5;
    private static Retrofit mRetrofit;//Retrofit对象
    private static String mBaseUrl = "http://route.showapi.com/";//项目总地址
    private static RetrofitApi mRetrofitAPI;//地址接口


    public static RetrofitApi getApi() {
        if (mRetrofitAPI == null) {
            OkHttpClient mClient = new OkHttpClient.Builder()
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(InterceptorUtil.HeaderInterceptor())
                    .addInterceptor(InterceptorUtil.LogInterceptor())//添加日志拦截器
                    .build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(mBaseUrl)//设置项目总地址
                    .addConverterFactory(GsonConverterFactory.create())//添加Gson解析工厂
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加RxJava2转换器
                    .addCallAdapterFactory(BaseCallAdapterFactory.create())//添加自定义BaseCall转换器
                    .client(mClient)
                    .build();
            mRetrofitAPI = mRetrofit.create(RetrofitApi.class);
        }
        return mRetrofitAPI;
    }

}

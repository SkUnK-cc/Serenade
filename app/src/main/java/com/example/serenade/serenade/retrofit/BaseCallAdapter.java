package com.example.serenade.serenade.retrofit;


import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;

/**
 * Created by Serenade on 17/6/16.
 */

public class BaseCallAdapter<R> implements CallAdapter<R, BaseCall<R>> {
    private final Type responseType;

    // 下面的 responseType 方法需要数据的类型
    public BaseCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }


    @Override
    public BaseCall<R> adapt(Call<R> call) {
        return new BaseCall<>(call);
    }
}

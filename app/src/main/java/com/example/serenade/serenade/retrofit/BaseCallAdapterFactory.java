package com.example.serenade.serenade.retrofit;


import com.example.serenade.serenade.R;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by Serenade on 17/6/16.
 */

public final class BaseCallAdapterFactory extends CallAdapter.Factory {
    private BaseCallAdapterFactory(){
    }

    public static BaseCallAdapterFactory create(){
        return new BaseCallAdapterFactory();
    }

    @Override
    public CallAdapter<?,BaseCall<R>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        // 获取原始类型
        Class<?> rawType = getRawType(returnType);
        // 返回值必须是BaseCall并且带有泛型
        if (rawType == BaseCall.class && returnType instanceof ParameterizedType) {
            Type callReturnType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new BaseCallAdapter(callReturnType);
        }
        return null;
    }
}
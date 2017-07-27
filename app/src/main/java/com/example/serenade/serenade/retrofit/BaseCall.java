package com.example.serenade.serenade.retrofit;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 自定义OkHttp Call
 * Created by Serenade on 17/6/16.
 */

public class BaseCall<T> implements Call<T> {
    public final Call<T> mCall;
    private MainThreadExecutor mExecutor;
    private String clzName;

    public BaseCall(Call<T> call) {
        this.mCall = call;
        mExecutor = new MainThreadExecutor();
    }

    @Override
    public Response<T> execute() throws IOException {
        Response<T> response = mCall.execute();
        if (clzName!=null){
            NetworkManager.getInstance().removeRequest(clzName,mCall);
        }
        return response;
    }

    /**
     * 因为在BaseCallAdapterFactory中没有创建callbackExecutor即MainThreadExecutor对象
     * 所以无法切换线程，使自定义的Call的Callback在子线程中运行了，导致不能更改UI
     * 可使用handler切换线程或者直接在BaseCall构造方法中新建MainThreadExecutor对象
     *
     * @param callback
     */
    @Override
    public void enqueue(final Callback<T> callback) {
        mCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, final Response<T> response) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (clzName!=null){
                            NetworkManager.getInstance().removeRequest(clzName,mCall);
                        }
                        callback.onResponse(BaseCall.this, response);
                    }
                });
            }

            @Override
            public void onFailure(Call<T> call, final Throwable t) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (clzName!=null){
                            NetworkManager.getInstance().removeRequest(clzName,mCall);
                        }
                        callback.onFailure(BaseCall.this, t);
                    }
                });
            }
        });
    }

    public BaseCall<T> record(Class clz) {
        clzName = clz.getName();
        NetworkManager.getInstance().recordRequest(clzName, mCall);
        return this;
    }

    @Override
    public boolean isExecuted() {
        return mCall.isExecuted();
    }

    @Override
    public void cancel() {
        if (clzName!=null){
            NetworkManager.getInstance().removeRequest(clzName,mCall);
        }
        mCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return mCall.isCanceled();
    }

    @Override
    public BaseCall clone() {
        return new BaseCall(mCall.clone());
    }

    @Override
    public Request request() {
        return mCall.request();
    }
}

package com.example.serenade.serenade.retrofit;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * 网络请求管理类
 * Created by Serenade on 17/6/16.
 */

public class NetworkManager {
    private ArrayMap<String, List<Call>> mCalls;//管理

    /**
     * 构造方法私有化
     */
    private NetworkManager() {
    }

    /**
     * 静态内部类实现单例
     */
    private static class NetworkManagerHolder {
        public static final NetworkManager INSTANCE = new NetworkManager();
    }

    /**
     * 获得单一实例
     *
     * @return 单一实例
     */
    public static NetworkManager getInstance() {
        return NetworkManagerHolder.INSTANCE;
    }


    public void cancelRequests(String className) {
        if (mCalls != null) {
            List<Call> callList = mCalls.get(className);
            if (callList != null) {
                int size = callList.size();
                for (int i = 0; i < size; i++) {
                    Call call = callList.get(i);
                    call.cancel();
                }
                callList.clear();
                mCalls.remove(className);
            }
        }
    }

    public void recordRequest(String className, Call call) {
        if (mCalls == null)
            mCalls = new ArrayMap<>();
        if (mCalls.containsKey(className)) {
            List<Call> callList = mCalls.get(className);
            callList.add(call);
        } else {
            List<Call> callList = new ArrayList<>();
            callList.add(call);
            mCalls.put(className, callList);
        }
    }

    public void removeRequest(String className, Call call) {
        if (mCalls == null)
            return;

        if (mCalls.containsKey(className)) {
            List<Call> callList = mCalls.get(className);
            callList.remove(call);
        }
    }

}

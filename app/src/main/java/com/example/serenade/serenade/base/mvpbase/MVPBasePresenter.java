package com.example.serenade.serenade.base.mvpbase;

import com.example.serenade.serenade.retrofit.NetworkManager;

/**
 * Created by hjn on 2016/11/3.
 */
public abstract class MVPBasePresenter<T extends IBaseView> {
    protected T mView;
    public void attach(T mView) {
        this.mView = mView;
    }

    public void detach() {
        if (mView != null) {
            mView = null;
            //取消该页面所有网络请求
            NetworkManager.getInstance().cancelRequests(getClass().getName());
        }
    }

    public boolean isAttach() {
        return mView != null;
    }

}
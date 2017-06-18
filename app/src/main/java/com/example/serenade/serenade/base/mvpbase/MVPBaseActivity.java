package com.example.serenade.serenade.base.mvpbase;

import android.os.Bundle;

import com.example.serenade.serenade.base.BaseActivity;


/**
 * Created by admin on 2016/11/1.
 */
public abstract class MVPBaseActivity<T extends MVPBasePresenter> extends BaseActivity {

    protected T mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPresenter = createPresenter();
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.attach((IBaseView) this);
        }
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
        super.onDestroy();
    }


    public abstract T createPresenter();

}



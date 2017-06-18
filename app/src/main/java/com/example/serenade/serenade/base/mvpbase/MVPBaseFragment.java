package com.example.serenade.serenade.base.mvpbase;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.serenade.serenade.base.BaseFragment;


/**
 * Created by admin on 2016/11/1.
 */
public abstract class MVPBaseFragment<T extends MVPBasePresenter<IBaseView>> extends BaseFragment {
    protected MVPBasePresenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.attach((IBaseView) this);
        }
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
        super.onDestroy();
    }


    public abstract T createPresenter();
}

package com.example.serenade.serenade.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Serenade on 17/6/16.
 */

public class BaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (registerEventBus())
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registerEventBus())
            EventBus.getDefault().unregister(this);
    }

    @Subscribe
    protected void onReceiveEvent(EventCenter event) {
        onEventBusResult(event);
    }

    /**
     * EventBus回传消息重写方法
     *
     * @param event
     */
    protected void onEventBusResult(EventCenter event) {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BaseBinder(this);
    }


    /**
     * 是否注册EventBus
     *
     * @return true 注册 false 不注册
     */
    public boolean registerEventBus() {
        return false;
    }
}

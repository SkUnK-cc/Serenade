package com.example.serenade.serenade.base;

import android.os.Binder;

/**
 * Created by Serenade on 17/6/16.
 */

public class BaseBinder<T extends BaseService> extends Binder {
    private T service;

    public BaseBinder(T service) {
        this.service = service;
    }

    public T getService() {
        return service;
    }
}

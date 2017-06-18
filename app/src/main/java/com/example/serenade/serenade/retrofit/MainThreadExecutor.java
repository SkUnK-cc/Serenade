package com.example.serenade.serenade.retrofit;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Serenade on 17/6/17.
 */

public class MainThreadExecutor implements Executor {
    private final Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void execute(@NonNull Runnable r) {
        handler.post(r);
    }
}

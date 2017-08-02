package com.example.serenade.serenade.application;

import android.app.Application;

import com.mob.MobSDK;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Serenade on 17/6/18.
 */

public class MyApplication extends Application {
    private static Realm download_realm;
    private static Realm play_list_realm;
    private static RefWatcher watcher;

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this, null, null);
        Realm.init(this);
        download_realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("serenade_download.realm")
                .deleteRealmIfMigrationNeeded()
                .directory(getDir("realm", MODE_PRIVATE))
                .build());
        play_list_realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("serenade_play_list.realm")
                .deleteRealmIfMigrationNeeded()
                .directory(getDir("realm", MODE_PRIVATE))
                .build());
        watcher = LeakCanary.install(this);
    }

    public static Realm getDownloadRealm() {
        return download_realm;
    }

    public static Realm getPlayListRealm() {
        return play_list_realm;
    }

    public static RefWatcher getWatcher(){
        return watcher;
    }
}

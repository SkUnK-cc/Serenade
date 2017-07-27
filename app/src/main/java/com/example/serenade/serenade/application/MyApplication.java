package com.example.serenade.serenade.application;

import android.app.Application;

import com.mob.MobSDK;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Serenade on 17/6/18.
 */

public class MyApplication extends Application {
    private static Realm download_realm;
    private static Realm recent_listen_realm;

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
        recent_listen_realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("serenade_recent_listen.realm")
                .deleteRealmIfMigrationNeeded()
                .directory(getDir("realm", MODE_PRIVATE))
                .build());

    }

    public static Realm getDownloadRealm() {
        return download_realm;
    }

    public static Realm getRecentListenRealm() {
        return recent_listen_realm;
    }
}

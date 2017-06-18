package com.example.serenade.serenade.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.serenade.serenade.application.MyApplication;
import com.example.serenade.serenade.base.EventCenter;
import com.example.serenade.serenade.bean.Song;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

public class PlayService extends Service {
    private MediaPlayer mPlayer;
    private Song song;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        int code = event.getCode();
        switch (code) {
            case 102:
                song = (Song) event.getData();
                stopAndRelease();
                initPlayer();
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        song = (Song) intent.getExtras().getSerializable("song");
        stopAndRelease();
        initPlayer();
        return super.onStartCommand(intent, flags, startId);
    }

    public Song getSongInfo() {
        return song;
    }

    public int getDuration() {
        if (mPlayer != null)
            return mPlayer.getDuration();
        return 0;
    }

    public int getCurrentPosition() {
        if (mPlayer != null)
            return mPlayer.getCurrentPosition();
        return 0;
    }

    public void stopAndRelease() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void pausePlay() {
        if (mPlayer != null && mPlayer.isPlaying())
            mPlayer.pause();
    }

    public boolean isPlaying() {
        if (mPlayer != null)
            return mPlayer.isPlaying();
        return false;
    }

    public void startPlay() {
        if (mPlayer != null && !mPlayer.isPlaying())
            mPlayer.start();
    }

    public void initPlayer() {
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(song.getM4a());
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    song.setDuration(getDuration());
                    Realm recent = MyApplication.getRecentListenRealm();
                    RealmResults<Song> all = recent.where(Song.class).equalTo("songid", song.getSongid()).findAll();
                    if (all == null || all.size() == 0) {
                        recent.beginTransaction();
                        recent.copyToRealm(song);
                        recent.commitTransaction();
                    }
                    mp.start();
                }
            });
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void seekTo(int msec) {
        if (mPlayer != null)
            mPlayer.seekTo(msec);
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}

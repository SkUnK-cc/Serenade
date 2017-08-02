package com.example.serenade.serenade.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.serenade.serenade.R;
import com.example.serenade.serenade.activity.PlayActivity;
import com.example.serenade.serenade.application.MyApplication;
import com.example.serenade.serenade.base.BaseBinder;
import com.example.serenade.serenade.base.BaseService;
import com.example.serenade.serenade.base.EventCenter;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.callback.OnPlayStateListener;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

public class PlayService extends BaseService {
    private MediaPlayer mPlayer;
    private Song song;
    private OnPlayStateListener mOnPlayStateListener;
    public static final int NOTIFICATION_ID = 1;
    private Notification notification;
    public static final String START_OR_PAUSE = "action_start_or_pause";
    public static final String LAST = "action_last";
    public static final String NEXT = "action_next";
    private NotificationManager notificationManager;
    private PlayStateReceiver playStateReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder(this);
    }

    protected void onEventBusResult(EventCenter event) {
        int code = event.getCode();
        switch (code) {
            case 102:
                song = (Song) event.getData();
                stop();
                initPlayer();
                break;
        }
    }

    @Override
    public boolean registerEventBus() {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        song = (Song) intent.getExtras().getSerializable("song");
        stop();
        initPlayer();
        Notification.Builder builder = new Notification.Builder(this);
        RemoteViews content = new RemoteViews(getPackageName(), R.layout.notification_content);

        content.setTextViewText(R.id.song, song.getSongname());
        content.setTextViewText(R.id.singer, song.getSingername());

        Intent image_intent = new Intent(getApplicationContext(), PlayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 100, image_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        content.setOnClickPendingIntent(R.id.image, pendingIntent);
        content.setOnClickPendingIntent(R.id.linearLayout, pendingIntent);

        builder.setContent(content)
                .setSmallIcon(R.mipmap.app_icon);
        notification = builder.build();

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(NOTIFICATION_ID, notification);

        Intent controlIntent = new Intent(START_OR_PAUSE);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, controlIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        content.setOnClickPendingIntent(R.id.startOrPause, pendingIntent);

        controlIntent.setAction(LAST);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, controlIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        content.setOnClickPendingIntent(R.id.last, pendingIntent);

        controlIntent.setAction(NEXT);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, controlIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        content.setOnClickPendingIntent(R.id.next, pendingIntent);

        NotificationTarget notificationTarget = new NotificationTarget(this, content, R.id.image, notification, NOTIFICATION_ID);
        Glide.with(this)
                .load(song.getAlbumpic_big())
                .asBitmap()
                .placeholder(R.mipmap.app_icon)
                .error(R.mipmap.app_icon)
                .into(notificationTarget);
        if (playStateReceiver==null) {
            IntentFilter startOrPauseFilter = new IntentFilter(START_OR_PAUSE);
            IntentFilter lastFilter = new IntentFilter(LAST);
            IntentFilter nextFilter = new IntentFilter(NEXT);
            playStateReceiver = new PlayStateReceiver();
            registerReceiver(playStateReceiver, startOrPauseFilter);
            registerReceiver(playStateReceiver, lastFilter);
            registerReceiver(playStateReceiver, nextFilter);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(playStateReceiver);
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

    public void stop(){
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
        }
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
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            changeNotificationPlayState(false);
        }
    }

    public boolean isPlaying() {
        if (mPlayer != null)
            return mPlayer.isPlaying();
        return false;
    }

    public void startPlay() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
            changeNotificationPlayState(true);
        }
    }

    public void initPlayer() {
        try {
            if (mPlayer==null) {
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        song.setDuration(getDuration());
                        Realm recent = MyApplication.getPlayListRealm();
                        RealmResults<Song> all = recent.where(Song.class).equalTo("songid", song.getSongid()).findAll();
                        if (all == null || all.size() == 0) {
                            recent.beginTransaction();
                            recent.copyToRealm(song);
                            recent.commitTransaction();
                        }
                        mp.start();
                        if (mOnPlayStateListener != null)
                            mOnPlayStateListener.onStart();
                    }
                });
            }
            mPlayer.setDataSource(song.getM4a());
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void seekTo(int msec) {
        if (mPlayer != null)
            mPlayer.seekTo(msec);
    }

    public class PlayBinder extends BaseBinder<PlayService> {

        public PlayBinder(PlayService service) {
            super(service);
        }
    }

    public void setOnPlayStateListener(OnPlayStateListener listener) {
        this.mOnPlayStateListener = listener;
    }

    public OnPlayStateListener getOnPlayStateListener() {
        return mOnPlayStateListener;
    }

    class PlayStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (START_OR_PAUSE.equals(action)) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    if (mOnPlayStateListener != null)
                        mOnPlayStateListener.onPause();
                    changeNotificationPlayState(false);
                } else {
                    mPlayer.start();
                    if (mOnPlayStateListener != null)
                        mOnPlayStateListener.onStart();
                    changeNotificationPlayState(true);
                }
            } else if (LAST.equals(action)) {
                Toast.makeText(context, "点击了上一首", Toast.LENGTH_SHORT).show();
            } else if (NEXT.equals(action)) {
                Toast.makeText(context, "点击了下一首", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeNotificationPlayState(boolean b) {
        if (b) {
            notification.contentView.setImageViewResource(R.id.startOrPause, R.mipmap.pause);
        } else {
            notification.contentView.setImageViewResource(R.id.startOrPause, R.mipmap.play);
        }
        notificationManager.notify(NOTIFICATION_ID,notification);
    }
}

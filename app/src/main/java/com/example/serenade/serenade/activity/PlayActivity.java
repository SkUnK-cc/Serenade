package com.example.serenade.serenade.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.serenade.serenade.R;
import com.example.serenade.serenade.application.MyApplication;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.bean.Lyric;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.callback.OnPlayStateListener;
import com.example.serenade.serenade.retrofit.BaseCall;
import com.example.serenade.serenade.retrofit.BaseResponse;
import com.example.serenade.serenade.retrofit.RetrofitHelper;
import com.example.serenade.serenade.service.PlayService;
import com.example.serenade.serenade.utils.TimeParseUtil;
import com.example.serenade.serenade.widget.LyricView;
import com.example.serenade.serenade.window.RecentPlayListWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import cn.sharesdk.onekeyshare.OnekeyShare;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.serenade.serenade.R.mipmap.play;

public class PlayActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, LyricView.IndicatorListener, SeekBar.OnSeekBarChangeListener, LyricView.OnProgressChangedListener, AdapterView.OnItemClickListener {
    @BindView(R.id.startOrPause)
    ImageView startOrPause;
    @BindView(R.id.last)
    ImageView last;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.playMode)
    ImageView playMode;
    @BindView(R.id.playList)
    ImageView playList;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.current)
    TextView current;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.progress)
    SeekBar progress;
    @BindView(R.id.lyricView)
    LyricView lyricView;
    @BindView(R.id.singer)
    TextView singer;
    @BindView(R.id.song)
    TextView song;
    @BindView(R.id.background)
    ImageView background;
    @BindView(R.id.controller)
    LinearLayout controller;

    private PlayService playService;
    private PlayServiceConnection connection;
    private RecentPlayListWindow window;
    private List<Song> recentList;

    @Override
    public int setLayout() {
        return R.layout.activity_play;
    }

    @Override
    public void initEvent() {
        startOrPause.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);
        playMode.setOnClickListener(this);
        playList.setOnClickListener(this);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolBar.setOnMenuItemClickListener(this);
        lyricView.setOnIndicatorPlayListener(this);
        progress.setOnSeekBarChangeListener(this);
        lyricView.setOnProgressChangedListener(this);
    }

    @Override
    public void init() {
        toolBar.inflateMenu(R.menu.share);
        setStatusBarColor(Color.parseColor("#00000000"));
        getBackgroundImageView(this).setColorFilter(Color.parseColor("#BB000000"));
        Intent intent = new Intent(this, PlayService.class);
        connection = new PlayServiceConnection();
        bindService(intent, connection, BIND_AUTO_CREATE);

        recentList = new ArrayList<>();
        window = new RecentPlayListWindow(this, recentList);
        window.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.playMode:
                int i = new Random().nextInt(4);
                showToast("点击了播放模式");
                switch (i) {
                    case 0:
                        playMode.setImageResource(R.mipmap.song_recycle);
                        break;
                    case 1:
                        playMode.setImageResource(R.mipmap.list_play);
                        break;
                    case 2:
                        playMode.setImageResource(R.mipmap.list_recycle);
                        break;
                    case 3:
                        playMode.setImageResource(R.mipmap.random_play);
                        break;
                }
                break;
            case R.id.last:
                showToast("点击了上一首");
                break;
            case R.id.startOrPause:
                if (playService.isPlaying()) {
                    playService.pausePlay();
                    lyricView.stop();
                    startOrPause.setImageResource(play);
                } else {
                    playService.startPlay();
                    int currentPosition = playService.getCurrentPosition();
                    lyricView.setCurrentPosition(currentPosition);
                    lyricView.start();
                    startOrPause.setImageResource(R.mipmap.pause);
                }
                break;
            case R.id.next:
                showToast("点击了下一首");
                break;
            case R.id.playList:
                Realm recent = MyApplication.getRecentListenRealm();
                RealmResults<Song> all = recent.where(Song.class).findAll();
                recentList.clear();
                recentList.addAll(all);
                window.initLocation();
                if (!window.isShowing())
                    window.showAsDropDown(controller, 0, -controller.getMeasuredHeight() - window.getHeight());
                else
                    window.dismiss();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        showToast("点击了分享");
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("我正在Serenade听"+"\""+playService.getSongInfo().getAlbumname()+"\"");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(playService.getSongInfo().getM4a());
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我正在Serenade听"+"\""+playService.getSongInfo().getAlbumname()+"\"");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("file:///android_asset/app_icon.png");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(playService.getSongInfo().getM4a());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(playService.getSongInfo().getM4a());

        // 启动分享GUI
        oks.show(this);
        return true;
    }

    @Override
    public void onPlayClick(int position) {
        if (playService != null)
            playService.seekTo(position);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        lyricView.pauseProgressListener(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        lyricView.setCurrentPosition(progress);
        lyricView.pauseProgressListener(false);
        playService.seekTo(progress);
    }

    @Override
    public void OnProgressChanged(int progress) {
        String currentPosition = TimeParseUtil.parse(progress);
        current.setText(currentPosition);
        this.progress.setProgress(progress);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song info = recentList.get(position);
        Song temp = MyApplication.getRecentListenRealm().copyFromRealm(info);

        unbindService(connection);
        lyricView.stop();
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", temp);
        startService(PlayService.class, bundle);

        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder binder = (PlayService.PlayBinder) service;
            playService = binder.getService();
            Song songInfo = playService.getSongInfo();
            int duration = playService.getDuration();
            final int currentPosition = playService.getCurrentPosition();
            lyricView.setDuration(duration);
            progress.setMax(duration);
            progress.setProgress(currentPosition);
            singer.setText(songInfo.getSingername());
            song.setText(songInfo.getSongname());
            String tot = TimeParseUtil.parse(duration);
            total.setText(tot);
            String cur = TimeParseUtil.parse(currentPosition);
            current.setText(cur);

            playService.setOnPlayStateListener(new OnPlayStateListener() {
                @Override
                public void onStart() {
                    startOrPause.setImageResource(R.mipmap.pause);
                    if (!lyricView.isStarted())
                        lyricView.start();
                }
            });

            Glide.with(PlayActivity.this).load(songInfo.getAlbumpic_big()).dontAnimate().into(getBackgroundImageView(PlayActivity.this));
            BaseCall<BaseResponse<Lyric>> call = RetrofitHelper.getApi().queryLyric(songInfo.getSongid() + "");
            call.record(PlayActivity.class).enqueue(new Callback<BaseResponse<Lyric>>() {
                @Override
                public void onResponse(Call<BaseResponse<Lyric>> call, Response<BaseResponse<Lyric>> response) {
                    BaseResponse<Lyric> body = response.body();
                    String lyric = body.data.getLyric();
                    lyric = Html.fromHtml(lyric).toString();
                    lyric = lyric.replace("&apos;", "'");
                    lyric = lyric.replace("[", "\n\t[");
                    lyricView.setLyric(lyric);
                    lyricView.setCurrentPosition(currentPosition);
                    if (playService.isPlaying()) {
                        startOrPause.setImageResource(R.mipmap.pause);
                        if (!lyricView.isStarted())
                            lyricView.start();
                    } else {
                        startOrPause.setImageResource(R.mipmap.play);
                        lyricView.stop();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<Lyric>> call, Throwable t) {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            lyricView.stop();
        }
    }
}

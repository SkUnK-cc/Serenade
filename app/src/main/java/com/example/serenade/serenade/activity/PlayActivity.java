package com.example.serenade.serenade.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.serenade.serenade.R;
import com.example.serenade.serenade.adapter.PlayListAdapter;
import com.example.serenade.serenade.application.MyApplication;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.bean.Lyric;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.callback.OnItemClickListener;
import com.example.serenade.serenade.callback.OnPlayStateListener;
import com.example.serenade.serenade.dialog.PlayListDialog;
import com.example.serenade.serenade.itemdecoration.RecyclerViewDivider;
import com.example.serenade.serenade.retrofit.BaseCall;
import com.example.serenade.serenade.retrofit.BaseResponse;
import com.example.serenade.serenade.retrofit.RetrofitHelper;
import com.example.serenade.serenade.service.PlayService;
import com.example.serenade.serenade.utils.TimeParseUtil;
import com.example.serenade.serenade.widget.LyricView;

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

public class PlayActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, LyricView.IndicatorListener, SeekBar.OnSeekBarChangeListener, LyricView.OnProgressChangedListener, OnItemClickListener {
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
    @BindView(R.id.controller)
    LinearLayout controller;

    private PlayService play_service;
    private PlayServiceConnection connection;
    private List<Song> play_list;
    private PlayListDialog play_list_dialog;
    private PlayListAdapter adapter;

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
        toolBar.setNavigationOnClickListener(this);
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

        play_list = new ArrayList<>();
//        play_list_dialog = new BottomSheetDialog(this);
        play_list_dialog = new PlayListDialog(this);
        View play_list_content = LayoutInflater.from(this).inflate(R.layout.play_list, null);
        RecyclerView recyclerView = (RecyclerView)play_list_content.findViewById(R.id.list) ;
        ImageView close = (ImageView) play_list_content.findViewById(R.id.close);
        close.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new PlayListAdapter(this, play_list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerViewDivider(false));
        play_list_dialog.setContentView(play_list_content);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        lyricView.setOnProgressChangedListener(null);
        lyricView.setOnIndicatorPlayListener(null);
        play_service.setOnPlayStateListener(null);
        lyricView.stop();
        super.onDestroy();
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
                if (play_service.isPlaying()) {
                    play_service.pausePlay();
                    lyricView.stop();
                    startOrPause.setImageResource(play);
                } else {
                    play_service.startPlay();
                    int currentPosition = play_service.getCurrentPosition();
                    lyricView.setCurrentPosition(currentPosition);
                    lyricView.start();
                    startOrPause.setImageResource(R.mipmap.pause);
                }
                break;
            case R.id.next:
                showToast("点击了下一首");
                break;
            case R.id.playList:
                Realm recent = MyApplication.getPlayListRealm();
                RealmResults<Song> all = recent.where(Song.class).findAll();
                play_list.clear();
                play_list.addAll(all);
                play_list_dialog.show();
                break;
            case R.id.close:
                play_list_dialog.dismiss();
                break;
            default:
                finish();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("我正在Serenade听" + "《" + play_service.getSongInfo().getSongname() + "》");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(play_service.getSongInfo().getM4a());
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我正在Serenade听" + "《" + play_service.getSongInfo().getSongname() + "》");
        // imageUrl是图片的地址，Linked-In以外的平台都支持此参数
        oks.setImageUrl(play_service.getSongInfo().getAlbumpic_small());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(play_service.getSongInfo().getM4a());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(" ");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(play_service.getSongInfo().getM4a());

        // 启动分享GUI
        oks.show(this);
        return true;
    }

    @Override
    public void onPlayClick(int position) {
        if (play_service != null)
            play_service.seekTo(position);
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
        play_service.seekTo(progress);
    }

    @Override
    public void OnProgressChanged(int progress) {
        String currentPosition = TimeParseUtil.parse(progress);
        current.setText(currentPosition);
        this.progress.setProgress(progress);
    }

    @Override
    public void onItemClick(View view, int position) {
        Song info = play_list.get(position);
        Song temp = MyApplication.getPlayListRealm().copyFromRealm(info);

        unbindService(connection);
        lyricView.stopAndClear();
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", temp);
        startService(PlayService.class, bundle);

        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        play_list_dialog.dismiss();
    }

    class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder binder = (PlayService.PlayBinder) service;
            play_service = binder.getService();
            Song songInfo = play_service.getSongInfo();
            int duration = play_service.getDuration();
            lyricView.setDuration(duration);
            progress.setMax(duration);
            singer.setText(songInfo.getSingername());
            song.setText(songInfo.getSongname());
            String tot = TimeParseUtil.parse(duration);
            total.setText(tot);

            play_service.setOnPlayStateListener(new OnPlayStateListener() {
                @Override
                public void onStart() {
                    startOrPause.setImageResource(R.mipmap.pause);
                    if (!lyricView.isStarted())
                        lyricView.start();
                }

                @Override
                public void onPause() {
                    startOrPause.setImageResource(R.mipmap.play);
                    if (lyricView.isStarted())
                        lyricView.stop();
                }
            });

            Glide.with(PlayActivity.this).load(songInfo.getAlbumpic_big()).dontAnimate().placeholder(getBackgroundImageView(PlayActivity.this).getDrawable()).into(getBackgroundImageView(PlayActivity.this));
            BaseCall<BaseResponse<Lyric>> call = RetrofitHelper.getApi().queryLyric(songInfo.getSongid() + "");
            call.record(PlayActivity.class).enqueue(new Callback<BaseResponse<Lyric>>() {
                @Override
                public void onResponse(Call<BaseResponse<Lyric>> call, Response<BaseResponse<Lyric>> response) {
                    BaseResponse<Lyric> body = response.body();
                    String lyric = body.data.getLyric();
                    if (!TextUtils.isEmpty(lyric)) {
                        lyric = Html.fromHtml(lyric).toString();
                        lyric = lyric.replace("&apos;", "'");
                        lyric = lyric.replace("[", "\n\t[");
                    }
                    lyricView.setLyric(lyric);
                    int currentPosition = play_service.getCurrentPosition();
                    lyricView.setCurrentPosition(currentPosition);
                    progress.setProgress(currentPosition);
                    String cur = TimeParseUtil.parse(currentPosition);
                    current.setText(cur);
                    if (play_service.isPlaying()) {
                        startOrPause.setImageResource(R.mipmap.pause);
                        if (!lyricView.isStarted())
                            lyricView.start();
                    } else {
                        startOrPause.setImageResource(R.mipmap.play);
                        if (lyricView.isStarted())
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

package com.example.serenade.serenade.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.serenade.serenade.R;
import com.example.serenade.serenade.adapter.RecommendListAdapter;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.base.EventCenter;
import com.example.serenade.serenade.bean.Recommend;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.bean.User;
import com.example.serenade.serenade.callback.OnItemClickListener;
import com.example.serenade.serenade.retrofit.BaseResponse;
import com.example.serenade.serenade.retrofit.RetrofitHelper;
import com.example.serenade.serenade.service.PlayService;
import com.example.serenade.serenade.utils.img.GlideRoundTransform;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, View.OnClickListener, OnItemClickListener, XRecyclerView.LoadingListener {
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.recommend_list)
    XRecyclerView recommend_list;

    SearchView searchView;
    MenuItem menuItem;
    private View headerView;
    private ImageView head;
    private TextView userName;
    private List<Song> recommendData;
    private RecommendListAdapter adapter;

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initEvent() {
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mToolbar.setNavigationOnClickListener(this);
        headerView = navigationView.getHeaderView(0);
        head = (ImageView) headerView.findViewById(R.id.head);
        userName = (TextView) headerView.findViewById(R.id.username);
        head.setOnClickListener(this);
        userName.setOnClickListener(this);
    }

    @Override
    public void init() {
        setStatusBarColor(Color.parseColor("#005CACEE"));
        mToolbar.inflateMenu(R.menu.toolbar_menu);
        Menu menu = mToolbar.getMenu();
        //在菜单中找到对应控件的item
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("请输入歌曲");
        recommendData = new ArrayList<>();
        adapter = new RecommendListAdapter(this, recommendData);
        recommend_list.setAdapter(adapter);
        recommend_list.setLayoutManager(new GridLayoutManager(this, 2));
        adapter.setOnItemClickListener(this);
        recommend_list.setLoadingListener(this);
        getRecommend();
        recommend_list.setLoadingMoreEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public boolean registerEventBus() {
        return true;
    }

    @Override
    protected void onEventBusResult(EventCenter event) {
        super.onEventBusResult(event);
        switch (event.getCode()) {
            case 100:
                userName.setText(User.getInstance().getUsername());
                Glide.with(this)
                        .load(User.getInstance().getHead())
                        .placeholder(R.mipmap.app_icon)
                        .error(R.mipmap.app_icon)
                        .transform(new GlideRoundTransform(this))
                        .into(head);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.head:
            case R.id.username:
                startActivity(LoginActivity.class);
                break;
            default:
                drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
        } else if (menuItem.isActionViewExpanded()) {
            menuItem.collapseActionView();
        } else {
            finish();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            Bundle bundle = new Bundle();
            bundle.putString("song", query);
            startActivity(SearchActivity.class, bundle);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        Song bean = recommendData.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", bean);
        startService(PlayService.class, bundle);
    }

    public void getRecommend() {
        RetrofitHelper.getApi()
                .getRecommendList("5")
                .record(getClass())
                .enqueue(new Callback<BaseResponse<Recommend>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Recommend>> call, Response<BaseResponse<Recommend>> response) {
                        Recommend recommend = response.body().data;
                        List<Recommend.PagebeanBean.SonglistBeanX> songList = recommend.getPagebean().getSonglist();
                        for (Recommend.PagebeanBean.SonglistBeanX bean : songList) {
                            Song song = new Song();
                            song.setM4a(bean.getUrl());
                            song.setAlbummid(bean.getAlbummid());
                            song.setAlbumid(bean.getAlbumid());
                            song.setAlbumpic_big(bean.getAlbumpic_big());
                            song.setAlbumpic_small(bean.getAlbumpic_small());
                            song.setSongid(bean.getSongid());
                            song.setSongname(bean.getSongname());
                            song.setSingername(bean.getSingername());
                            song.setSingerid(bean.getSingerid());
                            recommendData.add(song);
                        }
                        adapter.notifyDataSetChanged();
                        recommend_list.refreshComplete();
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Recommend>> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onRefresh() {
        recommendData.clear();
        adapter.notifyDataSetChanged();
        getRecommend();
    }

    @Override
    public void onLoadMore() {

    }
}

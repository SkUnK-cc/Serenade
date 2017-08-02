package com.example.serenade.serenade.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.adapter.SongListAdapter;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.bean.SongBean;
import com.example.serenade.serenade.callback.OnItemClickListener;
import com.example.serenade.serenade.itemdecoration.RecyclerViewDivider;
import com.example.serenade.serenade.retrofit.BaseCall;
import com.example.serenade.serenade.retrofit.BaseResponse;
import com.example.serenade.serenade.retrofit.RetrofitHelper;
import com.example.serenade.serenade.service.PlayService;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener, OnItemClickListener {
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.search)
    TextView search;
    @BindView(R.id.list)
    XRecyclerView list;

    private SongListAdapter adapter;
    private List<Song> data;
    private LinearLayoutManager manager;

    @Override
    public int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void initEvent() {
        input.setOnEditorActionListener(this);
        search.setOnClickListener(this);
    }

    @Override
    public void init() {
        String song = getIntent().getExtras().getString("song");
        input.setText(song);
        input.setSelection(song.length());
        hideKeyboard(input);
        data = new ArrayList<>();
        adapter = new SongListAdapter(this, data);
        adapter.setOnItemClickListener(this);
        list.setAdapter(adapter);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);
        list.addItemDecoration(new RecyclerViewDivider(true));
        querySong(song);
    }

    public void querySong(String song) {
        BaseCall<BaseResponse<SongBean>> call = RetrofitHelper.getApi().querySong(song);
        call.record(getClass()).enqueue(new Callback<BaseResponse<SongBean>>() {
            @Override
            public void onResponse(Call<BaseResponse<SongBean>> call, Response<BaseResponse<SongBean>> response) {
                BaseResponse<SongBean> body = response.body();
                SongBean songBean = body.data;
                SongBean.PagebeanBean pagebean = songBean.getPagebean();
                List<Song> been = pagebean.getContentlist();
                data.clear();
                data.addAll(been);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<BaseResponse<SongBean>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.search:
                hideKeyboard(input);
                String song = input.getText().toString();
                querySong(song);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard(input);
        String song = input.getText().toString();
        querySong(song);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        Song bean = data.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", bean);
        startService(PlayService.class, bundle);
    }
}

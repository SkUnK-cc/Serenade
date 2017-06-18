package com.example.serenade.serenade.activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.adapter.OnItemClickListener;
import com.example.serenade.serenade.adapter.SongListAdapter;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.bean.SongBean;
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

public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener {
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
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Song bean = data.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("song", bean);
                startService(PlayService.class, bundle);
            }
        });
        list.setAdapter(adapter);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);

        list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                if (manager.findLastVisibleItemPosition() != adapter.getItemCount()) {
                    // recyclerView是否设置了paddingLeft和paddingRight
                    int left = parent.getPaddingLeft();
                    int right = parent.getWidth() - parent.getPaddingRight();
                    int childCount = parent.getChildCount();

                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setColor(Color.GRAY);
                    for (int i = 1; i < childCount - 2; i++) {
                        View child = parent.getChildAt(i);
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                                .getLayoutParams();
                        // divider的top 应该是 item的bottom 加上 marginBottom 再加上 Y方向上的位移
                        int top = child.getBottom() + params.bottomMargin +
                                Math.round(ViewCompat.getTranslationY(child));
                        // divider的bottom就是top加上divider的高度了
                        int bottom = (int) (top + 1);
                        c.drawRect(left, top, right, bottom, paint);
                    }
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (manager.findLastVisibleItemPosition() != adapter.getItemCount())
                    outRect.bottom = 1;
            }
        });
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
}

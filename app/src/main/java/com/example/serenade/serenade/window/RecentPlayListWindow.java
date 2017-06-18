package com.example.serenade.serenade.window;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.adapter.RecentListAdapter;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.utils.ListViewUtil;

import java.util.List;

/**
 * Created by Serenade on 17/6/18.
 */

public class RecentPlayListWindow extends PopupWindow {
    private Context mContext;
    private LayoutInflater mInflater;
    private View mContentView;
    private List<Song> mData;
    private ListView mList;
    private RecentListAdapter mAdapter;
    private int height=0;

    public RecentPlayListWindow(Context context, List<Song> data) {
        super(context);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mContentView = mInflater.inflate(R.layout.recent_list_window, null);
        mList = (ListView) mContentView.findViewById(R.id.list);
        mData = data;

        //设置View
        setContentView(mContentView);

        mAdapter = new RecentListAdapter(context, data);
        mList.setAdapter(mAdapter);

        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        //设置进出动画
//        setAnimationStyle(R.style.RecentListWindow);
    }

    public void initLocation(){
        mAdapter.notifyDataSetChanged();
        height = ListViewUtil.setListViewHeightBasedOnChildren(mList, 5);
        Log.e("hahahha","="+height);
        setHeight(height);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mList.setOnItemClickListener(listener);
    }

}

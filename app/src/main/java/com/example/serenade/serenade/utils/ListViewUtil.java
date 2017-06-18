package com.example.serenade.serenade.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Serenade on 2017/6/18.
 */

public class ListViewUtil {
    /**
     * 设置可见item数量并设置listview高度
     *
     * @param listView
     * @return listView的高度
     */
    public static int setListViewHeightBasedOnChildren(ListView listView, int visibleCount) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        int count = listAdapter.getCount() > visibleCount ? visibleCount : listAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (count - 1));
        listView.setLayoutParams(params);
        return params.height;
    }
}

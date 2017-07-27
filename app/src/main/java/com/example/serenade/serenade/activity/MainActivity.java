package com.example.serenade.serenade.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.base.EventCenter;

import butterknife.BindView;


public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.gotoplay)
    Button gotoplay;

    SearchView searchView;
    MenuItem menuItem;

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initEvent() {
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        gotoplay.setOnClickListener(this);
    }

    @Override
    public void init() {
        setStatusBarColor(Color.parseColor("#770000FF"));
        mToolbar.inflateMenu(R.menu.toolbar_menu);
        Menu menu = mToolbar.getMenu();
        //在菜单中找到对应控件的item
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("请输入歌曲");
    }

    @Override
    protected void onEventBusResult(EventCenter event) {
        super.onEventBusResult(event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.gotoplay:
                startActivity(PlayActivity.class);
                break;
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
        } else
            showToast("搜索内容不能为空");
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}

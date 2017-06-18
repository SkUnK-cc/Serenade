package com.example.serenade.serenade.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.base.EventCenter;

import butterknife.BindView;


public class MainActivity extends BaseActivity implements TextView.OnEditorActionListener, Toolbar.OnMenuItemClickListener {
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.gotoplay)
    Button gotoplay;

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initEvent() {
        input.setOnEditorActionListener(this);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        toolBar.setOnMenuItemClickListener(this);
        gotoplay.setOnClickListener(this);
    }

    @Override
    public void init() {
        setStatusBarColor(Color.parseColor("#770000FF"));
        toolBar.inflateMenu(R.menu.search);
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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideKeyboard(input);
        querySong();
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawers();
        else
            finish();
    }

    private void querySong() {
        String song = input.getText().toString().trim();
        if (!TextUtils.isEmpty(song)) {
            Bundle bundle = new Bundle();
            bundle.putString("song", song);
            startActivity(SearchActivity.class, bundle);
        } else
            showToast("搜索内容不能为空");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        hideKeyboard(input);
        querySong();
        return true;
    }
}

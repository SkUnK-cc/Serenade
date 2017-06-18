package com.example.serenade.serenade.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.serenade.serenade.base.mvpbase.MVPBaseActivity;
import com.example.serenade.serenade.retrofit.NetworkManager;
import com.example.serenade.serenade.utils.app.AppManager;
import com.example.serenade.serenade.utils.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private Unbinder mBind;
    View mRootView;
    protected Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册EventBus
        EventBus.getDefault().register(this);
        //取消ActionBar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置界面随键盘弹出自动上移
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mRootView = LayoutInflater.from(this).inflate(setLayout(), null);
        //设置布局
        setContentView(mRootView);
        //添加BackgroundImageView
        addBackgroundImageView(this);
        //绑定ButterKnife
        mBind = ButterKnife.bind(this);
        //添加Activity到管理堆栈
        AppManager.getInstance().addActivity(this);
        //设置UI
        initEvent();
        //初始化
        init();
    }

    /**
     * 设置布局
     *
     * @return 布局资源id
     */
    public abstract
    @LayoutRes
    int setLayout();

    /**
     * 设置监听
     */
    public abstract void initEvent();

    /**
     * 初始化
     */
    public abstract void init();


    /**
     * 获得根布局
     *
     * @return
     */
    public View getRootView() {
        return mRootView;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();//解除ButterKnife绑定
        EventBus.getDefault().unregister(this);//解除EventBus注册
        //从管理堆栈移除Activity
        AppManager.getInstance().finishActivity(this);
        //如果不是MVP模式，则取消记录的网络请求
        if (!(this instanceof MVPBaseActivity)) {
            NetworkManager.getInstance().cancelRequests(getClass().getName());
        }
    }

    /**
     * 隐藏输入法
     *
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示输入法
     *
     * @param view
     */
    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(view, 0);
    }

    /**
     * 获得StatusBar的高度
     *
     * @return StatusBar的高度
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置StatusBar透明
     */
    public void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void startActivity(Class clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startActivity(Class clz) {
        Intent intent = new Intent(this, clz);
        startActivity(intent);
    }

    public void startActivityForResult(Class clz, int requestCode) {
        Intent intent = new Intent(this, clz);
        startActivityForResult(intent, requestCode);
    }

    public void startActivityForResult(Class clz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, clz);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    public void startService(Class clz) {
        Intent intent = new Intent(this, clz);
        startService(intent);
    }

    public void startService(Class clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null)
            intent.putExtras(bundle);
        startService(intent);
    }

    public void initToolbar(@IdRes int id) {
        mToolbar = (Toolbar) findViewById(id);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public void setToolbarIndicator(@DrawableRes int resId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(resId);
        }
    }

    public void setToolbarTitle(CharSequence str) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(str);
        }
    }

    public void setToolbarTitle(@StringRes int strId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(strId);
        }
    }

    public ActionBar getToolbar() {
        return getSupportActionBar();
    }

    public View getDecorView() {
        return getWindow().getDecorView();
    }

    @Subscribe
    protected void onReceiveEvent(EventCenter event) {
        onEventBusResult(event);
    }

    /**
     * EventBus回传消息重写方法
     *
     * @param event
     */
    protected void onEventBusResult(EventCenter event) {

    }


    /**
     * 显示进度框
     */
    public void showProgressDialog() {
        LoadingDialog.getInstance().show(this, "加载中...", false);
    }

    /**
     * 隐藏进度框
     */
    public void dismissProgressDialog() {
        LoadingDialog.getInstance().dismissDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismissProgressDialog();
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 颜色
     */
    public void setStatusBarColor(int color) {
        setTranslucentStatus();
        ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
        setupStatusBarView(this, contentLayout, color);
        View contentView = contentLayout.getChildAt(1);
        //如果是DrawerLayout,让内部第一个布局设置padding
        if (contentView instanceof DrawerLayout)
            contentView = ((DrawerLayout) contentView).getChildAt(0);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.setMargins(0, getStatusBarHeight(), 0, 0);
    }

    /**
     * 创建view替换StatusBar
     *
     * @param activity
     * @param contentLayout
     * @param color
     */
    private void setupStatusBarView(Activity activity, ViewGroup contentLayout, int color) {
        View statusBarView = new View(activity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        contentLayout.addView(statusBarView, lp);
        statusBarView.setBackgroundColor(color);
    }

    /**
     * 添加背景ImageView
     * @param activity
     */
    private void addBackgroundImageView(Activity activity){
        ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
        ImageView background = new ImageView(activity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        contentLayout.addView(background,0, lp);
    }

    public ImageView getBackgroundImageView(Activity activity){
        ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
        ImageView background = (ImageView) contentLayout.getChildAt(0);
        return background;
    }



    /**
     * Toast
     *
     * @param message 消息
     */
    public void showToast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }
}

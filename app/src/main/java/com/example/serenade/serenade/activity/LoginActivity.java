package com.example.serenade.serenade.activity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.base.BaseActivity;
import com.example.serenade.serenade.base.EventCenter;
import com.example.serenade.serenade.bean.User;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

public class LoginActivity extends BaseActivity implements PlatformActionListener {

    @BindView(R.id.qq)
    ImageView qq;
    @BindView(R.id.weibo)
    ImageView weibo;

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initEvent() {
        qq.setOnClickListener(this);
        weibo.setOnClickListener(this);
    }

    @Override
    public void init() {

    }

    @Override
    public boolean registerEventBus() {
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.qq:
                login(QQ.NAME);
                break;
            case R.id.weibo:
                login(SinaWeibo.NAME);
                break;
        }
    }

    public void login(String name) {
        Platform mPlatform = ShareSDK.getPlatform(name);
        mPlatform.setPlatformActionListener(this);
        mPlatform.SSOSetting(false);
        mPlatform.authorize();//单独授权,OnComplete返回的hashmap是空的
//        mPlatform.showUser(null);//授权并获取用户信息
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Log.e("登录成功","========");
        showToast("登录成功");
        Log.e("openid", "===="+platform.getDb().getUserId()); //拿到登录后的openid
        Log.e("username", "===="+platform.getDb().getUserName()); //拿到登录用户的昵称
        Log.e("icon", "===="+platform.getDb().getUserIcon()); //拿到登录用户的头像
        User.getInstance().setHead(platform.getDb().getUserIcon());
        User.getInstance().setUsername(platform.getDb().getUserName());
        EventBus.getDefault().post(new EventCenter<>(100));
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        showToast("登录失败");
        Log.e("登录失败","========");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        showToast("取消登录");
        Log.e("取消登录","========");
    }
}

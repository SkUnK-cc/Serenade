package com.example.serenade.serenade.utils.app;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Serenade on 17/2/3.
 */

public class AppManager {
    private static Stack<Activity> activities;

    /**
     * 构造方法私有化
     */
    private AppManager() {
    }

    /**
     * 实例化单例的静态内部类
     */
    private static class AppManagerHolder {
        public static final AppManager instance = new AppManager();
    }

    /**
     * 获取单一实例
     *
     * @return 单一实例
     */
    public static AppManager getInstance() {
        return AppManagerHolder.instance;
    }

    /**
     * 添加Activity到堆栈
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activities == null)
            activities = new Stack<>();
        activities.add(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishTopActivity() {
        Activity activity = activities.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0; i < activities.size(); i++) {
            if (null != activities.get(i)) {
                activities.get(i).finish();
            }
        }
        activities.clear();
    }

    /**
     * 退出App
     */
    public void exitApp() {
        finishAllActivity();
        System.exit(0);
    }

    /**
     * 获取栈顶Activity
     */
    public Activity getTopActivity() {
        Activity activity = activities.lastElement();
        return activity;
    }

}

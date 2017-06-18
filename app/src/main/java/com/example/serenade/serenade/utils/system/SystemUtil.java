package com.example.serenade.serenade.utils.system;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.serenade.serenade.utils.app.AppManager;


public class SystemUtil {
    /**
     * 获取DP
     *
     * @param context 上下文对象
     * @return DP
     */
    public static float getDP(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context 上下文对象
     * @return 屏幕宽度
     */
    public static float getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context 上下文对象
     * @return 屏幕高度
     */
    public static float getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 判断APP是否在前台
     */
    public boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (currentPackageName != null && currentPackageName.equals("com.uou")) {
            return true;
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return 版本号
     * @throws Exception 获取版本号信息
     */
    public static String getVersionName(Context context) throws Exception {
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(
                context.getPackageName(), 0);
        return packInfo.versionName;
    }

    private static long exitCTime = 0;

    public static void exitApp(Context context) {
        if ((System.currentTimeMillis() - exitCTime) > 2000) {
            Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitCTime = System.currentTimeMillis();
        } else {
            AppManager.getInstance().exitApp();
        }
    }


    /**
     * 拨打电话
     *
     * @param mContext 上下文环境
     * @param phone    电话
     */
    public static void callPhone(Context mContext, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        mContext.startActivity(intent);
    }
}

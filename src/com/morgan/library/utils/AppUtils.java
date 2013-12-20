package com.morgan.library.utils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.Log;

import com.morgan.library.app.APPContext;

public class AppUtils {

    private static final String TAG = AppUtils.class.getName();

    public static void installLauncherShortCut(Intent intent, String shortCutName, Bitmap icon) {
        installLauncherShortCut(intent, shortCutName, icon, false);
    }

    public static void installLauncherShortCut(Intent intent, String shortCutName, Bitmap icon, boolean duplicate) {
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        shortCutIntent.putExtra("duplicate", duplicate);
        intent.setAction(Intent.ACTION_MAIN);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        APPContext.getContext().sendBroadcast(shortCutIntent);
    }

    /**
     * 获取App安装包信息
     * 
     * @return
     */
    public static PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = APPContext.getContext().getPackageManager()
                    .getPackageInfo(APPContext.getContext().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "获取安装包信息失败", e);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }
}

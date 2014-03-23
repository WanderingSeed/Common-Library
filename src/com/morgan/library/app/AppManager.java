package com.morgan.library.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * 用于管理所有的页面，特别是退出程序时保证能够完全退出程序。
 * 
 * @author morgan
 * 
 */
public class AppManager {

    private List<Activity> mActivities = new ArrayList<Activity>();
    private static AppManager mInstance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (mInstance == null) {
            mInstance = new AppManager();
        }
        return mInstance;
    }

    public void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivities.remove(activity);
    }

    public List<Activity> getActivities() {
        return mActivities;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = mActivities.size() - 1; i >= 0; i--) {
            if (null != mActivities.get(i)) {
                mActivities.get(i).finish();
            }
        }
        mActivities.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
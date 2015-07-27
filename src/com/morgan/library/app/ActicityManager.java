package com.morgan.library.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * 用于管理所有的页面，特别是退出程序时保证能够完全退出程序。
 * 
 * @author Morgan.Ji
 * 
 * @version 1.0
 * 
 * @date 2014年7月9日
 */
public class ActicityManager {

	private List<Activity> mActivities = new ArrayList<Activity>();
	private static ActicityManager mInstance;

	/**
	 * 私有构造函数保证单例。
	 */
	private ActicityManager() {
	}

	/**
	 * 获取AppManager的单例。
	 * 
	 * @return AppManager对象
	 */
	public static ActicityManager getInstance() {
		if (mInstance == null) {
			mInstance = new ActicityManager();
		}
		return mInstance;
	}

	/**
	 * 添加Activity。
	 * 
	 * @param activity
	 *            要添加的Activity
	 */
	public void addActivity(Activity activity) {
		mActivities.add(activity);
	}

	/**
	 * 删掉Activity。
	 * 
	 * @param activity
	 *            要删掉的Activity
	 */
	public void removeActivity(Activity activity) {
		mActivities.remove(activity);
	}

	/**
	 * 获取所有的Activity。
	 * 
	 * @return Activity列表
	 */
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
	 * 完全退出应用,包括杀掉后台进程。
	 * 
	 * @param context
	 *            该应用的Context
	 */
	public void exitApp(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.killBackgroundProcesses(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			// do nothing
		}
	}
}
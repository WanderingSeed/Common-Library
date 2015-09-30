package com.morgan.library.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.morgan.library.app.APPContext;

/**
 * 提供硬件相关的实用方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class HardwareUtils {

	/**
	 * 判断GPS是否可用
	 * 
	 * @return
	 */
	public static boolean isGPSAvailable() {
		LocationManager alm = (LocationManager) APPContext.getContext().getSystemService(
				Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 打开GPS配置界面
	 * 
	 * @param context
	 */
	public static void openGPS(Context context) {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(intent);
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) APPContext.getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取当前网络类型，-1为无可用网络
	 * 
	 * @return
	 */
	public static int getActiveNetworkType() {
		int defaultValue = -1;
		ConnectivityManager cm = (ConnectivityManager) APPContext.getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return defaultValue;
		}
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			return defaultValue;
		}
		return info.getType();
	}

	/**
	 * 当前WIFI是否可用
	 * 
	 * @return
	 */
	public static boolean isWifiActive() {
		ConnectivityManager connectivity = (ConnectivityManager) APPContext.getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info;
		if (connectivity != null) {
			info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getType() == ConnectivityManager.TYPE_WIFI
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

package com.morgan.library.service;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.morgan.library.R;
import com.morgan.library.app.APPContext;

/**
 * 提供定位功能（百度的定位包），如果对位置要求不严格，可以先使用{@link #getLastLocation()}来获取最后一次位置.
 * 
 * @author Morgan.Ji
 * 
 */
public class LocationManager {

	public static final double NO_LOCATION = 4.9E-324;
	private static LocationManager instance;
	private LocationClient mLocClient;
	private static final int minTime = 10 * 1000;
	private List<LocationCallBack> mCallback;
	private BDLocationListener mBDLocationListener;
	private BDLocation mLastLocation;

	private LocationManager() {
		mLocClient = new LocationClient(APPContext.getContext());
		mLastLocation = mLocClient.getLastKnownLocation();
		mCallback = new ArrayList<LocationCallBack>();
		setLocationOption();
		registerLocationListener();
	}

	/**
	 * 设置获取的位置信息类型
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		// 打开gps
		option.setOpenGps(true);
		option.setPriority(LocationClientOption.GpsFirst);
		// 设置坐标类型
		option.setCoorType("bd09ll");
		option.setAddrType("All");
		option.setScanSpan(minTime);
		option.setProdName(APPContext.getContext().getString(R.string.app_name));
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}

	public static LocationManager getInstance() {
		if (instance == null) {
			instance = new LocationManager();
		}
		return instance;
	}

	/**
	 * 使用百度定位端开始定位
	 */
	private void startLocClient() {
		if (mLocClient != null && !mLocClient.isStarted()) {
			mLocClient.start();
		}
	}

	/**
	 * 请求一次位置
	 */
	private void requestLocation() {
		if (mLocClient != null && !mLocClient.isStarted()) {
			mLocClient.requestLocation();
		}
	}

	/**
	 * 停止定位
	 */
	private void stopLocClient() {
		if (mLocClient != null) {
			mLocClient.stop();
		}
	}

	/**
	 * 获取最后一次的位置信息
	 * 
	 * @return
	 */
	public BDLocation getLastLocation() {
		return mLastLocation;
	}

	/**
	 * 注册位置监听器
	 */
	private void registerLocationListener() {
		if (mBDLocationListener == null) {
			mBDLocationListener = new MyLocationListenner();
			mLocClient.registerLocationListener(mBDLocationListener);
		}
	}

	/**
	 * 移除位置监听器
	 */
	public void destoryLocationListener() {
		mLocClient.unRegisterLocationListener(mBDLocationListener);
		mBDLocationListener = null;
	}

	/**
	 * 开始定位，如果只想获取一次位置信息，则需要在回调接口内移除回调
	 * 
	 * @param callback
	 *            获取到位置后的回调
	 */
	public void startLocate(LocationCallBack callback) {
		if (!mCallback.contains(callback)) {
			this.mCallback.add(callback);
		}
		startLocClient();
	}

	/**
	 * 移除定位后的回调接口
	 * 
	 * @param callback
	 */
	public void removeCallback(LocationCallBack callback) {
		this.mCallback.remove(callback);
		if (mCallback.size() == 0) {
			stopLocClient();
		}
	}

	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || location.getLatitude() == NO_LOCATION) {
				for (int i = mCallback.size() - 1; i >= 0; i--) {
					mCallback.get(i).onNoLocation();
				}
				if (mCallback.size() > 0) {
					requestLocation();
				}
			} else {
				updateLocation(location);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * 通知所有监听器当前的位置信息
	 * 
	 * @param location
	 */
	private void updateLocation(BDLocation location) {
		if (location.getAltitude() < 0 || location.getAltitude() == NO_LOCATION) {
			location.setAltitude(0);
		}
		for (int i = mCallback.size() - 1; i >= 0; i--) {
			mCallback.get(i).onReceiveLocation(location);
		}
		mLastLocation = location;
	}

	public interface LocationCallBack {
		/**
		 * 当前位置
		 * 
		 * @param location
		 */
		void onReceiveLocation(BDLocation location);

		/**
		 * 当获取位置失败时
		 */
		void onNoLocation();
	}
}

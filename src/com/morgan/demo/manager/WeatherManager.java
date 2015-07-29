package com.morgan.demo.manager;

import com.baidu.location.BDLocation;
import com.morgan.demo.model.Weather;
import com.morgan.demo.task.GetWeatherTask;
import com.morgan.library.async.IFeedback;
import com.morgan.library.service.LocationManager;
import com.morgan.library.utils.Logger;

/**
 * 从天气网站上抓取当天的天气预报。
 * 
 * @author Morgan.Ji
 * 
 */
public class WeatherManager {

	private static WeatherManager mInstance;
	private GetWeatherTask mGetWeatherTask;

	public static WeatherManager getInstance() {
		if (null == mInstance) {
			mInstance = new WeatherManager();
		}
		return mInstance;
	}

	private WeatherManager() {
	}

	/**
	 * 获取天气
	 */
	public void getWeather() {
		initData();
	}

	private void initData() {
		BDLocation location = LocationManager.getInstance().getLastLocation();
		if (null == location) {// 是否有可用位置
			LocationManager.getInstance().startLocate(mCallback);
		} else {
			getWeather(location);
		}
	}

	private LocationManager.LocationCallBack mCallback = new LocationManager.LocationCallBack() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 获取位置后就移掉监听器，防止多次回调
			LocationManager.getInstance().removeCallback(this);
			getWeather(location);
		}

		@Override
		public void onNoLocation() {
		}

	};

	/**
	 * 根据经纬度获取当地天气
	 * 
	 * @param location
	 */
	private void getWeather(BDLocation location) {
		if (mGetWeatherTask != null && !mGetWeatherTask.isCancelled()) {
			mGetWeatherTask.cancel(true);
		}
		mGetWeatherTask = new GetWeatherTask(mFeedback);
		mGetWeatherTask.execute(location.getLatitude(), location.getLongitude());
	}

	/**
	 * 获取到天气信息后的回调接口，也是异步线程的回调接口
	 */
	protected IFeedback mFeedback = new IFeedback() {

		@Override
		public boolean onFeedback(String key, boolean isSuccess, Object result) {
			if (isSuccess) {
				Weather weather = (Weather) result;
				Logger.i("the weather: ", weather.toString());
			} else {
				// do empty
			}
			return false;
		}
	};
}

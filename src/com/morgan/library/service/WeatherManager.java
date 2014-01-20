package com.morgan.library.service;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.morgan.library.async.IFeedback;
import com.morgan.library.model.Weather;
import com.morgan.library.task.GetWeatherTask;

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

    public void getWeather() {
        initData();
    }

    private void initData() {
        BDLocation location = LocationManager.getInstance().getLastLocation();
        if (null == location) {
            LocationManager.getInstance().addCallback(mCallback);
        } else {
            getWeather(location);
        }
    }

    private LocationManager.LocationCallBack mCallback = new LocationManager.LocationCallBack() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LocationManager.getInstance().removeCallback(this);
            getWeather(location);
        }

    };

    private void getWeather(BDLocation location) {
        if (mGetWeatherTask != null && !mGetWeatherTask.isCancelled()) {
            mGetWeatherTask.cancel(true);
        }
        mGetWeatherTask = new GetWeatherTask(mFeedback);
        mGetWeatherTask.execute(location.getLatitude(), location.getLongitude());
    }

    protected IFeedback mFeedback = new IFeedback() {

        @Override
        public boolean onFeedback(String key, boolean isSuccess, Object result) {
            if (isSuccess) {
                Weather weather = (Weather) result;
                Log.e("haha weather", weather.toString());
            } else {
                // do empty
            }
            return false;
        }
    };
}

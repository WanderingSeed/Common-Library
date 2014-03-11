package com.morgan.library.service;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.morgan.library.R;
import com.morgan.library.app.APPContext;

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

    private void startLocClient() {
        if (mLocClient != null && !mLocClient.isStarted()) {
            mLocClient.start();
        }
    }

    private void requestLocation() {
        if (mLocClient != null && !mLocClient.isStarted()) {
            mLocClient.requestLocation();
        }
    }

    private void stopLocClient() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
    }

    public BDLocation getLastLocation() {
        return mLastLocation;
    }

    private void registerLocationListener() {
        if (mBDLocationListener == null) {
            mBDLocationListener = new MyLocationListenner();
            mLocClient.registerLocationListener(mBDLocationListener);
        }
    }

    public void destoryLocationListener() {
        mLocClient.unRegisterLocationListener(mBDLocationListener);
        mBDLocationListener = null;
    }

    public void startLocate(LocationCallBack callback) {
        if (!mCallback.contains(callback)) {
            this.mCallback.add(callback);
        }
        startLocClient();
    }

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

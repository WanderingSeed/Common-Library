package com.morgan.library.task;

import android.os.AsyncTask;

import com.morgan.library.async.IFeedback;
import com.morgan.library.model.NetResult;
import com.morgan.library.model.Weather;
import com.morgan.library.net.ApiManager;

public class GetWeatherTask extends AsyncTask<Double, Void, Object> {

    public static final String FEED_BACK_KEY = GetWeatherTask.class.getName();
    private IFeedback feedback;
    private boolean mIsSuccess = false;

    public GetWeatherTask(IFeedback feedback) {
        this.feedback = feedback;
    }

    @Override
    protected Object doInBackground(Double... params) {
        double lat = params[0];
        double lon = params[1];
        NetResult<Weather> result = ApiManager.getApiClient().getWeather(lat, lon);
        if (result.isSuccess()) {
            mIsSuccess = true;
            return result.getObject();
        }
        return result.getErrorMessage();
    }

    @Override
    protected void onPostExecute(Object result) {
        feedback.onFeedback(FEED_BACK_KEY, mIsSuccess, result);
    }
}

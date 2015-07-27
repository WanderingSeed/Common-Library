package com.morgan.demo.task;

import android.os.AsyncTask;

import com.morgan.demo.model.NetResult;
import com.morgan.demo.model.Weather;
import com.morgan.demo.net.NetApiManager;
import com.morgan.library.async.IFeedback;

public class GetWeatherTask extends AsyncTask<Double, Void, Object> {

	public static final String FEED_BACK_KEY = GetWeatherTask.class.getName();
	private IFeedback mFeedback;
	private boolean mIsSuccess = false;

	public GetWeatherTask(IFeedback feedback) {
		this.mFeedback = feedback;
	}

	@Override
	protected Object doInBackground(Double... params) {
		double lat = params[0];
		double lon = params[1];
		NetResult<Weather> result = NetApiManager.getApiClient().getWeather(lat,
				lon);
		if (result.isSuccess()) {
			mIsSuccess = true;
			return result.getObject();
		}
		return result.getErrorMessage();
	}

	@Override
	protected void onPostExecute(Object result) {
		mFeedback.onFeedback(FEED_BACK_KEY, mIsSuccess, result);
	}
}

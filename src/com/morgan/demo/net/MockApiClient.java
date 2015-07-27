package com.morgan.demo.net;

import com.morgan.demo.model.NetResult;
import com.morgan.demo.model.Weather;

/**
 * 整个应用所有模拟的网络请求类，以便于展示
 * 
 * @author Morgan.Ji
 * 
 */
public class MockApiClient implements INetApi {

	@Override
	public NetResult<Weather> getWeather(double lat, double lon) {
		return null;
	}

}

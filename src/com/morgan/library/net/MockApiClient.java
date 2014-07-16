package com.morgan.library.net;

import com.morgan.library.model.NetResult;
import com.morgan.library.model.Weather;

/**
 * 模拟的网络请求类
 * 
 * @author Morgan.Ji
 * 
 */
public class MockApiClient implements IApiClient {

	@Override
	public NetResult<Weather> getWeather(double lat, double lon) {
		return null;
	}

}

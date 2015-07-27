package com.morgan.demo.net;

import com.morgan.demo.model.NetResult;
import com.morgan.demo.model.Weather;

/**
 * 一个应用所有网络请求的接口，抽取的好处是可以自定义一个模拟请求的类来为整个应用提供模拟数据。
 * 
 * @author Morgan.Ji
 * 
 */
public interface INetApi {

	/**
	 * 获取给定位置的天气情况
	 * 
	 * @param lat
	 *            给定位置的纬度
	 * @param lon
	 *            给定位置的经度
	 * @return 请求结果
	 */
	public NetResult<Weather> getWeather(double lat, double lon);
}

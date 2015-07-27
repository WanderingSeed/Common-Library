package com.morgan.demo.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.morgan.demo.model.NetResult;
import com.morgan.demo.model.Weather;
import com.morgan.demo.model.WeatherType;
import com.morgan.library.utils.Logger;

/**
 * 用于解析整个应用网络请求返回来的json字符串，解析成VO对象。
 * 
 * @author Morgan.Ji
 * 
 */
public class NetJsonUtils {

	public static final String TAG = NetJsonUtils.class.getName();

	/**
	 * 解析天气数据
	 * 
	 * @param result
	 */
	public static void JsonToWeather(NetResult<Weather> result) {
		try {
			JSONObject jsonObject = new JSONObject(result.getResponseMessage());
			if (jsonObject.getJSONArray("weather").length() > 0) {
				Weather weather = new Weather();
				weather.setType(WeatherType.typeOf(jsonObject.getJSONArray("weather")
						.getJSONObject(0).getString("icon")));
				weather.setTempature((int) (jsonObject.getJSONObject("main").getDouble(
						"temp") - 273.15));
				result.setObject(weather);
			}
		} catch (JSONException e) {
			Logger.e(TAG, "weather data parase error " + e.getMessage());
			result.setStatusCode(NetResult.JSON_PARSE_ERROR_CODE);
			result.setErrorMessage(NetResult.JSON_PARSE_ERROR_MESSAGE);
		}
	}
}

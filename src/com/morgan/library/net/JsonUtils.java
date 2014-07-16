package com.morgan.library.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.morgan.library.model.NetResult;
import com.morgan.library.model.Weather;
import com.morgan.library.model.WeatherType;
import com.morgan.library.utils.Logger;

/**
 * 用于解析网络请求返回来的json字符串，解析成VO对象。
 * 
 * @author Morgan.Ji
 * 
 */
public class JsonUtils {

	public static final String TAG = JsonUtils.class.getName();
	public static final String JSON_PARSE_ERROR_MESSAGE = "Json解析出错";
	public static final int JSON_PARSE_ERROR_CODE = -1;

	public static void JsonToWeather(NetResult<Weather> result) {
		try {
			JSONObject jsonObject = new JSONObject(result.getResponseMessage());
			if (jsonObject.getJSONArray("weather").length() > 0) {
				Weather weather = new Weather();
				weather.setType(WeatherType.typeOf(jsonObject
						.getJSONArray("weather").getJSONObject(0)
						.getString("icon")));
				weather.setTempature((int) (jsonObject.getJSONObject("main")
						.getDouble("temp") - 273.15));
				result.setObject(weather);
			}
		} catch (JSONException e) {
			Logger.e(TAG, "weather data parase error " + e.getMessage());
			result.setStatusCode(JSON_PARSE_ERROR_CODE);
			result.setErrorMessage(JSON_PARSE_ERROR_MESSAGE);
		}
	}
}

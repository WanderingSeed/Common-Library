package com.morgan.library.net;

import java.util.HashMap;
import java.util.Map;

import com.morgan.library.model.NetResult;
import com.morgan.library.model.Weather;
import com.morgan.library.utils.HttpClientUtils;
import com.morgan.library.utils.StrUtils;

/**
 * 真正发起网络请求的类
 * 
 * @author Morgan.Ji
 * 
 */
public class ApiClient implements IApiClient {

    @Override
    public NetResult<Weather> getWeather(double lat, double lon) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lat", lat);
        map.put("lon", lon);
        String url = StrUtils.encodeUrl(ApiUrl.GET_WEATHER_API, map);
        NetResult<Weather> result = new NetResult<Weather>(HttpClientUtils.get(url));
        if (result.isSuccess()) {
            JsonUtils.JsonToWeather(result);
        }
        return result;
    }

}

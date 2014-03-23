package com.morgan.library.net;

import com.morgan.library.model.NetResult;
import com.morgan.library.model.Weather;

/**
 * 网络请求的借口，抽取的好处是可以自定义一个模拟请求的类来为整个应用提供模拟数据。
 * 
 * @author Morgan.Ji
 * 
 */
public interface IApiClient {

    public NetResult<Weather> getWeather(double lat, double lon);
}

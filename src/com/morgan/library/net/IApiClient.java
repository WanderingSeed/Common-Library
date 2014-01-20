package com.morgan.library.net;

import com.morgan.library.model.NetResult;
import com.morgan.library.model.Weather;

public interface IApiClient {

    public NetResult<Weather> getWeather(double lat, double lon);
}

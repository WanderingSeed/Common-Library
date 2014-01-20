package com.morgan.library.net;

import com.morgan.library.model.NetResult;
import com.morgan.library.model.Weather;

public class MockApiClient implements IApiClient {

    @Override
    public NetResult<Weather> getWeather(double lat, double lon) {
        return null;
    }

}

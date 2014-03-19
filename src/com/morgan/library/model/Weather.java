package com.morgan.library.model;

/**
 * {@link com.morgan.library.service.WeatherManager}类中抓取的天气的model
 * 
 * @author Morgan.Ji
 * 
 */
public class Weather {

    private int tempature = Integer.MAX_VALUE;
    private WeatherType type = WeatherType.SUNNY;

    public int getTempature() {
        return tempature;
    }

    public void setTempature(int tempature) {
        this.tempature = tempature;
    }

    public WeatherType getType() {
        return type;
    }

    public void setType(WeatherType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "weather type: " + type + " tempature: " + tempature;
    }
}

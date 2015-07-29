package com.morgan.demo.model;


/**
 * {@link com.morgan.demo.manager.WeatherManager}类中抓取的天气的model
 * 
 * @author Morgan.Ji
 * 
 */
public class Weather {

	private int tempature = Integer.MAX_VALUE;// 温度
	private WeatherType type = WeatherType.SUNNY;// 天气类型

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

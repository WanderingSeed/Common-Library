package com.morgan.demo.model;

/**
 * 天气的类型，他们的值是所抓取的服务器定义的。
 * 
 * @author Morgan.Ji
 * 
 */
public enum WeatherType {
	SUNNY("01d"), FLEWCLOUDS("02d"), SCATTEREDCLOUDS("03d"), BROKENCLOUDS("04d"), SHOWERRAIN(
			"09d"), RAIN("10d"), THUNDERSTORM("11d"), SNOW("13d"), MIST("50d");

	private String mIconId = "";

	WeatherType(String icon) {
		this.mIconId = icon;
	}

	public static WeatherType typeOf(String iconId) {
		WeatherType[] values = WeatherType.values();
		for (int i = 0; i < values.length; i++) {
			if (values[i].getIconId().equals(iconId)) {
				return values[i];
			}
		}
		return WeatherType.SUNNY;
	}

	public String getIconId() {
		return this.mIconId;
	}
}

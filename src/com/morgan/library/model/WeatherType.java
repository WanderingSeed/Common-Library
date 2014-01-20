package com.morgan.library.model;

public enum WeatherType {
    SUNNY("01d"), FLEWCLOUDS("02d"), SCATTEREDCLOUDS("03d"), BROKENCLOUDS("04d"), SHOWERRAIN("09d"), RAIN("10d"), THUNDERSTORM("11d"), SNOW(
            "13d"), MIST("50d");

    private String icon = "";

    WeatherType(String icon) {
        this.icon = icon;
    }

    public static WeatherType typeOf(String icon)
    {
        WeatherType[] values = WeatherType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].icon().equals(icon)) { return values[i]; }
        }
        return WeatherType.SUNNY;
    }

    public String icon()
    {
        return this.icon;
    }
}

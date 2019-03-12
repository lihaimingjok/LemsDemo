package com.pcjz.lems.business.entity;

/**
 * created by yezhengyu on 2017/4/5 14:59
 */
public class WeatherInfo {
    private int temperature;
    private long time;
    private String weather;
    private String countTimes;

    public String getCountTimes() {
        return countTimes;
    }

    public void setCountTimes(String countTimes) {
        this.countTimes = countTimes;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}

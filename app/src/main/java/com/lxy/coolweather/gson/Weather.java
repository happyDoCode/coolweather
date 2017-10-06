package com.lxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class Weather implements Serializable{

    public String status;

    public AQI aqi;

    public Basic basic;

    public Now now;

    @SerializedName("daily_forecast")
    public List<DailyForecast> DailyForecastsList;

    public Suggestion suggestion;
}

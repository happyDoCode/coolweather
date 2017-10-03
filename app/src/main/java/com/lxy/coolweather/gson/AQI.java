package com.lxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class AQI {

    @SerializedName("city")
    public AQICity aqiCity;

    public class AQICity{
        public String aqi;
        public String co;
        public String no2;
        public String o3;
        public String pm10;
        public String pm25;
        public String qlty;
        public String so2;
    }
}

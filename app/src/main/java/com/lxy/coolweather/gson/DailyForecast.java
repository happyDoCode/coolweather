package com.lxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class DailyForecast {

    public String date;

    public String hum;

    @SerializedName("tmp")
    public Temperature temperature;

    public Cond cond;

    public class Temperature{

        public String max;
        public String min;
    }


    public class Cond{

        @SerializedName("txt_d")
        public String info;
    }
}

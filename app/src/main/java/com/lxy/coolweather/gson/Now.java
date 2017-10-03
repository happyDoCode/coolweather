package com.lxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;//温度

    public Wind wind;

    @SerializedName("cond")
    public More more;

    public String hum;

    public class More{

        @SerializedName("txt")
        public String info;
    }

    public class Wind{
        public String deg;
        public String dir;
        public String sc;
        public String spd;
    }
}

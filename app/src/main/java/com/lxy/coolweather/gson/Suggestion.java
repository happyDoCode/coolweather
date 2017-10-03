package com.lxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Fluence flu;

    @SerializedName("trav")
    public Travel travel;

    public Sport sport;

    public DressSuggest drsg;

    public class Comfort{

        public String brf;

        @SerializedName("txt")
        public String info;
    }

    /**
     * 洗车建议
     */
    public class CarWash{

        public String brf;

        @SerializedName("txt")
        public String info;
    }

    /**
     * 穿衣建议
     */
    public class DressSuggest{

        public String brf;

        @SerializedName("txt")
        public String info;
    }

    /**
     * 运动建议
     */
    public class Sport{

        public String brf;

        @SerializedName("txt")
        public String info;
    }

    /**
     * 旅游建议
     */
    public class Travel{

        public String brf;

        @SerializedName("txt")
        public String info;
    }

    /**
     * 感冒建议
     */
    public class Fluence{

        public String brf;

        @SerializedName("txt")
        public String info;
    }
}

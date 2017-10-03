package com.lxy.coolweather.gson;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class Basic {

    public String city;
    public String cnty;
    public String id;
    public String lat;
    public String lon;
    public Update update;

    public class Update{
        public String loc;
        public String utc;
    }
}

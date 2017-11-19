package com.lxy.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 刘晓阳 on 2017/10/7.
 */

public class Place extends DataSupport {


    private String weatherId;
    private String countyName;
    private int local;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
    }
}

package com.lxy.coolweather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxy.coolweather.R;
import com.lxy.coolweather.gson.Weather;
import com.lxy.coolweather.util.CircleBar;
import com.lxy.coolweather.util.Utility;

public class AQIDetailActivity extends AppCompatActivity {

    private CircleBar circleBar;
    private TextView aqiQlty;
    private TextView aqi_pm25,aqi_pm10,aqi_no2,aqi_co,aqi_o3,aqi_so2;
    private ImageView imageBack;
    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aqidetail);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        Log.i("so",weatherString);
        weather = Utility.handleWeatherResopnse(weatherString);

        initView();
    }

    private void initView() {

        imageBack = (ImageView)findViewById(R.id.aqi_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        aqiQlty = (TextView)findViewById(R.id.aqi_content);
        aqi_pm25 = (TextView)findViewById(R.id.aqi_content_pm25);
        aqi_pm10 = (TextView)findViewById(R.id.aqi_content_pm10);
        aqi_no2 = (TextView)findViewById(R.id.aqi_content_no2);
        aqi_co = (TextView)findViewById(R.id.aqi_content_co);
        aqi_o3 = (TextView)findViewById(R.id.aqi_content_o3);
        aqi_so2 = (TextView)findViewById(R.id.aqi_content_so2);

        aqiQlty.setText(weather.aqi.aqiCity.qlty);
        aqi_o3.setText(weather.aqi.aqiCity.o3);
        aqi_co.setText(weather.aqi.aqiCity.co);
        aqi_pm10.setText(weather.aqi.aqiCity.pm10);
        aqi_pm25.setText(weather.aqi.aqiCity.pm25);
        aqi_so2.setText(weather.aqi.aqiCity.so2);
        aqi_no2.setText(weather.aqi.aqiCity.no2);
//        Log.i("so",weather.aqi.aqiCity.so2);

        circleBar = (CircleBar)findViewById(R.id.aqi_bar);
        float sweepAngle = Float.parseFloat(weather.aqi.aqiCity.aqi);
        circleBar.setSweepAngle(sweepAngle);
        circleBar.setText(weather.aqi.aqiCity.aqi);
        circleBar.setDesText((String) getText(R.string.aqi_qlty));
    }
}

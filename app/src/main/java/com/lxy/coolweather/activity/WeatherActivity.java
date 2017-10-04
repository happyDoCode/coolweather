package com.lxy.coolweather.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lxy.coolweather.R;
import com.lxy.coolweather.gson.DailyForecast;
import com.lxy.coolweather.gson.Weather;
import com.lxy.coolweather.util.HttpUtil;
import com.lxy.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class WeatherActivity extends AppCompatActivity {

    private final static String TAG="WeatherActivity";

    public DrawerLayout drawerLayout;

    private ImageView drawerButton;

    public SwipeRefreshLayout refreshLayout;

    private ScrollView weatherLayout;

    private TextView title_city,title_time;
    private TextView now_degree,weather_info_text;
    private TextView now_windy_name,now_windy_num,now_hum_name,now_hum_num,now_cond_name,now_cond_num;

    private LinearLayout forecastList;

    private TextView aqi_weight,aqi_text,aqi_pm25_text,aqi_time;

    private TextView suggest_travel,suggest_comfort,suggest_car_wash,suggest_sport;
    String weatherId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();//初始化组件
        initData();//初始化数据
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeather(weatherId);
            }
        });

    }

    private void initData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        //检查缓存中是否有数据，有就直接有缓存数据，没有则去服务器获取
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResopnse(weatherString);
            showWeatherInfo(weather);
        }else {
            weatherId = getIntent().getExtras().getString("data");
            weatherLayout.setVisibility(View.INVISIBLE);
            getWeather(weatherId);
        }
    }

    private void initView() {
        drawerLayout = (DrawerLayout)findViewById(R.id.weather_drawer);
        drawerButton = (ImageView) findViewById(R.id.title_change_area);
        drawerButton.setOnClickListener(new MyClick());

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.weather_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        title_city = (TextView)findViewById(R.id.title_city);
        title_time = (TextView)findViewById(R.id.title_update_time);

        now_degree = (TextView)findViewById(R.id.degree_text);
        weather_info_text = (TextView)findViewById(R.id.weather_info_text);

        now_windy_name = (TextView)findViewById(R.id.now_windy_name);
        now_windy_num = (TextView)findViewById(R.id.now_windy_num);
        now_hum_name = (TextView)findViewById(R.id.now_hum_name);
        now_hum_num = (TextView)findViewById(R.id.now_hum_num);
        now_cond_name = (TextView)findViewById(R.id.now_cond_name);
        now_cond_num = (TextView)findViewById(R.id.now_cond_num);

        forecastList = (LinearLayout)findViewById(R.id.forecast_list);

        aqi_weight = (TextView)findViewById(R.id.aqi_weight);
        aqi_text = (TextView)findViewById(R.id.aqi_text);
        aqi_pm25_text = (TextView)findViewById(R.id.aqi_pm25_text);
        aqi_time = (TextView)findViewById(R.id.aqi_time);

        suggest_travel = (TextView)findViewById(R.id.suggest_travel);
        suggest_comfort = (TextView)findViewById(R.id.suggest_comfort);
        suggest_car_wash = (TextView)findViewById(R.id.suggest_car_wash);
        suggest_sport = (TextView)findViewById(R.id.suggest_sport);

        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);

    }

    private class MyClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.title_change_area:
                    drawerLayout.openDrawer(GravityCompat.START);
                    drawerLayout.setScrimColor(Color.TRANSPARENT);
            }
        }
    }


    /**
     * 从服务器中获取相应数据
     * @param weatherId
     */
    public void getWeather( final String weatherId) {
        String address = "https://free-api.heweather.com/v5/weather?city="+weatherId
                +"&key=12693de64b2e406c91d98d98333cd89e";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Toast.makeText(WeatherActivity.this,getText(R.string.false_for_data),Toast.LENGTH_SHORT).show();
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String text = response.body().string();

                final Weather weather = Utility.handleWeatherResopnse(text);
                Log.i(TAG,weather.status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor
                                    editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",text);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            refreshLayout.setRefreshing(false);
                            Toast.makeText(WeatherActivity.this,getText(R.string.false_for_data),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    /**
     * 将各项天气数据显示在界面上
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.city;
        String updateTime = weather.basic.update.loc.split(" ")[1];

        title_city.setText(cityName);
        title_time.setText(updateTime);

        now_degree.setText(weather.now.temperature+"℃");
        weather_info_text.setText(weather.now.more.info);

        now_hum_num.setText(weather.now.hum);
        now_windy_name.setText(weather.now.wind.dir);
        now_windy_num.setText(weather.now.wind.spd);
        now_cond_name.setText(getText(R.string.aqi_qlty)+weather.aqi.aqiCity.qlty);
        now_cond_num.setText(weather.aqi.aqiCity.aqi);

        forecastList.removeAllViews();
        for (DailyForecast forecast : weather.DailyForecastsList) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_forecast, forecastList,false);

            TextView date = (TextView)view.findViewById(R.id.time_for_today);
            TextView time_today_rain = (TextView)view.findViewById(R.id.time_today_rain);

            TextView tem_max = (TextView)view.findViewById(R.id.tem_max);
            TextView tem_min = (TextView)view.findViewById(R.id.tem_min);

            date.setText(forecast.date);
            time_today_rain.setText(forecast.cond.info);
            tem_max.setText(forecast.temperature.max);
            tem_min.setText(forecast.temperature.min);

            forecastList.addView(view);
        }

        aqi_weight.setText(weather.aqi.aqiCity.qlty);
        aqi_text.setText(weather.aqi.aqiCity.aqi);
        aqi_pm25_text.setText(weather.aqi.aqiCity.pm25);
        aqi_time.setText(updateTime+getText(R.string.aqi_time));

        /**生活建议*/
        String travel = getText(R.string.suggest_travel)+weather.suggestion.travel.info;
        String car_wash = getText(R.string.suggest_car)+weather.suggestion.carWash.info;
        String dress = getText(R.string.suggest_dress)+weather.suggestion.drsg.info;
        String sprot = getText(R.string.suggest_sport)+weather.suggestion.sport.info;

        suggest_travel.setText(travel);
        suggest_car_wash.setText(car_wash);
        suggest_sport.setText(sprot);
        suggest_comfort.setText(dress);

        weatherLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
    }
}

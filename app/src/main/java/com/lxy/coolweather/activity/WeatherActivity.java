package com.lxy.coolweather.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lxy.coolweather.R;
import com.lxy.coolweather.adapter.ForecastAdapter;
import com.lxy.coolweather.gson.DailyForecast;
import com.lxy.coolweather.gson.Weather;
import com.lxy.coolweather.service.AutoUpdateWeatherService;
import com.lxy.coolweather.service.LocationService;
import com.lxy.coolweather.util.CircleBar;
import com.lxy.coolweather.util.HttpUtil;
import com.lxy.coolweather.util.MyListView;
import com.lxy.coolweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



/**
 *
 * @author 刘晓阳
 * @date 2017/10/2
 */

public class WeatherActivity extends AppCompatActivity {

    private final static String TAG="WeatherActivity";


    public DrawerLayout drawerLayout;
    private boolean showing = true;

    private TextView drawerButton;

    public SwipeRefreshLayout refreshLayout;

    private ScrollView weatherLayout;

    private TextView title_city;
    private ImageView title_time;
    private TextView now_degree,weather_info_text;
    private TextView now_windy_name,now_windy_num,now_hum_num,now_cond_name,now_cond_num;

    private MyListView forecastList;
    private ForecastAdapter adapter;
    private List<DailyForecast> dataList;

    private TextView aqi_weight,aqi_time,aqi_detail_text;
    private CircleBar aqi_text,aqi_pm25_text;

    private TextView suggest_travel,suggest_comfort,suggest_car_wash,suggest_sport;
    String weatherId;
    Weather weather = new Weather();
    private int RESULT = 1;

    private Animation animIn,animOut;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int alphaColor = 5;

            window.setStatusBarColor(alphaColor);
        }
        setContentView(R.layout.activity_weather);


        initView();//初始化组件
        initData();//初始化数据

    }


    private void initData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        String place = preferences.getString("place",null);
        //检查缓存中是否有数据，有就直接有缓存数据，没有则去服务器获取
        if (weatherString != null ){
            weather = Utility.handleWeatherResopnse(weatherString);
            weatherId = weather.basic.id;
            showWeatherInfo(weather);
        }else {
            weatherId = getIntent().getExtras().getString("data");
            weatherLayout.setVisibility(View.INVISIBLE);
            getWeather(weatherId);
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeather(weatherId);
            }
        });
    }

    private void initView() {
        drawerLayout = (DrawerLayout)findViewById(R.id.weather_drawer);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // 得到contentView
                View content = drawerLayout.getChildAt(0);
                int offset = (int) (drawerView.getWidth() * slideOffset);
                content.setTranslationX(offset);
                //content.setScaleX(1 - slideOffset * 0.2f);
                //content.setScaleY(1 - slideOffset * 0.2f);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawerButton = (TextView) findViewById(R.id.title_change_area);
        drawerButton.setOnClickListener(new MyClick());

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.weather_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        title_city = (TextView)findViewById(R.id.title_city);
        title_time = (ImageView)findViewById(R.id.title_update_time);
        title_time.setOnClickListener(new MyClick());

        now_degree = (TextView)findViewById(R.id.degree_text);
        weather_info_text = (TextView)findViewById(R.id.weather_info_text);

        now_windy_name = (TextView)findViewById(R.id.now_windy_name);
        now_windy_num = (TextView)findViewById(R.id.now_windy_num);
        now_hum_num = (TextView)findViewById(R.id.now_hum_num);
        now_cond_name = (TextView)findViewById(R.id.now_cond_name);
        now_cond_num = (TextView)findViewById(R.id.now_cond_num);

        forecastList = (MyListView)findViewById(R.id.forecast_list);
        dataList = new ArrayList<>();
        adapter = new ForecastAdapter(WeatherActivity.this,dataList);
        forecastList.setAdapter(adapter);

        aqi_weight = (TextView)findViewById(R.id.aqi_weight);
        aqi_text = (CircleBar)findViewById(R.id.aqi_text);
        aqi_pm25_text = (CircleBar)findViewById(R.id.aqi_pm25_text);
        aqi_time = (TextView)findViewById(R.id.aqi_time);
        aqi_detail_text = (TextView)findViewById(R.id.aqi_detail_text);
        aqi_detail_text.setOnClickListener(new MyClick());

        suggest_travel = (TextView)findViewById(R.id.suggest_travel);
        suggest_comfort = (TextView)findViewById(R.id.suggest_comfort);
        suggest_car_wash = (TextView)findViewById(R.id.suggest_car_wash);
        suggest_sport = (TextView)findViewById(R.id.suggest_sport);

        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);

        Intent bindIntent = new Intent(this,LocationService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);

        animIn = AnimationUtils.loadAnimation(this,R.anim.anim);
        animOut = AnimationUtils.loadAnimation(this,R.anim.anim_out);
    }


    private class MyClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.title_change_area:
                    drawerLayout.setDrawerShadow(R.drawable.home, Gravity.LEFT);
                    drawerLayout.openDrawer(GravityCompat.START);
//                    Intent intent = new Intent(WeatherActivity.this,ChooseAreaFragment.class);
//                    startActivity(intent);
                    break;
                case R.id.aqi_detail_text:
                    Intent intent = new Intent(WeatherActivity.this,AQIDetailActivity.class);
                    startActivity(intent);
                    break;
                case R.id.title_update_time:

                    intent = new Intent(WeatherActivity.this,PlaceActivity.class);
                    startActivityForResult(intent,RESULT);

                default:
                    break;
            }
        }
    }

    /**
     *  动态获取权限
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT && resultCode == 2){
            String weatherId = data.getExtras().getString("data");
            refreshLayout.setRefreshing(true);
            getWeather(weatherId);
        }
    }

    /**
     * 从服务器中获取相应数据
     * @param weatherId
     */
    public void getWeather( final String weatherId) {

        this.weatherId = weatherId;
        String address = "https://free-api.heweather.com/v5/weather?city="+ weatherId
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

                weather = Utility.handleWeatherResopnse(text);
                Log.i(TAG,weather.status + "    weatherId:" +weatherId);
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

        now_degree.setText(weather.now.temperature+"℃");
        weather_info_text.setText(weather.now.more.info);

        now_hum_num.setText(weather.now.hum+"%");
        now_windy_name.setText(weather.now.wind.dir);
        now_windy_num.setText(weather.now.wind.spd);
        now_cond_name.setText(getText(R.string.aqi_qlty)+weather.aqi.aqiCity.qlty);
        now_cond_num.setText(weather.aqi.aqiCity.aqi);

        dataList.clear();
        dataList.addAll(weather.DailyForecastsList);
//        Log.i(TAG,dataList.size()+"");
        adapter.notifyDataSetChanged();

        aqi_weight.setText(weather.aqi.aqiCity.qlty);

        aqi_text.setText(weather.aqi.aqiCity.aqi);
        float sweep = Float.parseFloat(weather.aqi.aqiCity.aqi);
        aqi_text.setSweepAngle(sweep);
        aqi_text.setDesText(getText(R.string.aqi).toString());

        sweep = Float.parseFloat(weather.aqi.aqiCity.pm25);
        aqi_pm25_text.setSweepAngle(sweep);
        aqi_pm25_text.setText(weather.aqi.aqiCity.pm25);
        aqi_pm25_text.setDesText(getText(R.string.aqi_pm25).toString());

        aqi_time.setText(updateTime+getText(R.string.aqi_time));

        /**生活建议*/
        String travel = getText(R.string.suggest_travel)+weather.suggestion.travel.info;
        String car_wash = getText(R.string.suggest_car)+weather.suggestion.carWash.info;
        String dress = getText(R.string.suggest_dress)+weather.suggestion.drsg.info;
        String sport = getText(R.string.suggest_sport)+weather.suggestion.sport.info;

        suggest_travel.setText(travel);
        suggest_car_wash.setText(car_wash);
        suggest_sport.setText(sport);
        suggest_comfort.setText(dress);

        weatherLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        //开启自动更新天气服务，每小时更新一次
        Intent intent = new Intent(this, AutoUpdateWeatherService.class);
        startService(intent);
    }


    /**
     * 显示切换城市的提示框
     * @param cityName
     */
    public void showDialog(final String cityName){
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this,0);
        String message = getText(R.string.change_place)+cityName+getText(R.string.is_change_place);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getWeather(cityName);
            }
        }).show();
    }

    private LocationService.LocationBinder locationBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (LocationService.LocationBinder) service;
            if (locationBinder.isChangePlace()){
                showDialog(locationBinder.getCityName());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        isHasData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!showing) {

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

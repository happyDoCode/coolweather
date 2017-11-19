package com.lxy.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lxy.coolweather.R;
import com.lxy.coolweather.gson.Weather;
import com.lxy.coolweather.util.HttpUtil;
import com.lxy.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateWeatherService extends Service {
    private static final int ANHOUR = 1 * 60 * 60 * 1000;//一小时的毫秒数
    public AutoUpdateWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();

        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + ANHOUR;
        Intent i = new Intent(this,AutoUpdateWeatherService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferces = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferces.getString("weather",null);
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResopnse(weatherString);
            String weatherId = weather.basic.id;
            String weatherUrl = getText(R.string.weather_url) + weatherId + "&"
                    + getText(R.string.weather_url_key);
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("autoService",e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String text = response.body().string();
                    Weather weather = Utility.handleWeatherResopnse(text);
                    if (weather != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(AutoUpdateWeatherService.this)
                                        .edit();
                        editor.putString("weather",text);
                        editor.apply();
                    }
                }
            });
        }
    }
}

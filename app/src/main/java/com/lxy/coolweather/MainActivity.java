package com.lxy.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.lxy.coolweather.activity.WeatherActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString("weather",null) !=null){
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
//            intent.putExtra("data",dataListCounty.get(position).getWeatherId());
            startActivity(intent);
            finish();
        }
    }
}

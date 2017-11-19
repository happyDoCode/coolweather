package com.lxy.coolweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lxy.coolweather.R;
import com.lxy.coolweather.gson.DailyForecast;

import java.util.List;

/**
 * Created by 刘晓阳 on 2017/10/6.
 */

public class ForecastAdapter extends BaseAdapter {

    private Context mContext;
    private List<DailyForecast> mList;
    private String[] strings;

    public ForecastAdapter(Context context, List<DailyForecast> list){
        mContext = context;
        mList = list;
        strings = context.getResources().getStringArray(R.array.day);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forecast,parent,false);

            holder.image = (ImageView)convertView.findViewById(R.id.forecast_image) ;
            holder.date = (TextView)convertView.findViewById(R.id.time_for_today);
            holder.time_today_rain = (TextView)convertView.findViewById(R.id.time_today_rain);
            holder.tem_max = (TextView)convertView.findViewById(R.id.tem_max);
            holder.tem_min = (TextView)convertView.findViewById(R.id.tem_min);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load("https://cdn.heweather.com/cond_icon/100.png").into(holder.image);
        holder.date.setText(strings[position]);
        holder.time_today_rain.setText(mList.get(position).cond.info);
        holder.tem_max.setText(mList.get(position).temperature.max+"°");
        holder.tem_min.setText(mList.get(position).temperature.min+"°");

        return convertView;
    }

    private class ViewHolder{
        private ImageView image;
        private TextView date;
        private TextView time_today_rain;
        private TextView tem_max;
        private TextView tem_min;

    }
}

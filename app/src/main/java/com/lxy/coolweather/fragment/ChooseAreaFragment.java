package com.lxy.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lxy.coolweather.R;
import com.lxy.coolweather.activity.WeatherActivity;
import com.lxy.coolweather.db.City;
import com.lxy.coolweather.db.County;
import com.lxy.coolweather.db.Province;
import com.lxy.coolweather.util.HttpUtil;
import com.lxy.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 刘晓阳 on 2017/10/2.
 */

public class ChooseAreaFragment extends Fragment {

    //当前用户处于那种地区显示
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private static final String TAG = "ChooseAreaFragment";

    private Button backButton;

    //显示用户的选择地区
    private TextView titleArea;
    private ListView listView;

    private List<String> datalist = new ArrayList<>();

    private List<Province> dataListProvince;
    private List<City> dataListCity;
    private List<County> dataListCounty;

    private ArrayAdapter<String> adapter;

    /**记录当前选中的省份*/
    private Province selectedProvince;

    /**记录当前选中的城市*/
    private City selectedCity;

    /**记录当前选中的县*/
    private County selectedCounty;
    //记录当前选中的级别
    private int currentLevel = 0;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        backButton = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.area_list);
        titleArea = (TextView)view.findViewById(R.id.title_text);

        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvince();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = dataListProvince.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = dataListCity.get(position);
                    queryCounties();
                }else {
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("data",dataListCounty.get(position).getWeatherId());
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                    return;
                }
                if (currentLevel == LEVEL_CITY){
                    queryProvince();
                    return;
                }
            }
        });

    }



    /**
     * 查询中国所有的省，优先从数据库中查询，如果没有再去服务器上获取
     */
    private void queryProvince() {
        titleArea.setText(getText(R.string.title_area));
        backButton.setVisibility(View.GONE);
        //从数据库中查询
        dataListProvince = DataSupport.findAll(Province.class);
        if (dataListProvince.size() > 0){//如果数据库中有数据
            datalist.clear();
            for (Province province : dataListProvince) {
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = getText(R.string.url).toString();
//            String address ="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询该省的所有市，优先从数据库中查询，如果没有再去服务器上获取
     */
    private void queryCities() {
        backButton.setVisibility(View.VISIBLE);
        titleArea.setText(selectedProvince.getProvinceName());
        //从数据库中查询
        dataListCity = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (dataListCity.size() > 0){
            datalist.clear();
            for (City city : dataListCity) {
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            String address = getText(R.string.url).toString()+selectedProvince.getProvinceCode();
//            String address ="http://guolin.tech/api/china";
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询该市的所有县，区，优先从数据库中查询，如果没有再去服务器上获取
     */
    private void queryCounties() {
        backButton.setVisibility(View.VISIBLE);
        titleArea.setText(selectedCity.getCityName());
        //从数据库中查询
        dataListCounty = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (dataListCounty.size() > 0){
            datalist.clear();
            for (County county : dataListCounty) {
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = getText(R.string.url).toString() + provinceCode
                    +"/"+selectedCity.getCityCode();
//            String address ="http://guolin.tech/api/china";
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据地址和类型从服务器中查询省市县的数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,e.toString());
                //获取数据失败，通过toast提示用户
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),getText(R.string.false_for_data),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
//                Log.i(TAG+" Text",responseText.toString());
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                //如果解析数据成功
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }else {
                    closeProgressDialog();
                }

            }
        });
    }

    /**
     * 显示进度加载提示框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getText(R.string.progress_dialog_message));
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }

    /**
     * 关闭进度加载提示框
     */
    private void closeProgressDialog(){
        if (progressDialog != null)
            progressDialog.cancel();
    }

}

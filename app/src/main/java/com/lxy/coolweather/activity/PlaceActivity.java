package com.lxy.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lxy.coolweather.MainActivity;
import com.lxy.coolweather.R;
import com.lxy.coolweather.adapter.PlaceAdapter;
import com.lxy.coolweather.db.Place;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;


/**
 * Created by 刘晓阳 on 2017/10/10.
 */

public class PlaceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recycleView;
    private PlaceAdapter adapter;
    private List<Place> dataList = new ArrayList<>();
    private ImageView addCity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_place);

        init();
        initView();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_place);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.toolbar_place_set:
                        adapter.setLocal(true);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHasData();
                finish();
            }
        });
    }


    private void initView() {


        recycleView = (RecyclerView)findViewById(R.id.place_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycleView.setLayoutManager(manager);
        adapter = new PlaceAdapter(getContext(),dataList,false);
        recycleView.setAdapter(adapter);
        getData();
        //为recycleView设置拖拽
        helper.attachToRecyclerView(recycleView);
        adapter.setItemClickListener(new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position){
                Intent intent = new Intent();
                intent.putExtra("data",dataList.get(position).getWeatherId());
                setResult(2,intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    private void getData() {
        List<Place> places = DataSupport.findAll(Place.class);
//        Log.i("place",places.size()+"");
        // 如果数据库中有数据
        if (places.size() > 0){
            dataList.clear();
            for (Place place : places) {
                dataList.add(place);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void isHasData() {
        List<Place> places = DataSupport.findAll(Place.class);

        // 如果数据库中有数据
        if (places.size() == 0){
            // 清除缓存，直接进入MainActivity
            SharedPreferences.Editor
                    editor = PreferenceManager.getDefaultSharedPreferences(PlaceActivity.this).edit();
            editor.putString("weather",null);
            editor.apply();

            Intent intent = new Intent(PlaceActivity.this,MainActivity.class);
            startActivity(intent);
        }

    }


    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 首先回调的方法 返回int表示是否监听该方向
            // 上下方向，即拖拽
            int drag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = 0;
            return makeMovementFlags(drag,swipeFlags);

        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // 滑动事件
            Collections.swap(dataList,viewHolder.getAdapterPosition(),target.getAdapterPosition());
            adapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return false;

        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        // 是否可以拖拽
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    });


}

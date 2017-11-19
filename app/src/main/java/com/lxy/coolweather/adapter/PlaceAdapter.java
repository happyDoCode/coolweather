package com.lxy.coolweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxy.coolweather.R;
import com.lxy.coolweather.db.Place;

import java.util.List;

/**
 *
 * @author 刘晓阳
 * @date 2017/10/7
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {


    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv;
        private ImageView ivCancel;
        private ImageView ivMove;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.item_place);
            ivCancel = (ImageView)itemView.findViewById(R.id.item_place_cancel);
            ivMove = (ImageView)itemView.findViewById(R.id.item_place_move);
        }
     }
    private List<Place> mList;
    private OnItemClickListener listener;
    private boolean local;

    public PlaceAdapter(Context context, List<Place> list,boolean local){
        mList = list;
        this.local = local;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_place,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Place place = mList.get(position);
        if (local){
            showEditUi(holder);
        }
        holder.tv.setText(place.getCountyName());
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onItemClick(v,position);
                }
            }
        });

        holder.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeData(place);
                mList.remove(place);
                notifyDataSetChanged();

            }
        });

    }

    private void removeData(Place place) {
        place.delete();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    private void showEditUi(ViewHolder holder) {
        holder.ivCancel.setVisibility(View.VISIBLE);
        holder.ivMove.setVisibility(View.VISIBLE);
    }

    private void HiddenEditUi(ViewHolder holder) {
        holder.ivCancel.setVisibility(View.INVISIBLE);
        holder.ivMove.setVisibility(View.INVISIBLE);
    }

    public void setLocal(boolean local){
        this.local = local;
    }



}

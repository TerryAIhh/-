package com.cjlu.tyweather.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cjlu.tyweather.R;
import com.cjlu.tyweather.acticitiy.CityManagerActivity;
import com.cjlu.tyweather.bean.WeatherBean;
import com.cjlu.tyweather.databinding.ItemCityManagerBinding;
import com.cjlu.tyweather.db.DatabaseBean;
import com.google.gson.Gson;

import java.util.List;

import static com.cjlu.tyweather.db.DbManager.deleteCityInfo;

public class CityManagerAdapter extends RecyclerView.Adapter<CityManagerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<DatabaseBean> mDatas;
    private CityManagerActivity activity;

    public CityManagerAdapter(List<DatabaseBean> mDatas, CityManagerActivity activity) {
        this.mDatas = mDatas;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCityManagerBinding itemCityManagerBinding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_city_manager, parent, false);
        return new ViewHolder(itemCityManagerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseBean data = mDatas.get(position);
        WeatherBean weatherBean = new Gson().fromJson(data.getContent(), WeatherBean.class);
        holder.binding.setData(weatherBean);
        String[] str = weatherBean.getResults().get(0).getWeather_data().get(0).getDate().split("：");
        String temp = str[1].replace(")", "");
        holder.binding.itemCityTvTemp.setText(temp);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onItemDelete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("提示").setMessage("是否移除该城市？").setPositiveButton("确定", (dialog, which) -> {
            //移除数据
            DatabaseBean data = mDatas.get(position);
            WeatherBean weather = new Gson().fromJson(data.getContent(), WeatherBean.class);
            int res = deleteCityInfo(weather.getResults().get(0).getCurrentCity());
            if (res == 1) {
                mDatas.remove(position);
                notifyItemRemoved(position);
            } else {
                notifyDataSetChanged();
            }

        }).setNegativeButton("取消", (dialog, which) -> {
            notifyDataSetChanged();
        });
        builder.create().show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCityManagerBinding binding;

        public ViewHolder(ItemCityManagerBinding itemCityManagerBinding) {
            super(itemCityManagerBinding.getRoot());
            this.binding = itemCityManagerBinding;
        }
    }
}

package com.cjlu.tyweather.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cjlu.tyweather.R;
import com.cjlu.tyweather.bean.WeatherBean;
import com.cjlu.tyweather.databinding.ItemCityManagerBinding;
import com.cjlu.tyweather.db.DatabaseBean;
import com.google.gson.Gson;

import java.util.List;

public class CityManagerAdapter extends RecyclerView.Adapter<CityManagerAdapter.ViewHolder> {
    private List<DatabaseBean> mDatas;

    public CityManagerAdapter(List<DatabaseBean> mDatas) {
        this.mDatas = mDatas;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCityManagerBinding binding;

        public ViewHolder(ItemCityManagerBinding itemCityManagerBinding) {
            super(itemCityManagerBinding.getRoot());
            this.binding = itemCityManagerBinding;
        }
    }
}

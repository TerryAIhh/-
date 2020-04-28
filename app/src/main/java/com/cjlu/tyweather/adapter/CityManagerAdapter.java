package com.cjlu.tyweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cjlu.tyweather.R;
import com.cjlu.tyweather.bean.WeatherBean;
import com.cjlu.tyweather.db.DatabaseBean;
import com.google.gson.Gson;

import java.util.List;

public class CityManagerAdapter extends BaseAdapter {

    Context context;
    List<DatabaseBean> mDatas;

    public CityManagerAdapter(Context context, List<DatabaseBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city_manager, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DatabaseBean bean = mDatas.get(position);
        holder.cityTv.setText(bean.getCity());
        WeatherBean weatherBean = new Gson().fromJson(bean.getContent(), WeatherBean.class);
        // 获取今日的天气
        WeatherBean.ResultsBean.WeatherDataBean dataBean = weatherBean.getResults().get(0).getWeather_data().get(0);
        holder.weatherTv.setText(dataBean.getWeather());
        String[] str = dataBean.getDate().split("：");
        String temp = str[1].replace(")", "");
        holder.tempTv.setText(temp);
        holder.windTv.setText(dataBean.getWind());
        holder.rangeTv.setText(dataBean.getTemperature());
        return convertView;
    }

    class ViewHolder {
        TextView cityTv, weatherTv, tempTv, rangeTv, windTv;

        public ViewHolder(View itemView) {
            cityTv = itemView.findViewById(R.id.item_city_tv_city);
            weatherTv = itemView.findViewById(R.id.item_city_tv_weather);
            tempTv = itemView.findViewById(R.id.item_city_tv_temp);
            rangeTv = itemView.findViewById(R.id.item_city_tv_range);
            windTv = itemView.findViewById(R.id.item_city_tv_wind);
        }
    }
}

package com.cjlu.tyweather.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.cjlu.tyweather.R;
import com.cjlu.tyweather.base.BaseFragment;
import com.cjlu.tyweather.bean.WeatherBean;
import com.cjlu.tyweather.db.DbManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityWeatherFragment extends BaseFragment implements View.OnClickListener {

    private TextView cityTv, currentTempTv, weatherTv, dateTv, windTv, tempRangeTv,
            dressIndexTv, carIndexTv, coldIndexTv, sportIndexTv, raysIndexTv;
    private ImageView todayIv;
    private LinearLayout futureLayout;
    private ScrollView fragLayout;
    String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    String url2 = "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";
    private List<WeatherBean.ResultsBean.IndexBean> indexList;
    String city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        initView(view);
        changeBg();
        // 可以通过 activity 传值获取到当前 fragment 城市
        Bundle bundle = getArguments();
        city = bundle.getString("city");
        String url = url1 + city + url2;
        // 调用 BaseFragment 获取数据方法
        loadData(url);
        return view;
    }

    @Override
    public void onSuccess(String result) {
        // 解析并展示数据
        showData(result);
        // 更新数据
        int i = DbManager.updateInfoByCity(city, result);
        if (i <= 0) {
            // 更新失败，没有这条记录，则增加城市记录
            DbManager.addCityInfo(city, result);
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        String lastResult = DbManager.queryInfoByCity(city);
        if (!TextUtils.isEmpty(lastResult)) {
            showData(lastResult);
        }
    }

    // 更新背景图
    public void changeBg() {
        SharedPreferences bg_pref = getActivity().getSharedPreferences("bg_pref", MODE_PRIVATE);
        int bg_num = bg_pref.getInt("bg", 2);
        switch (bg_num) {
            case 0:
                fragLayout.setBackgroundResource(R.color.colorAccent);
                break;
            case 1:
                fragLayout.setBackgroundResource(R.color.pink);
                break;
            case 2:
                fragLayout.setBackgroundResource(R.color.gray_bg);
                break;
        }
    }

    @SuppressLint("InflateParams")
    private void showData(String result) {
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);
        WeatherBean.ResultsBean resultsBean = weatherBean.getResults().get(0);
        // 获取指数信息集合列表
        indexList = resultsBean.getIndex();
        // 设置 TextView
        dateTv.setText(weatherBean.getDate());
        cityTv.setText(resultsBean.getCurrentCity());
        // 设置今日天气信息
        WeatherBean.ResultsBean.WeatherDataBean todayDataBean = resultsBean.getWeather_data().get(0);
        windTv.setText(todayDataBean.getWind());
        tempRangeTv.setText(todayDataBean.getTemperature());
        weatherTv.setText(todayDataBean.getWeather());
        // 处理字符串  周四 04月23日 (实时：15℃)
        String[] str = todayDataBean.getDate().split("：");
        String temp = str[1].replace(")", "");
        currentTempTv.setText(temp);
        // 设置天气图片
        Picasso.with(getActivity()).load(todayDataBean.getDayPictureUrl()).into(todayIv);
        // 获取未来三天天气，加载到 layout 中
        List<WeatherBean.ResultsBean.WeatherDataBean> futureList = resultsBean.getWeather_data();
        futureList.remove(0);
        for (int i = 0; i < futureList.size(); i++) {
            View centerView = LayoutInflater.from(getActivity()).inflate(R.layout.main_center, null);
            centerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            futureLayout.addView(centerView);
            TextView centerDataTv = centerView.findViewById(R.id.center_tv_date);
            TextView centerTempRangeTv = centerView.findViewById(R.id.center_tv_temp_range);
            TextView centerWeatherTv = centerView.findViewById(R.id.center_tv_weather);
            ImageView centerIv = centerView.findViewById(R.id.center_iv);
            // 获取对应信息
            WeatherBean.ResultsBean.WeatherDataBean dataBean = futureList.get(i);
            centerDataTv.setText(dataBean.getDate());
            centerTempRangeTv.setText(dataBean.getTemperature());
            centerWeatherTv.setText(dataBean.getWeather());
            Picasso.with(getActivity()).load(dataBean.getDayPictureUrl()).into(centerIv);
        }
    }

    /**
     * 初始化
     */
    private void initView(View view) {
        // 初始化控件
        cityTv = view.findViewById(R.id.frag_tv_city);
        currentTempTv = view.findViewById(R.id.frag_tv_current_temp);
        weatherTv = view.findViewById(R.id.frag_tv_weather);
        dateTv = view.findViewById(R.id.frag_tv_date);
        windTv = view.findViewById(R.id.frag_tv_wind);
        tempRangeTv = view.findViewById(R.id.frag_tv_temp_range);
        dressIndexTv = view.findViewById(R.id.frag_tv_dress_index);
        carIndexTv = view.findViewById(R.id.frag_tv_car_index);
        coldIndexTv = view.findViewById(R.id.frag_tv_cold_index);
        sportIndexTv = view.findViewById(R.id.frag_tv_sport_index);
        raysIndexTv = view.findViewById(R.id.frag_tv_rays_index);
        todayIv = view.findViewById(R.id.frag_iv_today);
        futureLayout = view.findViewById(R.id.frag_layout_center);
        fragLayout = view.findViewById(R.id.frag_layout);
        // 设置点击事件监听
        dressIndexTv.setOnClickListener(this);
        carIndexTv.setOnClickListener(this);
        coldIndexTv.setOnClickListener(this);
        sportIndexTv.setOnClickListener(this);
        raysIndexTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        switch (v.getId()) {
            case R.id.frag_tv_dress_index:
                builder.setTitle("穿衣指数");
                WeatherBean.ResultsBean.IndexBean indexBean = indexList.get(0);
                String msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_tv_car_index:
                builder.setTitle("洗车指数");
                indexBean = indexList.get(1);
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_tv_cold_index:
                builder.setTitle("感冒指数");
                indexBean = indexList.get(2);
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_tv_sport_index:
                builder.setTitle("运动指数");
                indexBean = indexList.get(3);
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_tv_rays_index:
                builder.setTitle("紫外线指数");
                indexBean = indexList.get(4);
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
        }
        builder.create().show();
    }
}

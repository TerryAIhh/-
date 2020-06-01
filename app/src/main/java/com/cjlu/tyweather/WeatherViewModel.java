package com.cjlu.tyweather;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cjlu.tyweather.bean.WeatherBean;
import com.cjlu.tyweather.db.DbManager;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class WeatherViewModel extends AndroidViewModel {
    private MutableLiveData<WeatherBean> weather;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        weather = new MutableLiveData<>();
    }

    public LiveData<WeatherBean> getWeather(String city) {
        if (weather.getValue() == null) {
            loadWeather(city);
        }
        return weather;
    }

    private void loadWeather(String city) {
        String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
        String url2 = "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";
        String path = url1 + city + url2;
        RequestParams params = new RequestParams(path);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);
                weather.setValue(weatherBean);
                // 更新数据库
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
                    WeatherBean weatherBean = new Gson().fromJson(lastResult, WeatherBean.class);
                    weather.setValue(weatherBean);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}

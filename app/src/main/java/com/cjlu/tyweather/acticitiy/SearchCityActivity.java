package com.cjlu.tyweather.acticitiy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.cjlu.tyweather.MainActivity;
import com.cjlu.tyweather.R;
import com.cjlu.tyweather.base.BaseActivity;
import com.cjlu.tyweather.bean.WeatherBean;
import com.google.gson.Gson;

public class SearchCityActivity extends BaseActivity implements View.OnClickListener {
    EditText searchEdit;
    ImageView backIv, submitIv;
    GridView searchGv;
    String[] hotCities = {"上海", "北京", "广州", "苏州", "大连", "重庆", "杭州", "无锡", "天津",
            "佛山", "宁波", "南京", "成都", "东莞", "武汉", "青岛", "沈阳", "烟台", "唐山", "济南",
            "哈尔滨", "石家庄", "郑州", "泉州", "温州", "长沙", "南通", "长春", "潍坊", "绍兴", "福州",
            "淄博", "大庆", "常州", "台州", "济宁", "东营", "西安", "徐州", "临沂", "威海", "嘉兴",
            "邯郸", "洛阳", "沧州", "金华", "昆明", "南阳", "保定"};
    private ArrayAdapter<String> adapter;
    String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    String url2 = "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        searchEdit = findViewById(R.id.search_edit);
        backIv = findViewById(R.id.search_iv_back);
        submitIv = findViewById(R.id.search_iv_submit);
        searchGv = findViewById(R.id.search_gv_cities);
        // 设置按钮监听
        backIv.setOnClickListener(this);
        submitIv.setOnClickListener(this);
        // 设置 GridView 适配器
        adapter = new ArrayAdapter<>(this, R.layout.item_hot_city, hotCities);
        searchGv.setAdapter(adapter);
        setListener();
    }

    /**
     * 为 GridView 中城市设置监听事件
     */
    private void setListener() {
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = hotCities[position];
                String url = url1 + city + url2;
                loadData(url);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv_back:
                finish();
                break;
            case R.id.search_iv_submit:
                city = searchEdit.getText().toString();
                if (!TextUtils.isEmpty(city)) {
                    String url = url1 + city + url2;
                    loadData(url);
                } else {
                    Toast.makeText(this, "输入内容不能为空！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSuccess(String result) {
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);
        if (weatherBean.getError() == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city", city);
            startActivity(intent);
        } else {
            Toast.makeText(this, "暂时未收入此城市天气信息...", Toast.LENGTH_SHORT).show();
        }
    }
}
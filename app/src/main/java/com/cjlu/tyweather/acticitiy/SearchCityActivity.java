package com.cjlu.tyweather.acticitiy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.cjlu.tyweather.MainActivity;
import com.cjlu.tyweather.R;
import com.cjlu.tyweather.base.BaseActivity;
import com.cjlu.tyweather.bean.WeatherBean;
import com.cjlu.tyweather.databinding.ActivitySearchCityBinding;
import com.google.gson.Gson;

public class SearchCityActivity extends BaseActivity {
    ActivitySearchCityBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_city);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        // 设置 GridView 适配器
        adapter = new ArrayAdapter<>(this, R.layout.item_hot_city, hotCities);
        binding.searchGvCities.setAdapter(adapter);
        setListener();
    }

    /**
     * 为 GridView 中城市设置监听事件
     */
    private void setListener() {
        binding.searchGvCities.setOnItemClickListener((parent, view, position, id) -> {
            city = hotCities[position];
            String url = url1 + city + url2;
            loadData(url);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_city, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconified(false);//设置searchView处于展开状态
        searchView.onActionViewExpanded();// 当展开无输入内容的时候，没有关闭的图标
        searchView.setIconifiedByDefault(true);//默认为true在框内，设置false则在框外
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("请输入城市名...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    city = query;
                    String url = url1 + city + url2;
                    loadData(url);
                } else {
                    Toast.makeText(getApplicationContext(), "输入内容不能为空！", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
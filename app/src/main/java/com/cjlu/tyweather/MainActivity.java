package com.cjlu.tyweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cjlu.tyweather.acticitiy.CityManagerActivity;
import com.cjlu.tyweather.acticitiy.MoreActivity;
import com.cjlu.tyweather.adapter.MyPagerAdapter;
import com.cjlu.tyweather.databinding.ActivityMainBinding;
import com.cjlu.tyweather.db.DbManager;
import com.cjlu.tyweather.fragment.CityWeatherFragment;

import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActionBar supportActionBar; // 应用的标题栏
    List<Fragment> fragmentList; // 页面的数据源，fragment的列表
    List<String> cityList; // 已有城市的列表
    List<ImageView> pointList; // 底部指示器
    private MyPagerAdapter adapter; // ViewPager2 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // 初始化ActionBar
        supportActionBar = getSupportActionBar();
        // 初始化背景
        changeBg();
        // 数据的初始化
        fragmentList = new ArrayList<>();
        cityList = DbManager.queryAllCityName();

        pointList = new ArrayList<>();
        // 暂未添加任何城市，默认北京
        if (cityList.size() == 0) {
            cityList.add("北京");
        }
        // 搜索添加城市跳转到此页面
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        if (!cityList.contains(city) && !TextUtils.isEmpty(city)) {
            cityList.add(city);
        }
        // 初始化 ViewPager
        initViewPager();
        adapter = new MyPagerAdapter(this, fragmentList);
        binding.mainVp.setAdapter(adapter);
        // 初始化小圆点指示器
        initPoints();
        // 默认加载最后的一个城市
        binding.mainVp.setCurrentItem(fragmentList.size() - 1);
        supportActionBar.setTitle(cityList.get(cityList.size() - 1));
        // 设置滑动页面监听器
        setPagerListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.city_manager:
                intent.setClass(MainActivity.this, CityManagerActivity.class);
                break;
            case R.id.more_settings:
                intent.setClass(MainActivity.this, MoreActivity.class);
                break;
        }
        startActivity(intent);
        return true;
    }


    // 更新背景图
    public void changeBg() {
        SharedPreferences bg_pref = getSharedPreferences("bg_pref", MODE_PRIVATE);
        int bg_num = bg_pref.getInt("bg", 2);
        switch (bg_num) {
            case 0:
                binding.mainLayout.setBackgroundResource(R.color.purple);
                break;
            case 1:
                binding.mainLayout.setBackgroundResource(R.color.card_bg);
                break;
            case 2:
                binding.mainLayout.setBackgroundResource(R.color.pink);
                break;
        }
    }

    /**
     * 设置滑动页面监听器
     */
    private void setPagerListener() {
        binding.mainVp.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                supportActionBar.setTitle(cityList.get(position));
                for (int i = 0; i < pointList.size(); i++) {
                    pointList.get(i).setImageResource(R.drawable.ic_small_gray);
                }
                pointList.get(position).setImageResource(R.drawable.ic_small_white);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 初始化小圆点指示器
     */
    private void initPoints() {
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView pointIv = new ImageView(this);
            pointIv.setImageResource(R.drawable.ic_small_gray);
            pointIv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pointIv.getLayoutParams();
            lp.setMargins(0, 0, 20, 0);
            pointList.add(pointIv);
            binding.mainLayoutPoint.addView(pointIv);
        }
        pointList.get(pointList.size() - 1).setImageResource(R.drawable.ic_small_white);
    }

    private void initViewPager() {
        // 创建 Fragment 对象，添加到 View Pager 数据源 frangmentList中
        for (int i = 0; i < cityList.size(); i++) {
            CityWeatherFragment fragment = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city", cityList.get(i));
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
    }

    /**
     * 数据发生改变，更新主页面的 ViewPager
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        List<String> list = DbManager.queryAllCityName();
        if (list.size() == 0) {
            list.add("北京");
        }
        cityList.clear();
        cityList.addAll(list);
        fragmentList.clear();
        initViewPager();
        adapter.notifyDataSetChanged();
        // 小圆点也要更新
        pointList.clear();
        binding.mainLayoutPoint.removeAllViews();   // 将布局中所有元素全部移除
        initPoints();
        binding.mainVp.setCurrentItem(fragmentList.size() - 1);
    }
}

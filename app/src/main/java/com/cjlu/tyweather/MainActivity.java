package com.cjlu.tyweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.cjlu.tyweather.acticitiy.MoreActivity;
import com.cjlu.tyweather.adapter.MyPagerAdapter;
import com.cjlu.tyweather.acticitiy.CityManagerActivity;
import com.cjlu.tyweather.db.DbManager;
import com.cjlu.tyweather.fragment.CityWeatherFragment;

import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager2.widget.ViewPager2.*;

public class MainActivity extends AppCompatActivity {
    ImageView menuIv;
    LinearLayout pointLayout;
    ViewPager2 mainVp;
    RelativeLayout mainLayout;
    // ViewPager 的数据源
    List<Fragment> fragmentList;
    // 表示城市集合
    List<String> cityList;
    // 指示器集合
    List<ImageView> pointList;
    private MyPagerAdapter adapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控件
        menuIv = findViewById(R.id.main_iv_menu);
        pointLayout = findViewById(R.id.main_layout_point);
        mainVp = findViewById(R.id.main_vp);
        mainLayout = findViewById(R.id.main_layout);
        // 添加点击事件
        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(menuIv);
            }
        });
        // 初始化背景
        changeBg();
        // 数据的初始化
        fragmentList = new ArrayList<>();
        cityList = DbManager.queryAllCityName();
        pointList = new ArrayList<>();
        // 暂未添加任何城市，默认温州
        if (cityList.size() == 0) {
            // TODO 后续可以添加定位当前城市
            cityList.add("温州");
        }
        /* 搜索添加城市跳转到此页面 */
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        if (!cityList.contains(city) && !TextUtils.isEmpty(city)) {
            cityList.add(city);
        }
        // 初始化 ViewPager
        initViewPager();
        adapter = new MyPagerAdapter(this, fragmentList);
        mainVp.setAdapter(adapter);
        // 初始化小圆点指示器
        initPoints();
        // 默认加载最后的一个城市
        mainVp.setCurrentItem(fragmentList.size() - 1);
        // 设置滑动页面监听器
        setPagerListener();
    }

    // 更新背景图
    public void changeBg() {
        SharedPreferences bg_pref = getSharedPreferences("bg_pref", MODE_PRIVATE);
        int bg_num = bg_pref.getInt("bg", 2);
        switch (bg_num) {
            case 0:
                mainLayout.setBackgroundResource(R.color.colorAccent);
                break;
            case 1:
                mainLayout.setBackgroundResource(R.color.pink);
                break;
            case 2:
                mainLayout.setBackgroundResource(R.color.gray_bg);
                break;
        }
    }

    /**
     * 设置滑动页面监听器
     */
    private void setPagerListener() {
        mainVp.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
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
            pointLayout.addView(pointIv);
        }
        pointList.get(pointList.size() - 1).setImageResource(R.drawable.ic_small_white);
    }

    /**
     * 初始化菜单按钮
     * 并跳转到相对应 Activity
     */
    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
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
        pointLayout.removeAllViews();   // 将布局中所有元素全部移除
        initPoints();
        mainVp.setCurrentItem(fragmentList.size() - 1);
    }
}

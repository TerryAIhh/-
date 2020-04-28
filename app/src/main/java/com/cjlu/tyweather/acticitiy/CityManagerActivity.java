package com.cjlu.tyweather.acticitiy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cjlu.tyweather.R;
import com.cjlu.tyweather.adapter.CityManagerAdapter;
import com.cjlu.tyweather.db.DatabaseBean;
import com.cjlu.tyweather.db.DbManager;

import java.util.ArrayList;
import java.util.List;

public class CityManagerActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView addIv, backIv;
    SwipeMenuListView cityLv;
    List<DatabaseBean> mDatas;
    private CityManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        addIv = findViewById(R.id.city_iv_add);
        backIv = findViewById(R.id.city_iv_back);
        cityLv = findViewById(R.id.city_lv);
        mDatas = new ArrayList<>();
        // 添加点击事件监听
        addIv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        // 设置适配器
        adapter = new CityManagerAdapter(this, mDatas);
        cityLv.setAdapter(adapter);
        // 初始化滑动菜单
        initSwipeMenuListView();
    }

    /**
     * 获取数据库中真实数据源，添加到原有数据源中，提示适配器更新
     */
    @Override
    protected void onResume() {
        super.onResume();
        List<DatabaseBean> list = DbManager.queryAllInfo();
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_iv_add:
                int cityCount = DbManager.getCityCount();
                if (cityCount < 5) {
                    Intent intent = new Intent(this, SearchCityActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "存储城市已达上限，请删除后进行添加", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.city_iv_back:
                finish();
                break;
        }
    }

    private void initSwipeMenuListView() {
        // 初始化滑动删除按钮菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(CityManagerActivity.this);
                // set item background
                deleteItem.setBackground(R.drawable.delete_bg);
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        cityLv.setMenuCreator(creator);
        cityLv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final DatabaseBean city = mDatas.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(CityManagerActivity.this);
                builder.setTitle("提示信息").setMessage("您确定要删除该城市么？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbManager.deleteCityInfo(city.getCity());
                        mDatas.remove(city);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(CityManagerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                return true;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}

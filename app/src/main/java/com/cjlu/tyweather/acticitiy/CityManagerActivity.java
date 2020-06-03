package com.cjlu.tyweather.acticitiy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.cjlu.tyweather.R;
import com.cjlu.tyweather.adapter.CityManagerAdapter;
import com.cjlu.tyweather.databinding.ActivityCityManagerBinding;
import com.cjlu.tyweather.db.DatabaseBean;
import com.cjlu.tyweather.db.DbManager;
import com.cjlu.tyweather.myItemTouchHelperCallBack;

import java.util.List;

public class CityManagerActivity extends AppCompatActivity {
    ActivityCityManagerBinding binding;
    List<DatabaseBean> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_city_manager);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        mDatas = DbManager.queryAllInfo();
        // 设置适配器
        CityManagerAdapter adapter = new CityManagerAdapter(mDatas, this);
        binding.recycle.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new myItemTouchHelperCallBack(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.recycle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_city:
                int cityCount = DbManager.getCityCount();
                if (cityCount < 10) {
                    Intent intent = new Intent(this, SearchCityActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "存储城市已达上限，请删除后进行添加", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.city_manager, menu);
        return true;
    }
}

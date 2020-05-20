package com.cjlu.tyweather.acticitiy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cjlu.tyweather.MainActivity;
import com.cjlu.tyweather.R;
import com.cjlu.tyweather.databinding.ActivityMoreBinding;
import com.cjlu.tyweather.db.DbManager;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMoreBinding binding;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_more);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        binding.moreTvChangeBg.setOnClickListener(this);
        binding.moreTvVersion.setOnClickListener(this);
        binding.moreTvClearCache.setOnClickListener(this);
        binding.moreTvShare.setOnClickListener(this);
        setRGListener();
        pref = getSharedPreferences("bg_pref", MODE_PRIVATE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    /**
     * 设置RadioGroup监听
     */
    private void setRGListener() {
        binding.moreRg.setOnCheckedChangeListener((group, checkedId) -> {
            // 获取默认的背景图
            int bg = pref.getInt("bg", 0);
            SharedPreferences.Editor editor = pref.edit();
            Intent intent = new Intent(MoreActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            switch (checkedId) {
                case R.id.more_rb_green:
                    if (bg == 0) {
                        Toast.makeText(MoreActivity.this, "您当前已设置该背景", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editor.putInt("bg", 0);
                    editor.commit();
                    break;
                case R.id.more_rb_pink:
                    if (bg == 1) {
                        Toast.makeText(MoreActivity.this, "您当前已设置该背景", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editor.putInt("bg", 1);
                    editor.commit();
                    break;
                case R.id.more_rb_gray:
                    if (bg == 2) {
                        Toast.makeText(MoreActivity.this, "您当前已设置该背景", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editor.putInt("bg", 2);
                    editor.commit();
                    break;
            }
            startActivity(intent);
        });
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_tv_change_bg:
                if (binding.moreRg.getVisibility() == View.VISIBLE) {
                    binding.moreRg.setVisibility(View.GONE);
                } else {
                    binding.moreRg.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.more_tv_version:
                checkVersion();
                break;
            case R.id.more_tv_clear_cache:
                clearCache();
                break;
            case R.id.more_tv_share:
                shareApp("老铁天气，你值得拥有！");
                break;
        }
    }

    private void shareApp(String s) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(Intent.createChooser(intent, "老铁天气"));
    }

    private void checkVersion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("当前版本号").setMessage("当前版本：v" + getVersion());
        builder.setPositiveButton("检查更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MoreActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("确定", null);
        builder.create().show();
    }

    private String getVersion() {
        PackageManager packageManager = getPackageManager();
        String versionName = null;
        try {
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void clearCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("确定要清除所有缓存？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DbManager.deleteAll();
                Toast.makeText(MoreActivity.this, "已清除全部缓存", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).setNegativeButton("取消", null);
        builder.create().show();
    }
}

package com.cjlu.tyweather.base;


import androidx.fragment.app.Fragment;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * xutils 加载网络数据
 * 1. 声明整体模块
 * 2. 执行网络请求操作
 */
public class BaseFragment extends Fragment implements Callback.CommonCallback<String> {

    public void loadData(String path) {
        RequestParams params = new RequestParams(path);
        x.http().get(params, this);
    }

    @Override
    public void onSuccess(String result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}

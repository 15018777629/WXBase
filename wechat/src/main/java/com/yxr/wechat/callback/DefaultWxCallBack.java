package com.yxr.wechat.callback;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yxr.wechat.R;
import com.yxr.wechat.manager.WXManager;

/**
 * 微信默认回调(默认帮助用户处理了一些回调事件)
 * 如果用户需要自定义可以直接使用WXCallBack
 * 如果只需要部分自定义可以重写某个方法
 * Created by 63062 on 2018/2/9.
 */

public class DefaultWxCallBack implements WXCallBack {
    private final Activity activity;
    private View loadingView;

    public DefaultWxCallBack(Activity activity) {
        this.activity = activity;
    }

    public void onLoading() {
        showLoading();
    }

    @Override
    public void onWXSuccess(Object object) {
        dismissLoading(WXManager.instance().getTypeText() + "成功");
    }

    @Override
    public void onWXError(String error) {
        dismissLoading(error);
    }

    @Override
    public void onBackSelf() {
        dismissLoading(null);
    }

    private void showLoading() {
        if (!dismissLoading(null)){
            return;
        }
        try {
            FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
            loadingView = LayoutInflater.from(activity).inflate(R.layout.dialog_default_loading, decorView, false);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            loadingView.setLayoutParams(layoutParams);
            decorView.addView(loadingView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean dismissLoading(String toast){
        if (activity != null) {
            if (toast != null){
                Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
            }
            if (loadingView != null) {
                try {
                    FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
                    decorView.removeView(loadingView);
                    loadingView = null;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return activity != null;
    }
}

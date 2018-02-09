package com.yxr.wechat.callback;

/**
 * Created by 63062 on 2018/2/7.
 */

public interface WXCallBack {
    void onWXSuccess(Object object);

    void onWXError(String error);

    void onBackSelf();
}

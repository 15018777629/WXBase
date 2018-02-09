package com.yxr.wechat.callback;

import android.graphics.Bitmap;

/**
 * Created by 63062 on 2018/2/9.
 */

public interface FileCallback {
    void onSuccess(Bitmap bitmap);

    void onError();
}

package com.yxr.wechat.util;

import android.graphics.Bitmap;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.GetRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
/**
 * Created by 63062 on 2018/2/8.
 */

public class WXUtil {
    public static void simpleDownloadFile(String imageUrl, FileCallback callback) {
        GetRequest<File> fileGetRequest = OkGo.get(imageUrl);
        fileGetRequest.execute(callback);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}

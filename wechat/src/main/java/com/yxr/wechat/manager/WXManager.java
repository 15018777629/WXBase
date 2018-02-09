package com.yxr.wechat.manager;

import android.content.Context;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yxr.wechat.callback.DefaultWxCallBack;
import com.yxr.wechat.callback.WXCallBack;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信管理类
 * Created by 63062 on 2018/2/7.
 */

public class WXManager {
    public static final String TRANSACTION_IMAGE = "image";
    public static final String TRANSACTION_TEXT = "text";
    public static final String TRANSACTION_WEB_PAGE = "webPage";
    public static final String TRANSACTION_MUSIC = "music";
    public static final String TRANSACTION_VIDEO = "video";
    public static final String TRANSACTION_LOGIN = "login";

    public static final int TYPE_SHARE = 0;
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_PAY = 2;
    public static final String TAG = "WXManager";

    private Map<String, WXCallBack> wxCallBackMap = new HashMap<>();
    private IWXAPI wxApi;
    private String currTag;
    private int currType = TYPE_SHARE;
    private String wxAppId;
    private String wxSecret;
    private boolean wxAction;

    private WXManager() {

    }

    public static WXManager instance() {
        return WXManagerInstanceHolder.instance;
    }

    /**
     * 初始化，请在Application中调用
     *
     * @param context ：上下文
     * @param appId   ：微信申请的应用id
     */
    public void init(Context context, String appId, String secret) {
        wxAppId = appId;
        wxSecret = secret;
        wxApi = WXAPIFactory.createWXAPI(context, appId, true);
        wxApi.registerApp(appId);
    }

    public IWXAPI getWXAPI() {
        if (wxApi == null) {
            throw new RuntimeException("wx api is not init");
        }
        return wxApi;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public String getWxSecret() {
        return wxSecret;
    }

    /**
     * 根据tag注册微信回调，请在需要的界面初始化时注册
     *
     * @param tag      ：标记位
     * @param callBack ：回调
     */
    public void registerWXCallBackWithTag(String tag, WXCallBack callBack) {
        if (callBack == null || tag == null) {
            return;
        }
        wxCallBackMap.put(tag, callBack);
    }

    /**
     * 根据tag注销微信回调，请在注册的界面销毁时注销
     *
     * @param tag ：标记位
     */
    public void unregisterWXCallBackWithTag(String tag) {
        wxCallBackMap.remove(tag);
    }

    /**
     * 微信OnResume检查
     */
    public void onResume() {
        WXCallBack wxCallBack = wxCallBackMap.get(currTag);
        Log.e(TAG, "onResume:::::: " + wxCallBack + "::::::::::::" + wxAction );
        if (wxCallBack != null && wxAction) {
            setAction(false);
            wxCallBack.onBackSelf();
        }
    }

    public void callbackTagLoading() {
        WXCallBack callBack = wxCallBackMap.get(currTag);;
        if (callBack != null && callBack instanceof DefaultWxCallBack){
            ((DefaultWxCallBack) callBack).onLoading();
        }
    }

    /**
     * 微信错误回调
     *
     * @param error ：错误信息
     */
    public void callbackTagError(String error) {
        setAction(false);
        WXCallBack wxCallBack = wxCallBackMap.get(currTag);
        if (wxCallBack != null) {
            wxCallBack.onWXError(error);
        }
    }

    /**
     * 微信成功回调
     *
     * @param object
     */
    public void callbackTagSuccess(Object object) {
        setAction(false);
        WXCallBack wxCallBack = wxCallBackMap.get(currTag);
        if (wxCallBack != null) {
            wxCallBack.onWXSuccess(object);
        }
    }

    /**
     * 构建一个唯一标志
     *
     * @param type 分享的类型分字符串
     * @return 返回唯一字符串
     */
    public String buildTransaction(String type) {
        long currentTimeMillis = System.currentTimeMillis();
        return (type == null) ? currentTimeMillis + "" : type + currentTimeMillis;
    }

    public int getType() {
        return currType;
    }

    public void setTypeTag(int type, String tag) {
        currType = type;
        currTag = tag;
        setAction(true);
    }

    public String getTypeText() {
        switch (getType()) {
            case TYPE_SHARE:
                return "分享";
            case TYPE_LOGIN:
                return "登录";
            case TYPE_PAY:
                return "支付";
        }
        return "";
    }

    public void setAction(boolean action) {
        Log.e(TAG, "setAction:::::: " + action );
        wxAction = action;
    }

    private static class WXManagerInstanceHolder {
        private static WXManager instance = new WXManager();
    }
}

package com.yxr.wechat.manager;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.yxr.wechat.WXUserInfo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 微信登录管理
 * Created by 63062 on 2018/2/8.
 */

public class WXLoginManager {
    private WXLoginManager() {

    }

    public static WXLoginManager instance() {
        return WXLoginManagerInstanceHolder.instance;
    }

    /**
     * 微信登录
     *
     * @param tag ：回调标记
     */
    public void wxLogin(String tag) {
        WXManager.instance().setTypeTag(WXManager.TYPE_LOGIN, tag);
        IWXAPI api = WXManager.instance().getWXAPI();
        if (!api.isWXAppInstalled()) {
            // 没有安装微信客户端
            WXManager.instance().callbackTagError("请先安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login";
        api.sendReq(req);
    }

    public void getWXToken(BaseResp resp) {
        if (resp == null || !(resp instanceof SendAuth.Resp)) {
            WXManager.instance().callbackTagError("获取用户微信信息失败");
            return;
        }
        SendAuth.Resp respAuth = (SendAuth.Resp) resp;
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                WXManager.instance().getWxAppId() + "&secret=" + WXManager.instance().getWxSecret() +
                "&code=" + respAuth.code + "&grant_type=authorization_code";

        obGet(accessTokenUrl, null, new LoginCallBack() {
            @Override
            public void callBack(@NonNull Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    String accessToken = jsonObject.getString("access_token");
                    String openId = jsonObject.getString("openid");
                    getWXUserInfo(accessToken, openId);
                } catch (Exception e) {
                    e.printStackTrace();
                    WXManager.instance().callbackTagError("获取用户微信信息失败");
                }
            }
        });
    }

    private void getWXUserInfo(String accessToken, String openId) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
        obGet(url, null, new LoginCallBack() {
            @Override
            public void callBack(@NonNull Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    String nickname = jsonObject.getString("nickname");
                    String unionId = jsonObject.getString("unionid");
                    String name = new String(nickname.getBytes("ISO-8859-1"), "UTF-8");
                    String avatar = jsonObject.getString("headimgurl");
                    String sex = jsonObject.getString("sex");
                    String province = jsonObject.getString("province");
                    String country = jsonObject.getString("country");
                    String city = jsonObject.getString("city");

                    WXUserInfo userInfo = new WXUserInfo(unionId, name, avatar, sex, province, country, city);
                    WXManager.instance().callbackTagSuccess(userInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    WXManager.instance().callbackTagError("获取用户微信信息失败");
                }
            }
        });
    }

    private void obGet(String url, Map<String, String> map, final LoginCallBack callBack) {
        if (map == null) {
            map = new HashMap<>();
        }
        OkGo.<String>get(url)
                .params(map)
                .converter(new StringConvert())
                .adapt(new ObservableResponse<String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<String> response) {
                        callBack.callBack(response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        WXManager.instance().callbackTagError("获取用户微信信息失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static class WXLoginManagerInstanceHolder {
        private static WXLoginManager instance = new WXLoginManager();
    }

    private interface LoginCallBack {
        void callBack(@NonNull Response<String> response);
    }
}

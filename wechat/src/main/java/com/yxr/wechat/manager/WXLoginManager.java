package com.yxr.wechat.manager;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.yxr.wechat.WXUserInfo;

import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
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

        obGet(accessTokenUrl, new LoginCallBack() {
            @Override
            public void callBack(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
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
        obGet(url, new LoginCallBack() {
            @Override
            public void callBack(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
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

    private void obGet(final String getUrl, final LoginCallBack callBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                StringBuffer stringBuffer = null;
                try {
                    URL url = new URL(getUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10 * 1000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        stringBuffer = new StringBuffer();
                        byte[] b = new byte[4096];
                        int n;
                        while ((n = inputStream.read(b)) != -1) {
                            stringBuffer.append(new String(b, 0, n));
                        }
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                emitter.onNext(stringBuffer == null ? null : stringBuffer.toString());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        callBack.callBack(response);
                    }
                });
    }

    private static class WXLoginManagerInstanceHolder {
        private static WXLoginManager instance = new WXLoginManager();
    }

    private interface LoginCallBack {
        void callBack(String response);
    }
}

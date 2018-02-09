package com.yxr.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yxr.wechat.manager.WXManager;

/**
 * Created by Sl on 2017/3/14.
 */

public class WXBasePayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String APP_ID = "wx7407b7423d2dbf65";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    private void initData(){
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                WXManager.instance().callbackTagSuccess(resp);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                WXManager.instance().callbackTagError(WXManager.instance().getTypeText() + "取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                WXManager.instance().callbackTagError("用户拒绝了微信授权");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                WXManager.instance().callbackTagError("发送失败");
                break;
            default:
                break;
        }
        finish();
    }
}

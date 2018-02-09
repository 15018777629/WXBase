package com.yxr.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yxr.wechat.manager.WXLoginManager;
import com.yxr.wechat.manager.WXManager;

/**
 * Created by Sl on 2017/3/6.
 */

public class WXBaseEntryActivity extends Activity implements IWXAPIEventHandler {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WXManager.instance().setAction(false);
    }

    private void initData() {
        WXManager.instance().setAction(true);
        WXManager.instance().getWXAPI().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e(WXManager.TAG, "onReq:::::::: " + req );
    }


    @Override
    public void onResp(BaseResp resp) {
        Log.e(WXManager.TAG, "onResp:::::::: " + resp.errCode );
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (WXManager.instance().getType() == WXManager.TYPE_LOGIN) {
                    WXLoginManager.instance().getWXToken(resp);
                } else {
                    WXManager.instance().callbackTagSuccess(resp);
                }
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
                WXManager.instance().callbackTagError("其他错误");
                break;
        }
        finish();
    }
}

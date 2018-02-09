package com.yxr.wechat.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.yxr.wechat.callback.WXCallBack;
import com.yxr.wechat.util.WXUtil;

import java.io.File;

/**
 * 微信分享管理
 * Created by 63062 on 2018/2/8.
 */

public class WXShareManager {

    private WXShareManager() {

    }

    public static WXShareManager instance() {
        return WXShareManagerInstanceHolder.instance;
    }

    /**
     * 文本分享
     *
     * @param shareFriend ：是否分享给好友
     * @param text        ：图片内容
     * @param tag         ：标记符，主要用于回调(请和registerWXCallBackWithTag的tag保持一致)
     */
    public void shareText(boolean shareFriend, String text, String tag) {
        WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        WXTextObject wxTextObject = new WXTextObject();
        wxTextObject.text = text;

        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = wxTextObject;
        message.description = text;
        sendReq(shareFriend, WXManager.TRANSACTION_TEXT, message);
    }

    /**
     * 网络图片分享
     *
     * @param shareFriend ：是否分享给微信好友
     * @param title       ：分享标题
     * @param imageUrl    ：图片链接
     * @param tag         ：标记符，主要用于回调
     */
    public void shareImage(final boolean shareFriend, final String title, String imageUrl, final String tag) {
        WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        downloadImage(imageUrl, new BitmapCallBack() {
            @Override
            public void callBack(Bitmap bitmap) {
                shareImage(shareFriend, title, tag, bitmap, false);
            }
        });
    }

    /**
     * 网络图片分享
     */
    public void shareImage(boolean shareFriend, String title, Bitmap bitmap, String tag) {
        shareImage(shareFriend, title, tag, bitmap, true);
    }

    /**
     * 网络图片分享
     */
    private void shareImage(boolean shareFriend, String title, String tag, Bitmap bitmap, boolean setTypeTag) {
        if (setTypeTag) {
            WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        }
        if (bitmap == null || bitmap.isRecycled()) {
            WXManager.instance().callbackTagError("获取图片失败");
            return;
        }

        int width = Math.max(1, bitmap.getWidth());
        int height = Math.max(1, bitmap.getHeight());
        float scale = width / (float) height;
        if (scale > 1) {
            width = 100;
            height = (int) (width * scale);
        } else {
            height = 100;
            width = (int) (height * scale);
        }

        WXImageObject imageObject = new WXImageObject(bitmap);

        WXMediaMessage message = getWXMediaMessage(imageObject, title, "", bitmap, width, height);
        sendReq(shareFriend, WXManager.TRANSACTION_IMAGE, message);
    }

    /**
     * 链接网址分享
     *
     * @param shareFriend ：是否分享给微信好友
     * @param title       ：标题
     * @param content     ：内容
     * @param imageUrl    ：图片地址
     * @param webUrl      ：链接地址
     * @param tag         ：回调标记
     */
    public void shareWebPage(final boolean shareFriend, final String title, final String content, String imageUrl, final String webUrl, final String tag) {
        WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        downloadImage(imageUrl, new BitmapCallBack() {
            @Override
            public void callBack(Bitmap bitmap) {
                shareWebPage(shareFriend, title, content, bitmap, webUrl, tag, false);
            }
        });
    }

    public void shareWebPage(final boolean shareFriend, final String title, final String content, Bitmap bitmap
            , final String webUrl, final String tag) {
        shareWebPage(shareFriend, title, content, bitmap, webUrl, tag, true);
    }

    private void shareWebPage(final boolean shareFriend, final String title, final String content, Bitmap bitmap
            , final String webUrl, final String tag, boolean setTypeTag) {
        if (setTypeTag) {
            WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        }

        WXWebpageObject webPageObject = new WXWebpageObject();
        webPageObject.webpageUrl = webUrl;

        WXMediaMessage message = getWXMediaMessage(webPageObject, title, content, bitmap, 100, 100);
        sendReq(shareFriend, WXManager.TRANSACTION_WEB_PAGE, message);
    }

    /**
     * 音乐分享
     *
     * @param shareFriend ：是否分享非微信好友
     * @param musicUrl    ：音乐链接
     * @param title       ：标题
     * @param description ：内容简介
     * @param iconUrl     ：图片地址
     * @param tag         ：回调标记
     */
    public void shareMusic(final boolean shareFriend, final String musicUrl, final String title, final String description, String iconUrl, final String tag) {
        WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        downloadImage(iconUrl, new BitmapCallBack() {
            @Override
            public void callBack(Bitmap bitmap) {
                shareMusic(shareFriend, musicUrl, title, description, bitmap, tag, false);
            }
        });
    }

    public void shareMusic(boolean shareFriend, String musicUrl, String title, String description, Bitmap bitmap, String tag) {
        shareMusic(shareFriend, musicUrl, title, description, bitmap, tag, true);
    }

    public void shareMusic(boolean shareFriend, String musicUrl, String title, String description, Bitmap bitmap
            , String tag, boolean setTypeTag) {
        if (setTypeTag) {
            WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        }
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicUrl = musicUrl;
        WXMediaMessage wxMediaMessage = getWXMediaMessage(musicObject, title, description, bitmap, 100, 100);
        sendReq(shareFriend, WXManager.TRANSACTION_MUSIC, wxMediaMessage);
    }

    /**
     * 视频分享
     *
     * @param shareFriend ：是否分享给微信好友
     * @param videoUrl    ：视频地址
     * @param title       ：标题
     * @param description ：简介
     * @param iconUrl     ：图片地址
     * @param tag         ：回调标记
     */
    public void shareVideo(final boolean shareFriend, final String videoUrl, final String title, final String description, String iconUrl, final String tag) {
        WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        downloadImage(iconUrl, new BitmapCallBack() {
            @Override
            public void callBack(Bitmap bitmap) {
                shareVideo(shareFriend, videoUrl, title, description, bitmap, tag, false);
            }
        });
    }

    public void shareVideo(boolean shareFriend, String videoUrl, String title, String description, Bitmap bitmap, String tag) {
        shareMusic(shareFriend, videoUrl, title, description, bitmap, tag, true);
    }

    public void shareVideo(boolean shareFriend, String videoUrl, String title, String description, Bitmap bitmap
            , String tag, boolean setTypeTag) {
        if (setTypeTag) {
            WXManager.instance().setTypeTag(WXManager.TYPE_SHARE, tag);
        }
        WXVideoObject videoObject = new WXVideoObject();
        videoObject.videoUrl = videoUrl;
        WXMediaMessage wxMediaMessage = getWXMediaMessage(videoObject, title, description, bitmap, 100, 100);
        sendReq(shareFriend, WXManager.TRANSACTION_VIDEO, wxMediaMessage);
    }

    private WXMediaMessage getWXMediaMessage(WXMediaMessage.IMediaObject object, String title, String description
            , Bitmap bitmap, int width, int height) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = object;
        message.title = title;
        message.description = description;
        Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        message.setThumbImage(thumbBitmap);

        thumbBitmap.recycle();
        thumbBitmap = null;

        bitmap.recycle();
        bitmap = null;
        return message;
    }

    private void downloadImage(String imageUrl, final BitmapCallBack callBack) {
        WXUtil.simpleDownloadFile(imageUrl, new FileCallback() {
            @Override
            public void onSuccess(Response<File> response) {
                File file = response.body();
                if (file == null || !file.exists()) {
                    WXManager.instance().callbackTagError("图片下载失败");
                } else {
                    if (callBack != null) {
                        callBack.callBack(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    }
                }
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                WXManager.instance().callbackTagError("图片下载失败");
            }
        });
    }

    /**
     * 发送信息到微信
     */
    private void sendReq(boolean shareFriend, String type, WXMediaMessage message) {
        if (message == null) {
            WXManager.instance().callbackTagError("分享失败");
            return;
        }
        IWXAPI api = WXManager.instance().getWXAPI();
        if (!api.isWXAppInstalled()) {
            // 没有安装微信客户端
            WXManager.instance().callbackTagError("请先安装微信客户端");
            return;
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = WXManager.instance().buildTransaction(type);
        req.message = message;
        req.scene = shareFriend ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 根据tag注册微信回调，请在需要的界面初始化时注册
     *
     * @param tag      ：标记位
     * @param callBack ：回调
     */
    public void registerWXCallBackWithTag(String tag, WXCallBack callBack) {
        WXManager.instance().registerWXCallBackWithTag(tag, callBack);
    }

    /**
     * 根据tag注销微信回调，请在注册的界面销毁时注销
     *
     * @param tag ：标记位
     */
    public void unregisterWXCallBackWithTag(String tag) {
        WXManager.instance().unregisterWXCallBackWithTag(tag);
    }

    private static class WXShareManagerInstanceHolder {
        private static WXShareManager instance = new WXShareManager();
    }

    private interface BitmapCallBack {
        void callBack(Bitmap bitmap);
    }
}

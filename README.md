# WXBase
## 如何使用
我们开发经常会涉及微信的分享，登录，支付等功能。但是，微信的官方文档真的很烂，而且集成比较麻烦。再者图片下载，获取微信用户信息很是繁琐，为了处理微信的一些回调也是让人很头疼。但是，当你用这个工具库的时候，你会发现一切都那么简单。我们只要配置一些东西，写个回调，甚至回调我们也帮你处理好了。最后只需要一两行代码，就可以帮我们完成这个繁琐的工作。
### 进行依赖
```java
compile 'com.yxr.wxbase:wxbase:0.0.3'

// 还需要依赖以下第三方库
compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
compile 'io.reactivex.rxjava2:rxjava:2.1.3'
compile 'com.tencent.mm.opensdk:wechat-sdk-android-with-mta:+'
```
### 代码编辑
* Step1：和官方一样先配置WXEntryActivity和WXPayEntryActivity
```java
public class WXEntryActivity extends WXBaseEntryActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
```
配置AndroidManifest.xml中（和微信官方一致）
```java
 <activity
        android:name=".wxapi.WXEntryActivity"
        android:exported="true"
        android:launchMode="singleTop"
        android:screenOrientation="portrait"
        android:theme="@android:style/Theme.Translucent.NoTitleBar"/>
```
WXPayEntryActivity一致，可完全参照WXEntryActivity
* Step2：在Application的onCreate中进行初始化
```java
@Override
public void onCreate() {
    super.onCreate();
    WXManager.instance().init(this, WX_APP_ID, WX_SECRET);
}
```
* Step3：注册监听和重写OnResume()处理用户没有按照正常流程返回APP的事件
```java
private void initListener() {
    WXManager.instance().registerWXCallBackWithTag(WX_TAG, new WXCallBack() {
        @Override
        public void onWXSuccess(Object o) {
            toast("图片分享成功");
        }
        @Override
        public void onWXError(String s) {
            toast("图片分享失败");
        }
        @Override
        public void onBackSelf() {
            toast("用户没有按照分享流程回到APP");
        }
    });
}
    @Override
    protected void onResume() {
        super.onResume();
        WXManager.instance().onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        WXManager.instance().unregisterWXCallBackWithTag(WX_TAG);
    }
```
当然我们也提供了默认的回调处理，你可以只实例化而不用处理任何回调，或者只处理某个你想处理的回调方法（重写某个方法）
```java
WXManager.instance().registerWXCallBackWithTag(WX_TAG, new DefaultWxCallBack(activity));
```
* Step4：调用相应的功能
```java
 // 图片分享
WXShareManager.instance().shareImage(true,"TITLE","http://pic30.nipic.com/20130622/10558908_111729300000_2.jpg", WX_TAG);

 // 链接分享(网页分享)
WXShareManager.instance().shareWebPage(true, "TITLE", "CONTENT","http://pic30.nipic.com/20130622/10558908_111729300000_2.jpg", "https://www.baidu.com", WX_TAG);

 // 微信登录
 WXLoginManager.instance().wxLogin(WX_TAG);
```

PS：微信登录还需要测试一下（如果遇到什么问题，请及时联系我），微信支付目前还没有集成进去，目测会在下个版本加入。
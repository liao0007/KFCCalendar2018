package com.youbohudong.kfccalendar2018.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.youbohudong.kfccalendar2018.bean.WeChatResponseEvent;
import com.youbohudong.kfccalendar2018.utils.WechatManager;
import de.greenrobot.event.EventBus;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册API
        IWXAPI api = WechatManager.createWXAPI(this);
        api.handleIntent(getIntent(), this);
        Log.i("savedInstanceState", " sacvsa" + api.handleIntent(getIntent(), this));
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    //  发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        EventBus.getDefault().post(new WeChatResponseEvent(resp));
        finish();

    }
}

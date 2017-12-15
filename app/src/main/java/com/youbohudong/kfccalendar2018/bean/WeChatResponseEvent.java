package com.youbohudong.kfccalendar2018.bean;

import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * Created by ${bcq} on 2017/12/12.
 */

public class WeChatResponseEvent {
    public BaseResp payload;

    public WeChatResponseEvent(BaseResp payload) {
        this.payload = payload;
    }

}

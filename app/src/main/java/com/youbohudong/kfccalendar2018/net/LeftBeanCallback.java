package com.youbohudong.kfccalendar2018.net;

import com.google.gson.Gson;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.zhy.http.okhttp.callback.Callback;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by ${bcq} on 2017/12/9.
 */

public abstract class LeftBeanCallback extends Callback<LeftBean> {
    @Override
    public LeftBean parseNetworkResponse(Response response, int id) throws IOException {
        String string = response.body().string();
        LeftBean user = new Gson().fromJson(string, LeftBean.class);
        return user;
    }
}

package com.youbohudong.kfccalendar2018.net;

import com.google.gson.Gson;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.zhy.http.okhttp.callback.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

/**
 * Created by ${bcq} on 2017/12/9.
 */

public abstract class ListLeftBeanCallback  extends Callback<List<LeftBean>>
{

    @Override
    public List<LeftBean> parseNetworkResponse(Response response, int id) throws IOException
    {
        String string = response.body().string();
        List<LeftBean> user = new Gson().fromJson(string, List.class);
        return user;
    }


}

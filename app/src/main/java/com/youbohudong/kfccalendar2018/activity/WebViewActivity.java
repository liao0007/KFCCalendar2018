package com.youbohudong.kfccalendar2018.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.utils.DeviceUuidFactory;

import java.util.UUID;

/**
 * Created by ${bcq} on 2017/12/11.
 */

public class WebViewActivity extends BaseActivity {
    private ImageView img_back;
    private WebView webView;
    private String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
        initData();
        initListening();
    }

    @Override
    public void initView() {
        url=getIntent().getStringExtra("URL");
       img_back=(ImageView) findViewById(R.id.img_back);
        webView=(WebView) findViewById(R.id.webview);
    }

    @Override
    public void initData() {
        UUID uuid= new DeviceUuidFactory(this).getDeviceUuid();
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("https://www.youbohudong.com/biz/vip/kfc/calendar-2018/tasks"+"?udid="+uuid);
        webView.loadUrl(url+"?udid="+uuid);
    }



    @Override
    public void initListening() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}

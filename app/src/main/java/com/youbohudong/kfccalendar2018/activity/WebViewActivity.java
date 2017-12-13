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
        url = getIntent().getStringExtra("URL");
        img_back = (ImageView) findViewById(R.id.img_back);
        webView = (WebView) findViewById(R.id.webview);
    }

    @Override
    public void initData() {
        UUID uuid = new DeviceUuidFactory(this).getDeviceUuid();
        webView.getSettings().setJavaScriptEnabled(true);
        String urlWithUdid = url.contains("?") ? url + "&" + "udid=" + uuid : url + "?" + "udid=" + uuid;
        webView.loadUrl(urlWithUdid);
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

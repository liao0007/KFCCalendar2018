package com.youbohudong.kfccalendar2018.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.utils.DeviceUuidFactory;
import com.youbohudong.kfccalendar2018.utils.WechatManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ${bcq} on 2017/12/11.
 */

public class WebViewActivity extends BaseActivity {
    private static final String Schema = "kc2018://";
    private static final String SchemaShareAction = "share";
    private static final String SchemaScanAction = "scan";

    private ImageView img_back;
    private WebView webView;
    private String url;
    private String callingActivity;

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
        img_back = findViewById(R.id.img_back);
        webView = findViewById(R.id.webview);

        this.callingActivity = getIntent().getStringExtra("calling-activity");
    }

    @Override
    public void initData() {
        UUID uuid = new DeviceUuidFactory(this).getDeviceUuid();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.addJavascriptInterface(new JsInteration(this), "android");
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
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;

            }
        });
    }

    public class JsInteration {
        private Context context;

        public JsInteration(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void postMessage(String location) {
            location = location.toLowerCase();
            if (location.startsWith(Schema)) {
                String instruction = location.substring(Schema.length());
                if (instruction.startsWith(SchemaScanAction)) {
                    if (callingActivity.equals(ArActivity)) {
                        finish();
                    } else {
                        startActivity(new Intent(WebViewActivity.this, ArActivity.class));
                    }

                } else if (instruction.startsWith(SchemaShareAction)) {
                    instruction = instruction.substring(SchemaShareAction.length());
                    Map<String, String> params = urlToParams(instruction);
                    WechatManager.shareUrl(context, params.get("url"), params.get("title"), params.get("thumb"), SendMessageToWX.Req.WXSceneSession);

                } else {
                    Toast.makeText(WebViewActivity.this, "解析错误", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(WebViewActivity.this, "解析错误", Toast.LENGTH_LONG).show();
            }
        }

        private Map<String, String> urlToParams(String url) {
            Map<String, String> params = new HashMap<>();

            for (String paramString :
                    url.split("&")) {
                try {
                    String[] p = paramString.split("=");
                    params.put(p[0], URLDecoder.decode(p[1], "utf-8"));
                } catch (UnsupportedEncodingException ex) {

                }
            }
            return params;
        }
    }

}

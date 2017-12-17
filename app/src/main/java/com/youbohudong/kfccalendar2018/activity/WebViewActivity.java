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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.WeChatResponseEvent;
import com.youbohudong.kfccalendar2018.utils.DeviceUuidFactory;
import com.youbohudong.kfccalendar2018.utils.WechatManager;
import de.greenrobot.event.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

    private ProgressBar loadingProgressBar;

    private String callingActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        ImageButton navigationBackImageButton = findViewById(R.id.navigationBackImageButton);
        navigationBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
        UUID uuid = new DeviceUuidFactory(this).getDeviceUuid();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.addJavascriptInterface(new JsInteration(this), "android");

        String url = getIntent().getStringExtra("URL");
        String urlWithUuid = url.contains("?") ? url + "&" + "udid=" + uuid : url + "?" + "udid=" + uuid;
        webView.loadUrl(urlWithUuid);

        callingActivity = getIntent().getStringExtra("calling-activity");

        initView();
        initData();
        initListening();
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListening() {
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
                    /* scan */
                    if (callingActivity!= null && callingActivity.equals(ArActivity)) {
                        finish();
                    } else {
                        startActivity(new Intent(WebViewActivity.this, ArActivity.class));
                        finish();
                    }

                } else if (instruction.startsWith(SchemaShareAction)) {
                    /* share */
                    instruction = instruction.substring(SchemaShareAction.length() + 1); //1 for '?'
                    Map<String, String> params = urlToParams(instruction);

                    int scene = 0;
                    if (params.get("type").length() > 0 && Integer.parseInt(params.get("type")) == 1) {
                        scene = SendMessageToWX.Req.WXSceneSession;
                    } else {
                        scene = SendMessageToWX.Req.WXSceneTimeline;
                    }
                    WechatManager.shareUrl(context, params.get("url"), params.get("title"), params.get("thumb"), scene);

                }
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(WeChatResponseEvent event) {
        switch (event.payload.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(WebViewActivity.this, "分享成功", Toast.LENGTH_SHORT).show();

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_UNSUPPORT:
            default:
                Toast.makeText(WebViewActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

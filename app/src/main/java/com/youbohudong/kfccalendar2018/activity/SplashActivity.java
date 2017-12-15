package com.youbohudong.kfccalendar2018.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.WeChatResponseEvent;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by ${bcq} on 2017/11/6.
 */

public class SplashActivity extends BaseActivity {
    private static final String IsInitialLaunchKey = "IsInitialLaunchKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(this);
        boolean isInitialLaunch = sharedPreferencesUtils.getBoolean(IsInitialLaunchKey, true);

        if (isInitialLaunch) {
            startActivity(new Intent(SplashActivity.this, CameraActivity.class));
            SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

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

}

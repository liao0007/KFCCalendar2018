package com.youbohudong.kfccalendar2018.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;

/**
 * Created by ${bcq} on 2017/11/6.
 */

public class SplashActivity extends BaseActivity {
    private static final String ISFIRST = "is_first";
    private boolean isFirst;
    SharedPreferencesUtils spUtils;
    private static final int INTOMAIN = 101;
    private static final int INTOGUIDE = 102;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INTOMAIN://进入主页
                    startActivity(new Intent(SplashActivity.this, CustomerCameraActivity.class));
                    SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case INTOGUIDE://进入向导
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        spUtils = new SharedPreferencesUtils(this);
        isFirst = spUtils.getBln(ISFIRST, true);
        initView();
        initData();
        initListening();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        if (isFirst) {
            Message msg = new Message();
            msg.what = INTOGUIDE;
            mHandler.sendMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = INTOMAIN;
            mHandler.sendMessageDelayed(msg, 2000);
        }
    }

    @Override
    public void initListening() {

    }
}

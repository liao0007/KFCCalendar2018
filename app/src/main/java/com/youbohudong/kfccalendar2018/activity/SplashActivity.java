package com.youbohudong.kfccalendar2018.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.permission.PermissionUtils;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;

/**
 * Created by ${bcq} on 2017/11/6.
 */

public class SplashActivity extends BaseActivity {
    private static final String IsInitialLaunchKey = "IsInitialLaunchKey";

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (PermissionUtils.isOverMarshmallow()) {
            requestPermissions();
        } else {
            start();
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

    private void start() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(SplashActivity.this);
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
    }

    private void requestPermissions() {
        int requestCode = 0x001;
        PermissionUtils.requestPermissions(SplashActivity.this, permissions, requestCode, new PermissionUtils.OnPermissionCallBack() {
            @Override
            public void onPermissionAllowed() {
                start();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(SplashActivity.this, "权限获取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionUtils.onActivityResult(requestCode, resultCode, data);
    }


}

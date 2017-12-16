package com.youbohudong.kfccalendar2018.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ${bcq} on 2017/11/6.
 */

public class SplashActivity extends BaseActivity {
    private static final String IsInitialLaunchKey = "IsInitialLaunchKey";

    private static final String MIUI = "miui";
    private final int REQUEST_CODE = 100;
    private SharedPreferencesUtils sharedPreferencesUtils;

    private static final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final Map<String, String> permissionToText;

    static {
        HashMap<String, String> map = new HashMap<>();
        map.put(Manifest.permission.CAMERA, "相机");
        map.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写缓存");
        map.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读缓存");
        permissionToText = Collections.unmodifiableMap(map);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferencesUtils = new SharedPreferencesUtils(SplashActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission(true, Manifest.permission.CAMERA);
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
            sharedPreferencesUtils.setBoolean(IsInitialLaunchKey, false);
            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, CameraActivity.class));
            SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }


    private void checkPermission(boolean isFirstBoot, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //已经拒绝过了。
                showDialog(permission);
            } else {
                //申请权限。
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }

        } else {
            //有权限
            //如果是小米手机 则要判断系统设置中是否有允许。
            if (checkAppOps(this, AppOpsManager.OPSTR_CAMERA) && checkAppOps(this, AppOpsManager.OPSTR_READ_EXTERNAL_STORAGE) && checkAppOps(this, AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE) ) {
                //通过权限
                start();
            } else {
                showToast("权限被关闭，请去应用设置打开权限");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == -1) {
                    //权限被拒绝了
                    showDialog(permissions[0]);
                } else {
                    //获得了权限。
                    checkPermission(false, permissions[0]);
                }
            } else {
                showToast(permissions[0] + "权限被拒绝");
            }
        }
    }

    private void showDialog(String permissionCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("需要" + permissionToText.get(permissionCode) + "权限").setTitle("提示").setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goSettingPage();
            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goSettingPage() {
        Context context = SplashActivity.this.getApplicationContext();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        SplashActivity.this.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                checkPermission(false, permissions[0]);
                break;
        }
    }

    /**
     * 查看原生态的权限是否有授权
     *
     * @param context
     * @param op      如定位权限AppOpsManager.OPSTR_FINE_LOCATION
     * @return
     */
    private boolean checkAppOps(Context context, String op) {
        if (isMIUI()) { // 只有小米手机才检测
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int checkOp = appOpsManager.checkOp(op, Binder.getCallingUid(), context.getPackageName());
                if (checkOp == AppOpsManager.MODE_IGNORED) {
                    return false;
                }
            }
        }
        return true;
    }

    // 检测MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    // 注意这个状态最好用SharePreference保存起来，需要每次读取检测
    private boolean isMIUI() {
        //获取缓存状态
        String miui = sharedPreferencesUtils.getString(MIUI, null);

        if (miui != null) {
            if ("1".equals(miui))
                return true;
            else if ("2".equals(miui))
                return false;
        }
        Properties prop = new Properties();
        boolean isMIUI;
        try {
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        isMIUI = prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        sharedPreferencesUtils.setString(MIUI, isMIUI ? "1" : "2");
        return isMIUI;
    }

    private void showToast(String text) {
        Toast.makeText(SplashActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}

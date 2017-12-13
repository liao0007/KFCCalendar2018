package com.youbohudong.kfccalendar2018.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.easyar.Engine;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.ar.GLView;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.MyEvent;
import com.youbohudong.kfccalendar2018.bean.TaskCompletionBean;
import com.youbohudong.kfccalendar2018.utils.DeviceUuidFactory;
import com.youbohudong.kfccalendar2018.view.My_Dialog;
import com.youbohudong.kfccalendar2018.view.ScanView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ${bcq} on 2017/12/10.
 */

public class ArActivity extends BaseActivity implements View.OnClickListener {
    private static String key = "amxBNPSXKbRBragBOjnJ0rV5tjSBwQZFk3SqTyd8qlTOv54A8CFjO4fP8RaVD9NDDKcvzXc4aPWHFj7cW5gtViFP1Q4j5nD23zodBz30agY29ai2ar7VQPcW7n41yxP8zv5ZlNhWy1vY4xujQpW8U34E9ZLyKT3byHamzdqWwUD1jnoGS82pRYqGQXiiQGn2pfpwC5BO";
    private GLView glView;
    private ScanView scanView;
    private TextView txt_action, txt_des, txt_gosee;
    private ImageView img_scan;
    private LinearLayout ll_root;
    private TaskCompletionBean taskCompletionBean;
    My_Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dialog = new My_Dialog(this);
        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
        }
        EventBus.getDefault().register(this);
        glView = new GLView(this);

        final View v = LayoutInflater.from(this).inflate(R.layout.scan_layout, null);
        img_scan = (ImageView) v.findViewById(R.id.img_scan);
        ll_root = (LinearLayout) v.findViewById(R.id.ll_root);
        txt_des = (TextView) v.findViewById(R.id.txt_des);
        txt_gosee = (TextView) v.findViewById(R.id.txt_gosee);

        txt_gosee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_gosee.getText().toString().equals("知道了")) {
                    finish();
                } else {
                    Intent intent = new Intent(ArActivity.this, WebViewActivity.class);
                    intent.putExtra("URL", taskCompletionBean.getCompletionUrl());
                    startActivity(intent);
                }
            }
        });
        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                ((ViewGroup) findViewById(R.id.preview)).addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            }

            @Override
            public void onFailure() {
            }
        });
        initView();
        initListening();
        initData();
    }

    @Override
    public void initView() {
        scanView = (ScanView) findViewById(R.id.scanview);
        scanView.play();
        txt_action = (TextView) findViewById(R.id.btn_event_list);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListening() {
        txt_action.setOnClickListener(this);
    }

    private interface PermissionCallback {
        void onSuccess();

        void onFailure();
    }

    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;

    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glView != null) {
            glView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (glView != null) {
            glView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_event_list://动作
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("URL", "https://www.youbohudong.com/biz/vip/kfc/calendar-2018/tasks");
                startActivity(intent);
                break;
        }
    }

    public void onEventMainThread(MyEvent event) {
        String name = (String) event.what;
        UUID uuid = new DeviceUuidFactory(this).getDeviceUuid();
        completeTask(uuid, name);
//        Toast.makeText(this,name,Toast.LENGTH_LONG).show();
    }

    /**
     * 从服务器获取数据
     */
    private void completeTask(UUID uuid, String taskKey) {
        dialog.ShowDialog();
        String url = "https://www.youbohudong.com/api/biz/vip/kfc/calendar-2018/tasks/"+uuid+"/"+taskKey;
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new TaskCompletionCallback());
    }

    public class TaskCompletionCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id) {
            setTitle("TaskCompletion");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            dialog.DismissDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.DismissDialog();
            txt_action.setVisibility(View.GONE);
            Gson gson = new Gson();
            taskCompletionBean = gson.fromJson(response, TaskCompletionBean.class);
            if (!TextUtils.isEmpty(taskCompletionBean.getCompletionResource())) {
                img_scan.setVisibility(View.VISIBLE);
                Glide.with(ArActivity.this).load(taskCompletionBean.getCompletionResource()).into(img_scan);
            } else {
                img_scan.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(taskCompletionBean.getCompletionUrl())) {
                ll_root.setVisibility(View.VISIBLE);
                txt_des.setText(taskCompletionBean.getCompletionDescription());
                txt_gosee.setText("去看看");
            } else {
                ll_root.setVisibility(View.VISIBLE);
                txt_gosee.setText("知道了");
            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}

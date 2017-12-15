package com.youbohudong.kfccalendar2018.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cn.easyar.Engine;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.gson.Gson;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.ar.GLView;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.ArScanEvent;
import com.youbohudong.kfccalendar2018.bean.TaskCompletionBean;
import com.youbohudong.kfccalendar2018.utils.DeviceUuidFactory;
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

public class ArActivity extends BaseActivity {

    private static final String EasyArKey = "amxBNPSXKbRBragBOjnJ0rV5tjSBwQZFk3SqTyd8qlTOv54A8CFjO4fP8RaVD9NDDKcvzXc4aPWHFj7cW5gtViFP1Q4j5nD23zodBz30agY29ai2ar7VQPcW7n41yxP8zv5ZlNhWy1vY4xujQpW8U34E9ZLyKT3byHamzdqWwUD1jnoGS82pRYqGQXiiQGn2pfpwC5BO";

    private GLView glView;

    private RelativeLayout overlayRelativeLayout;

    private LinearLayout scanAnimationLinearLayout;

    private RelativeLayout scanSuccessRelativeLayout;
    private ProgressBar scanSuccessProgressBar;
    private LinearLayout scanSuccessDetailLinearLayout;
    private ImageView scanSuccessImageView;
    private TextView scanSuccessTextView;
    private Button scanSuccessGoButton;

    private TaskCompletionBean taskCompletionBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Engine.initialize(this, EasyArKey)) {
            Log.e("ArCore", "Initialization Failed.");
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final View activityAr = LayoutInflater.from(this).inflate(R.layout.activity_ar, null);
        setContentView(activityAr);

        /* gl view*/
        glView = new GLView(this);
        requestCameraPermission(new CameraPermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.glViewRelativeLayout)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
            }
        });

        /* link elements */
        overlayRelativeLayout = activityAr.findViewById(R.id.overlayRelativeLayout);

        /* navigation */
        ImageButton navigationBackImageButton = activityAr.findViewById(R.id.navigationBackImageButton);
        navigationBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlayRelativeLayout.setBackgroundResource(android.R.color.background_dark);
                finish();
            }
        });

        /* scan element */
        scanAnimationLinearLayout = activityAr.findViewById(R.id.scanAnimationLinearLayout);
        Button eventListButton = activityAr.findViewById(R.id.eventListButton);
        eventListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArActivity.this, WebViewActivity.class);
                intent.putExtra("URL", "https://www.youbohudong.com/biz/vip/kfc/calendar-2018/tasks");
                startActivity(intent);
            }
        });

        /* scan success view */
        scanSuccessRelativeLayout = activityAr.findViewById(R.id.scanSuccessRelativeLayout);
        scanSuccessProgressBar = activityAr.findViewById(R.id.scanSuccessProgressBar);
        scanSuccessImageView = activityAr.findViewById(R.id.scanSuccessImageView);
        scanSuccessDetailLinearLayout = activityAr.findViewById(R.id.scanSuccessDetailLinearLayout);
        scanSuccessTextView = activityAr.findViewById(R.id.scanSuccessTextView);
        scanSuccessGoButton = activityAr.findViewById(R.id.scanSuccessGoButton);
        scanSuccessGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scanSuccessGoButton.getText().toString().equals("知道了")) {
                    finish();
                } else {
                    Intent intent = new Intent(ArActivity.this, WebViewActivity.class);
                    intent.putExtra("URL", taskCompletionBean.getCompletionUrl());
                    intent.putExtra("calling-activity", ArActivity);
                    startActivity(intent);
                }
            }
        });

        initView();
        initListening();
        initData();
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

    private interface CameraPermissionCallback {
        void onSuccess();

        void onFailure();
    }

    private HashMap<Integer, CameraPermissionCallback> permissionCallbacks = new HashMap<Integer, CameraPermissionCallback>();
    private int permissionRequestCodeSerial = 0;

    @TargetApi(23)
    private void requestCameraPermission(CameraPermissionCallback callback) {
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
            CameraPermissionCallback callback = permissionCallbacks.get(requestCode);
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
        EventBus.getDefault().register(this);
        super.onResume();
        if (glView != null) {
            glView.onResume();
            showScanLayout();
        }
        overlayRelativeLayout.setBackgroundResource(android.R.color.transparent);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        overlayRelativeLayout.setBackgroundResource(android.R.color.black);
        if (glView != null) {
            glView.onPause();
        }
        super.onPause();
    }

    private static boolean isTrackEventFired = false;

    public void onEventMainThread(ArScanEvent event) {
        if (!isTrackEventFired) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.scan_success);
            mediaPlayer.start();
            isTrackEventFired = true;

            String taskKey = event.taskKey;
            UUID uuid = new DeviceUuidFactory(this).getDeviceUuid();
            requestTaskCompletion(uuid, taskKey);
        }
    }

    private void showScanLayout() {
        isTrackEventFired = false;
        glView.startTracker();
        scanAnimationLinearLayout.setVisibility(View.VISIBLE);
        scanSuccessRelativeLayout.setVisibility(View.GONE);
    }

    private void showScanSuccessLayout(Boolean isLoading) {
        glView.stopTracker();
        scanAnimationLinearLayout.setVisibility(View.GONE);
        scanSuccessRelativeLayout.setVisibility(View.VISIBLE);

        if (isLoading) {
            scanSuccessProgressBar.setVisibility(View.VISIBLE);
            scanSuccessDetailLinearLayout.setVisibility(View.GONE);
        } else {
            scanSuccessProgressBar.setVisibility(View.GONE);
            scanSuccessDetailLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 从服务器获取数据
     */
    private void requestTaskCompletion(UUID uuid, String taskKey) {
        showScanSuccessLayout(true);
        String url = "https://www.youbohudong.com/api/biz/vip/kfc/calendar-2018/tasks/" + uuid + "/" + taskKey;
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
            showScanLayout();
            e.printStackTrace();
        }

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            taskCompletionBean = gson.fromJson(response, TaskCompletionBean.class);


            if (!TextUtils.isEmpty(taskCompletionBean.getCompletionResource())) {
                scanSuccessImageView.setVisibility(View.VISIBLE);
                scanSuccessRelativeLayout.setVisibility(View.VISIBLE);

                Glide.with(ArActivity.this).load(taskCompletionBean.getCompletionResource())
                        .into(new ImageViewTarget<GlideDrawable>(scanSuccessImageView) {
                            @Override
                            protected void setResource(GlideDrawable glideDrawable) {
                                showScanSuccessLayout(false);
                                scanSuccessImageView.setImageDrawable(glideDrawable);
                            }
                        });
            } else {
                showScanSuccessLayout(false);
            }

            if (!TextUtils.isEmpty(taskCompletionBean.getCompletionUrl())) {
                scanSuccessTextView.setText(taskCompletionBean.getCompletionDescription());
                scanSuccessGoButton.setText("去看看");
            } else {
                scanSuccessGoButton.setText("知道了");
            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
        }
    }
}

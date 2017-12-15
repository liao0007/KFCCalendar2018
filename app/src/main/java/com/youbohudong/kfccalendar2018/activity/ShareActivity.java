package com.youbohudong.kfccalendar2018.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.EventLog;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.WeChatResponseEvent;
import com.youbohudong.kfccalendar2018.utils.WechatManager;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.ThreadMode;

public class ShareActivity extends BaseActivity {
    private LinearLayout shareDialogLinearLayout;
    private ImageView photoImageView;
    private Display display;

    private boolean isShareDialogVisible = false;
    private boolean isAnimatingDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = wm.getDefaultDisplay();

        shareDialogLinearLayout = findViewById(R.id.shareDialogLinearLayout);

        photoImageView = findViewById(R.id.photoImageView);
        photoImageView.setDrawingCacheEnabled(true);
        try {
            photoImageView.setImageBitmap(BitmapFactory.decodeStream(openFileInput("temp")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton retakeImageButton = findViewById(R.id.retakeImageButton);
        retakeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, CameraActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        ImageButton shareImageButton = findViewById(R.id.shareImageButton);
        shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleShareDialog();
            }
        });

        Button shareToSessionButton = findViewById(R.id.shareToSessionButton);
        shareToSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WechatManager.shareImage(ShareActivity.this, photoImageView.getDrawingCache(), SendMessageToWX.Req.WXSceneSession);
            }
        });

        Button shareToCircleButton = findViewById(R.id.shareToCircleButton);
        shareToCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WechatManager.shareImage(ShareActivity.this, photoImageView.getDrawingCache(), SendMessageToWX.Req.WXSceneTimeline);
            }
        });

        Button shareCancelButton = findViewById(R.id.shareCancelButton);
        shareCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleShareDialog();
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
    public void initListening() {
    }

    @Override
    public void initData() {
    }


    private void toggleShareDialog() {
        if (isAnimatingDialog) {
            return;
        }

        isAnimatingDialog = true;
        if (!isShareDialogVisible) {
            shareDialogLinearLayout.setY(display.getHeight());
            shareDialogLinearLayout.animate()
                    .translationYBy(-shareDialogLinearLayout.getHeight())
                    .setDuration(500)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isShareDialogVisible = true;
                            isAnimatingDialog = false;
                        }
                    });
        } else {
            shareDialogLinearLayout.setY(display.getHeight() - shareDialogLinearLayout.getHeight());
            shareDialogLinearLayout.animate()
                    .translationYBy(shareDialogLinearLayout.getHeight())
                    .setDuration(500)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isShareDialogVisible = false;
                            isAnimatingDialog = false;
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shareDialogLinearLayout.setY(display.getHeight());
        isShareDialogVisible = false;
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
                Toast.makeText(ShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_UNSUPPORT:
            default:
                Toast.makeText(ShareActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

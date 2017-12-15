package com.youbohudong.kfccalendar2018.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.utils.WechatManager;

public class ShareActivity extends BaseActivity {
    private LinearLayout shareDialogLinearLayout;
    private ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

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
            }
        });

        ImageButton shareImageButton = findViewById(R.id.shareImageButton);
        shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDialogLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        Button shareToSessionButton = findViewById(R.id.shareToSessionButton);
        shareToSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WechatManager.shareImage(ShareActivity.this, photoImageView.getDrawingCache(), SendMessageToWX.Req.WXSceneSession);
                shareDialogLinearLayout.setVisibility(View.GONE);
            }
        });

        Button shareToCircleButton = findViewById(R.id.shareToCircleButton);
        shareToCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WechatManager.shareImage(ShareActivity.this, photoImageView.getDrawingCache(), SendMessageToWX.Req.WXSceneTimeline);
                shareDialogLinearLayout.setVisibility(View.GONE);
            }
        });

        Button shareCancelButton = findViewById(R.id.shareCancelButton);
        shareCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDialogLinearLayout.setVisibility(View.GONE);
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

}

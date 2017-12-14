package com.youbohudong.kfccalendar2018.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.utils.WechatManager;

import java.io.File;

/**
 * Created by ${bcq} on 2017/11/21.
 */

public class WeChatEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private ImageView img_bg, img_savetip, img_sharetip, img_againtip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_layout);

        img_bg = findViewById(R.id.img_bg);
        img_savetip = findViewById(R.id.img_savetip);
        img_sharetip = findViewById(R.id.img_sharetip);
        img_againtip = findViewById(R.id.img_againtip);

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
        WechatManager.createWXAPI(this).handleIntent(getIntent(), this);

        // 获取内置SD卡路径
        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
        // 图片文件路径
        String filePath = sdCardPath + File.separator + "screenshot.png";
        Bitmap bmp = WechatManager.createThumbnailFromFile(filePath);
        img_bg.setImageBitmap(bmp);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }


    /***
     * 请求微信的相应码
     * @author YOLANDA
     */
    @Override
    public void onResp(BaseResp baseResp) {
        String result = null;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK: {
                result = "分享成功";
            }
            break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "分享取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                break;
            default:
                result = "分享返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                break;
        }

        Toast toast = new Toast(this);
        View v = LayoutInflater.from(this).inflate(R.layout.share_layout, null);
        toast.setView(v);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        this.finish();
    }

    /**
     * 微信主动请求我们
     **/
    @Override
    public void onReq(BaseReq baseResp) {
    }

}
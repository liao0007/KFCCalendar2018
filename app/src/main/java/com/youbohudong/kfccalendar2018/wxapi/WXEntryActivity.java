package com.youbohudong.kfccalendar2018.wxapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;

import java.io.File;

/**
 * Created by ${bcq} on 2017/11/21.
 */

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;
    private ImageView img_bg,img_savetip,img_sharetip,img_againtip;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_layout);
        initView();
        initListening();
        initData();
    }
    @Override
    public void initView() {
        img_bg= (ImageView) findViewById(R.id.img_bg);
         img_savetip= (ImageView) findViewById(R.id.img_savetip);
         img_sharetip= (ImageView) findViewById(R.id.img_sharetip);
        img_againtip= (ImageView) findViewById(R.id.img_againtip);
    }

    @Override
    public void initListening() {

    }
    @Override
    public void initData() {
        api =WXAPIFactory.createWXAPI(this, "wxb0c1974e2394893d");
        api.handleIntent(getIntent(), this);
        // 获取内置SD卡路径
        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
        // 图片文件路径
       String filePath = sdCardPath + File.separator + "screenshot.png";
       Bitmap bmp= getimage(filePath);
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

        Toast toast=new Toast(this);
        View v= LayoutInflater.from(this).inflate(R.layout.share_layout,null);
        toast.setView(v);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        this.finish();
    }

    /**微信主动请求我们**/
    @Override
    public void onReq(BaseReq baseResp) {
    }


    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap getimage(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1920f;// 这里设置高度为800f
        float ww = 1080f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return  bitmap;
//        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

}
package com.youbohudong.kfccalendar2018.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.adapter.LeftAdapter;
import com.youbohudong.kfccalendar2018.adapter.RightAdapter;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.youbohudong.kfccalendar2018.bean.RightBean;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import com.youbohudong.kfccalendar2018.utils.ToastUtils;
import com.youbohudong.kfccalendar2018.utils.Util;
import com.youbohudong.kfccalendar2018.view.SingleTouchView;
import com.youbohudong.kfccalendar2018.view.StampDownloadProgress;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StampActivity extends BaseActivity implements View.OnClickListener, RightAdapter.UpdateItemListening, SingleTouchView.DeleteUIListening {
    private static List<LeftBean> stampGroupListData;

    private static final int THUMB_SIZE = 150;

    private ImageButton showStampGroupImageButton, returnImageButton, saveImageButton;
    private RelativeLayout rl_root;
    private FrameLayout fl_root;
    private ImageView img_pic;
    private DrawerLayout drawer_layout;
    private List<LeftBean.StampsBean> rightList;
    private ListView lv_left, lv_right;
    private LinearLayout ll_close;

    private ImageView img_savetip;
    private LeftAdapter leftAdapter;
    private RightAdapter rightAdapter;

    Display display;

    int bmpHeight, bmpWidth;
    private int currentLeftPos;
    private SharedPreferencesUtils spUtils;

    private ToastUtils toastUtils;
    private int imgTag = 0;
    private LinearLayout llReturn_Save;
    Uri mImageCaptureUri;
    int sHight, sWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display dy = wm.getDefaultDisplay();
        sHight = dy.getHeight();
        sWidth = dy.getWidth();
        String url = getIntent().getStringExtra("URI");
        if (!TextUtils.isEmpty(url)) {
            mImageCaptureUri = Uri.parse(url);
        }
        spUtils = new SharedPreferencesUtils(this);
        toastUtils = new ToastUtils(StampActivity.this);
        initView();
        initListening();
        initData();
    }

    @Override
    public void initView() {
        drawer_layout = findViewById(R.id.contentDrawerLayout);

        showStampGroupImageButton = findViewById(R.id.showStampGroupImageButton);
        returnImageButton = findViewById(R.id.returnImageButton);
        saveImageButton = findViewById(R.id.saveImageButton);
        img_savetip = findViewById(R.id.saveSuccessImageView);
        llReturn_Save = (LinearLayout) findViewById(R.id.llReturn_Save);
        rl_root = findViewById(R.id.rl_root);
        fl_root = findViewById(R.id.fl_root);
        img_pic = findViewById(R.id.img_pic);
        lv_left = findViewById(R.id.stampGroupListView);
        lv_right = findViewById(R.id.lv_right);
        ll_close = findViewById(R.id.stampGroupControllerCloseLinearLayout);

    }

    @Override
    public void initListening() {
        showStampGroupImageButton.setOnClickListener(this);
        saveImageButton.setOnClickListener(this);
        returnImageButton.setOnClickListener(this);

        lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentLeftPos = i;// 记录当前左面的位置
                if (i == stampGroupListData.size() - 1) {
                    toastUtils.show(StampActivity.this, "不定期推出新贴纸，请关注App通知和线下活动！");
                } else {
                    LeftBean stampGroupData = stampGroupListData.get(i);
                    if (!stampGroupData.isIsAvailable() && stampGroupData.getNote().length() > 0) {
                        toastUtils.show(StampActivity.this, stampGroupData.getNote());
                    }
                }

                if (stampGroupListData.get(i).getStamps() != null) {
                    rightAdapter = new RightAdapter(StampActivity.this, stampGroupListData.get(i).getStamps(), i, stampGroupListData.get(i).isIsAvailable());
                    rightAdapter.setmUpdateItemListening(StampActivity.this);
                    lv_right.setAdapter(rightAdapter);
                    leftAdapter.setPos(i);
                    leftAdapter.notifyDataSetChanged();
                }
            }
        });
//        lv_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(stampGroupListData!=null&&stampGroupListData.size()>0){
//                  LeftBean leftBean= stampGroupListData.get(currentLeftPos);
//                    List<LeftBean.StampsBean> list=leftBean.getStamps();
//                    if(list!=null&&list.size()>0){
//                        LeftBean.StampsBean stampsBean= list.get(i);
//                        if(leftBean.isIsAvailable()){
//                            boolean isDown = new SharedPreferencesUtils(StampActivity.this).getBoolean(stampsBean.getImage(), false);
//                            if(isDown){
//
//                            }else{
//
//                            }
//                        }
//                        if(!TextUtils.isEmpty(stampsBean.getNote())){
//                            new ToastUtils(StampActivity.this).show(StampActivity.this, stampsBean.getNote());
//                        }
//                    }
//                }
//            }
//        });

        fl_root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) motionEvent.getX();
                        startY = (int) motionEvent.getY();
                        isShowSaveAndReturn(startX, startY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void initData() {
        rightList = new ArrayList<>();
        try {
            Bitmap photoBmp = null;
            if (mImageCaptureUri != null) {
                photoBmp = decodeUri(this, mImageCaptureUri, sWidth, sHight);
                bmpHeight = photoBmp.getHeight();
                bmpWidth = photoBmp.getWidth();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sWidth, sHight);
                img_pic.setLayoutParams(params);

            } else {
                photoBmp = BitmapFactory.decodeStream(openFileInput("temp"));
            }
            img_pic.setImageBitmap(photoBmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (stampGroupListData == null) {
            requestStampGroupData();
        } else {
            leftAdapter = new LeftAdapter(StampActivity.this, stampGroupListData);
            lv_left.setAdapter(leftAdapter);
            rightAdapter = new RightAdapter(StampActivity.this, stampGroupListData.get(0).getStamps(), 0, stampGroupListData.get(0).isIsAvailable());
            rightAdapter.setmUpdateItemListening(StampActivity.this);
            lv_right.setAdapter(rightAdapter);
        }

    }

    /**
     * 从服务器获取数据
     */
    private void requestStampGroupData() {
        String url = "https://www.youbohudong.com/api/biz/vip/kfc/calendar-2018/stamps";
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new StampGroupDataCallback());
    }


    public class StampGroupDataCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id) {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            // json转为带泛型的list
            List<LeftBean> listData = gson.fromJson(response, new TypeToken<List<LeftBean>>() {
            }.getType());

            stampGroupListData = listData;

            LeftBean bean = new LeftBean();
            bean.setName("敬请期待");
            stampGroupListData.add(bean);

            leftAdapter = new LeftAdapter(StampActivity.this, stampGroupListData);
            lv_left.setAdapter(leftAdapter);

            leftAdapter.notifyDataSetChanged();
            rightAdapter = new RightAdapter(StampActivity.this, stampGroupListData.get(0).getStamps(), 0, stampGroupListData.get(0).isIsAvailable());
            rightAdapter.setmUpdateItemListening(StampActivity.this);
            lv_right.setAdapter(rightAdapter);
        }

        @Override
        public void inProgress(float progress, long total, int id) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveImageButton://保存
                drawer_layout.closeDrawer(Gravity.RIGHT);
                try {
                    Thread.sleep(600);
                } catch (Exception e) {
                }
                showStampGroupImageButton.setVisibility(View.GONE);
                returnImageButton.setVisibility(View.GONE);
                saveImageButton.setVisibility(View.GONE);
                updateView();
                takeScreenShot();
                break;
            case R.id.showStampGroupImageButton://贴纸
                drawer_layout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.returnImageButton:
                startActivity(new Intent(StampActivity.this, CameraActivity.class));
                finish();
                break;

            case R.id.stampGroupControllerCloseLinearLayout://关闭侧滑
                drawer_layout.closeDrawer(Gravity.RIGHT);
                break;

        }
    }


    public void updateView() {
        int count = fl_root.getChildCount();
        for (int i = 0; i < count; i++) {
            SingleTouchView touchView = (SingleTouchView) fl_root.getChildAt(i);
            touchView.setEditable(false);
        }
    }

    /**
     * 设置可编辑view
     *
     * @param view
     */
    public void setEditView(SingleTouchView view) {
        int count = fl_root.getChildCount();
        for (int i = 0; i < count; i++) {
            SingleTouchView touchView = (SingleTouchView) fl_root.getChildAt(i);
            if (touchView.getTag() == view.getTag()) {
                touchView.setEditable(true);
            } else {
                touchView.setEditable(false);
            }

        }
        llReturn_Save.setVisibility(View.GONE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    public static final Bitmap getBitmap(ContentResolver cr, Uri url)
            throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }


    private static final String savePath = Environment.getExternalStorageDirectory() + "/downLoadImg/";
    int progress;

    public void downloadImg(final String imgUrl, final String fileName, int parentIndex, int pos, final StampDownloadProgress v, final TextView view) {
        OkHttpUtils.get().url(imgUrl).build().execute(new FileCallBack(savePath, fileName) {

            @Override
            public void onBefore(Request request, int id) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                v.setProgress((int) progress * 100);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                v.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(File file, int id) {
                spUtils.setBoolean(imgUrl, true);
                view.setVisibility(View.GONE);
                rightAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDeleteUI(View v) {
        fl_root.removeView(v);
    }

    @Override
    public void onItemClick(int parentIndex, int pos, String fileName) {
        updateView();
        llReturn_Save.setVisibility(View.GONE);
        imgTag++;
        SingleTouchView singleTouchView = new SingleTouchView(StampActivity.this);
        singleTouchView.setmDeleteUIListening(StampActivity.this);
        singleTouchView.setImageScale(0.5);
        singleTouchView.setControlLocation(SingleTouchView.RIGHT_BOTTOM);
        singleTouchView.setControlDelLocation(SingleTouchView.LEFT_TOP);

        Bitmap bmp = BitmapFactory.decodeFile(savePath + fileName);
        singleTouchView.setImageResource(bmp);
        singleTouchView.bringToFront();
        singleTouchView.setTag(imgTag);
        fl_root.addView(singleTouchView);
        drawer_layout.closeDrawer(Gravity.RIGHT);
        singleTouchView.setmEditViewListening(new SingleTouchView.EditViewListening() {
            @Override
            public void editView(View v) {
                setEditView((SingleTouchView) v);
            }
        });

    }

    @Override
    public void onDownloadItem(int parentIndex, int pos, StampDownloadProgress v, TextView view) {
        String url = stampGroupListData.get(parentIndex).getStamps().get(pos).getImage();
        downloadImg(url, url.substring(url.lastIndexOf("/")), parentIndex, pos, v, view);
    }

    /**
     * 截屏
     */
    Bitmap screenBitmap;

    public void takeScreenShot() {
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmapScreen = dView.getDrawingCache();
        img_pic.setVisibility(View.GONE);
        if (bmpWidth > 0 && bmpHeight > 0 && bmpHeight < sHight * 2 / 3) {
            screenBitmap = Bitmap.createBitmap(bitmapScreen, 0, (sHight - bmpHeight) / 2, sWidth, bmpHeight + 50);
            dView.destroyDrawingCache();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sWidth, bmpHeight);
            fl_root.removeAllViews();
        } else {
            screenBitmap = Bitmap.createBitmap(bitmapScreen);
            dView.destroyDrawingCache();
            fl_root.removeAllViews();
        }
        if (screenBitmap != null) {
            try {
//                // 获取内置SD卡路径
//                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
//                // 图片文件路径
//                imagePath = sdCardPath + File.separator + "screenshot.png";
//                File file = new File(imagePath);
//                FileOutputStream os = new FileOutputStream(file);
//                screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//                os.flush();
//                os.close();
//                img_savetip.setVisibility(View.VISIBLE);
//                Message msg = new Message();
//                msg.what = SAVE_PIC;
//                img_savetip.bringToFront();
//                mHandler.sendMessageDelayed(msg, 2000);

                  /* compress */
                screenBitmap = Util.compressImage(screenBitmap);

        /* rotate */
                Matrix matrix = new Matrix();
                matrix.reset();
                screenBitmap = Bitmap.createBitmap(screenBitmap, 0, 0, screenBitmap.getWidth(), screenBitmap.getHeight(), matrix, true);

        /* save to file */
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    screenBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    FileOutputStream fileOutputStream = openFileOutput("temp", Context.MODE_PRIVATE);
                    fileOutputStream.write(bytes.toByteArray());
                    fileOutputStream.close();
                    startActivity(new Intent(StampActivity.this, ShareActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
            }
        }
    }


    public void onEventMainThread(RightBean event) {
    }


//    @Override
//    public void onLeftItem(int pos) {
//        if (stampGroupListData.get(pos).getStamps() != null) {
//            rightAdapter = new RightAdapter(CustomerCameraActivity.this, stampGroupListData.get(pos).getStamps(),pos,stampGroupListData.get(pos).isIsAvailable());
//            rightAdapter.setmUpdateItemListening(CustomerCameraActivity.this);
//            lv_right.setAdapter(rightAdapter);
//            leftAdapter.setPos(pos);
//            leftAdapter.notifyDataSetChanged();
//
//
//        }
//    }

    public void screenBmp(View layout) {
        int width = layout.getWidth();
        int height = layout.getHeight();

        //打开图像缓存
        layout.setDrawingCacheEnabled(true);
        //测量Linearlayout的大小
        layout.measure(0, 0);
        width = layout.getMeasuredWidth();
        height = layout.getMeasuredHeight();
        //发送位置和尺寸到LienarLayout及其所有的子View
        //简单地说，就是我们截取的屏幕区域，注意是以Linearlayout左上角为基准的，而不是屏幕左上角
        layout.layout(0, 0, width, height);
        //拿到截取图像的bitmap
        Bitmap bitmap = layout.getDrawingCache();
        rl_root.setVisibility(View.GONE);
    }

    int startX, startY;

    private Rect mChangeImageBackgroundRect = null;

    private boolean isInChangeImageZone(View view, int x, int y) {
        if (null == mChangeImageBackgroundRect) {
            mChangeImageBackgroundRect = new Rect();
        }
        view.getDrawingRect(mChangeImageBackgroundRect);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mChangeImageBackgroundRect.left = location[0];
        mChangeImageBackgroundRect.top = location[1];
        mChangeImageBackgroundRect.right = mChangeImageBackgroundRect.right + location[0];
        mChangeImageBackgroundRect.bottom = mChangeImageBackgroundRect.bottom + location[1];
        return mChangeImageBackgroundRect.contains(x, y);
    }

    public void isShowSaveAndReturn(int x, int y) {
        int count = fl_root.getChildCount();
        for (int i = 0; i < count; i++) {
            SingleTouchView touchView = (SingleTouchView) fl_root.getChildAt(i);
            if (isInChangeImageZone(touchView, x, y)) {
                Toast.makeText(this, "在里面", Toast.LENGTH_SHORT).show();
            } else {
                updateView();
                llReturn_Save.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 读取一个缩放后的图片，限定图片大小，避免OOM
     *
     * @param uri       图片uri，支持“file://”、“content://”
     * @param maxWidth  最大允许宽度
     * @param maxHeight 最大允许高度
     * @return 返回一个缩放后的Bitmap，失败则返回null
     */
    public static Bitmap decodeUri(Context context, Uri uri, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只读取图片尺寸
        resolveUri(context, uri, options);

        //计算实际缩放比例
        int scale = 1;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if ((options.outWidth / scale > maxWidth &&
                    options.outWidth / scale > maxWidth * 1.4) ||
                    (options.outHeight / scale > maxHeight &&
                            options.outHeight / scale > maxHeight * 1.4)) {
                scale++;
            } else {
                break;
            }
        }

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;//读取图片内容
        options.inPreferredConfig = Bitmap.Config.RGB_565; //根据情况进行修改
        Bitmap bitmap = null;
        try {
            bitmap = resolveUriForBitmap(context, uri, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    private static void resolveUri(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return;
        }

        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUri", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUri", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUri", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUri", "Unable to close content: " + uri);
        }
    }

    private static Bitmap resolveUriForBitmap(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return null;
        }

        Bitmap bitmap = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUriForBitmap", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUriForBitmap", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        }

        return bitmap;
    }


}

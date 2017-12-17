package com.youbohudong.kfccalendar2018.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.youbohudong.kfccalendar2018.utils.CapturePhotoUtils;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import com.youbohudong.kfccalendar2018.utils.ToastUtils;
import com.youbohudong.kfccalendar2018.view.SingleTouchView;
import com.youbohudong.kfccalendar2018.view.StampDownloadProgress;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;

import java.io.*;
import java.util.List;

public class StampActivity extends BaseActivity implements View.OnClickListener, RightAdapter.UpdateItemListening, SingleTouchView.DeleteUIListening {
    private static List<LeftBean> stampGroupListData;

    private DisplayMetrics displayMetrics;

    private ImageButton showStampGroupImageButton, returnImageButton, saveImageButton;
    private FrameLayout fl_root;
    private ImageView photoImageView;
    private DrawerLayout drawer_layout;
    private ListView lv_left, lv_right;

    private LeftAdapter leftAdapter;
    private RightAdapter rightAdapter;

    private int photoHeight, photoWidth;
    private SharedPreferencesUtils spUtils;

    private ToastUtils toastUtils;
    private int imgTag = 0;
    private LinearLayout llReturn_Save;
    private Uri mImageCaptureUri;
//    private int sHeight, sWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

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
        llReturn_Save = findViewById(R.id.llReturn_Save);
        fl_root = findViewById(R.id.fl_root);
        photoImageView = findViewById(R.id.stampPhotoImageView);
        photoImageView.setDrawingCacheEnabled(true);
        lv_left = findViewById(R.id.stampGroupListView);
        lv_right = findViewById(R.id.lv_right);
    }

    @Override
    public void initListening() {
        showStampGroupImageButton.setOnClickListener(this);
        saveImageButton.setOnClickListener(this);
        returnImageButton.setOnClickListener(this);

        lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
        try {
            Bitmap photoBmp;
            if (mImageCaptureUri != null) {
                photoBmp = decodeUri(this, mImageCaptureUri);
            } else {
                photoBmp = BitmapFactory.decodeStream(openFileInput("temp"));
            }
            photoHeight = photoBmp.getHeight();
            photoWidth = photoBmp.getWidth();
            photoImageView.setImageBitmap(photoBmp);
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

            stampGroupListData = gson.fromJson(response, new TypeToken<List<LeftBean>>() {
            }.getType());

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
                drawer_layout.closeDrawer(Gravity.END);
                try {
                    Thread.sleep(600);
                } catch (Exception ignored) {
                }
                showStampGroupImageButton.setVisibility(View.GONE);
                returnImageButton.setVisibility(View.GONE);
                saveImageButton.setVisibility(View.GONE);
                updateView();
                takeScreenShot();
                break;
            case R.id.showStampGroupImageButton://贴纸
                drawer_layout.openDrawer(Gravity.END);
                break;
            case R.id.returnImageButton:
                finish();
                break;

            case R.id.stampGroupControllerCloseLinearLayout://关闭侧滑
                drawer_layout.closeDrawer(Gravity.END);
                break;

        }
    }


    private void updateView() {
        int count = fl_root.getChildCount();
        for (int i = 0; i < count; i++) {
            SingleTouchView touchView = (SingleTouchView) fl_root.getChildAt(i);
            touchView.setEditable(false);
        }
    }


    private void setEditView(SingleTouchView view) {
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


    private static final String savePath = Environment.getExternalStorageDirectory() + "/kc2018/";

    private void downloadImg(final String imgUrl, final String fileName, final StampDownloadProgress v, final TextView view) {
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
        drawer_layout.closeDrawer(Gravity.END);
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
        downloadImg(url, url.substring(url.lastIndexOf("/")), v, view);
    }

    private void takeScreenShot() {
        View decorView = getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bitmapScreen = decorView.getDrawingCache();

        int x, y, width, height;
        double photoRatio = photoHeight * 1.0 / photoWidth;
        double screenRatio = displayMetrics.heightPixels * 1.0 / displayMetrics.widthPixels;
        if (photoRatio < screenRatio) {
            //横屏 imageWith = screenWidth
            width = displayMetrics.widthPixels;
            height = photoHeight * width / photoWidth;
            x = 0;
            y = (displayMetrics.heightPixels - height) / 2;
        } else {
            //竖屏 imageHeight = screenHeight
            height = displayMetrics.heightPixels;
            width = photoWidth * height / photoHeight;
            y = 0;
            x = (displayMetrics.widthPixels - width) / 2;
        }

        Bitmap screenBitmap;
        screenBitmap = Bitmap.createBitmap(bitmapScreen, x, y, width, height);
        decorView.destroyDrawingCache();
        fl_root.removeAllViews();

        if (screenBitmap != null) {
            try {
        /* rotate */
                Matrix matrix = new Matrix();
                matrix.reset();
                screenBitmap = Bitmap.createBitmap(screenBitmap, 0, 0, screenBitmap.getWidth(), screenBitmap.getHeight(), matrix, true);


                // insert into photo gallery
                CapturePhotoUtils.insertImage(getContentResolver(), screenBitmap, "K记大玩家", "");

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

            } catch (Exception ignored) {
            }
        }
    }

    private int startX, startY;

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

    private void isShowSaveAndReturn(int x, int y) {
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


    private static Bitmap decodeUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = resolveUriForBitmap(context, uri);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap resolveUriForBitmap(Context context, Uri uri) {
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
                bitmap = BitmapFactory.decodeStream(stream);
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

package com.youbohudong.kfccalendar2018.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.adapter.LeftAdapter;
import com.youbohudong.kfccalendar2018.adapter.RightAdapter;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.youbohudong.kfccalendar2018.bean.RightBean;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import com.youbohudong.kfccalendar2018.utils.ToastUtils;
import com.youbohudong.kfccalendar2018.utils.WechatManager;
import com.youbohudong.kfccalendar2018.view.SingleTouchView;
import com.youbohudong.kfccalendar2018.view.SlefProgress;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerCameraActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener, RightAdapter.UpdateItemListening, SingleTouchView.DeleteUIListening {
    private static List<LeftBean> stampGroupListData;

    private static final int THUMB_SIZE = 150;
    //声明一个camera对象
    private Camera camera;
    //图片的实时预览
    private SurfaceView suf_camera;
    private SurfaceHolder surfaceHolder;
    //相机参数设置
    private Camera.Parameters parameters;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private ImageView img_start, img_switch, img_xz;
    private SurfaceHolder mHolder;
    private RelativeLayout rl_finish, rl_root;
    private FrameLayout fl_root;
    private ImageView img_pic;
    private DrawerLayout drawer_layout;
    private List<LeftBean> leftList;
    private List<LeftBean.StampsBean> rightList;
    private ListView lv_left, lv_right;
    private LinearLayout ll_share, ll_close, ll_save_pic;
    private TextView txt_frends, txt_cancel, txt_frendsquare;
    private ImageButton helpButton;

    private static final int REQUEST_XC_CODE = 101;
    private static final int DOWNLOADING = 102;
    private static final int DOWNLOADED = 103;
    private static final int DOWNLOAD_FAILED = 104;
    private static final int SAVE_PIC = 105;
    private ContentResolver resolver;
    private ImageView img_restart, img_save, img_paster, img_share, img_save_pic, img_ar;
    private ImageView img_again, img_savetip;
    private LeftAdapter leftAdapter;
    private RightAdapter rightAdapter;

    Display display;

    int bmpHeight, bmpWidth;

    private String isCheck = "0";
    private static String imagePath = "";
    private SharedPreferencesUtils spUtils;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOADING:
                    break;
                case DOWNLOADED:
                    break;
                case DOWNLOAD_FAILED:
                    break;
                case SAVE_PIC:
                    img_savetip.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_camera);
        spUtils = new SharedPreferencesUtils(this);
        spUtils.setBoolean("is_first", false);
        EventBus.getDefault().register(this);
        initView();
        initListening();
        initData();

    }

    @Override
    public void initView() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        resolver = getContentResolver();
        drawer_layout = findViewById(R.id.drawer_layout);
        img_start = findViewById(R.id.img_start);
        img_switch = findViewById(R.id.img_switch);
        img_xz = findViewById(R.id.img_xz);
        suf_camera = findViewById(R.id.suf_camera);
        rl_finish = findViewById(R.id.rl_finish);
        rl_root = findViewById(R.id.rl_root);
        fl_root = findViewById(R.id.fl_root);
        img_pic = findViewById(R.id.img_pic);
        lv_left = findViewById(R.id.lv_left);
        lv_right = findViewById(R.id.lv_right);
        img_restart = findViewById(R.id.img_restart);
        img_save = findViewById(R.id.img_save);
        img_paster = findViewById(R.id.img_paster);
        img_share = findViewById(R.id.img_share);
        img_again = findViewById(R.id.img_again);
        img_savetip = findViewById(R.id.img_savetip);
        img_save_pic = findViewById(R.id.img_save_pic);
        img_ar = findViewById(R.id.img_ar);

        ll_close = findViewById(R.id.ll_close);
        ll_share = findViewById(R.id.ll_share);

        txt_frends = findViewById(R.id.txt_frends);
        txt_frendsquare = findViewById(R.id.txt_friendsquare);
        txt_cancel = findViewById(R.id.txt_cancel);

        ll_save_pic = findViewById(R.id.ll_save_pic);

        helpButton = findViewById(R.id.helpButton);

        surfaceHolder = suf_camera.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void initListening() {
        img_start.setOnClickListener(this);
        img_switch.setOnClickListener(this);
        img_xz.setOnClickListener(this);
        img_restart.setOnClickListener(this);
        img_save.setOnClickListener(this);
        img_paster.setOnClickListener(this);
        img_share.setOnClickListener(this);
        img_again.setOnClickListener(this);
        txt_frends.setOnClickListener(this);
        txt_frendsquare.setOnClickListener(this);
        txt_cancel.setOnClickListener(this);
        ll_close.setOnClickListener(this);
        img_ar.setOnClickListener(this);
        lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new ToastUtils(CustomerCameraActivity.this).show(CustomerCameraActivity.this, leftList.get(i).getNote());
                if (leftList.get(i).getStamps() != null) {
                    rightAdapter = new RightAdapter(CustomerCameraActivity.this, leftList.get(i).getStamps(), i, leftList.get(i).isIsAvailable());
                    rightAdapter.setmUpdateItemListening(CustomerCameraActivity.this);
                    lv_right.setAdapter(rightAdapter);
                    leftAdapter.setPos(i);
                    leftAdapter.notifyDataSetChanged();
                }
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerCameraActivity.this, GuideActivity.class));
                CustomerCameraActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    @Override
    public void initData() {
        leftList = new ArrayList<>();
        rightList = new ArrayList<>();
        leftAdapter = new LeftAdapter(CustomerCameraActivity.this, leftList);
//        leftAdapter.setmOnLeftClickListening(this);
        lv_left.setAdapter(leftAdapter);

        if (stampGroupListData == null) {
            requestStampGroupData();
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
            stampGroupListData = gson.fromJson(response, new TypeToken<List<LeftBean>>() {}.getType());
            leftList.clear();
            leftList.addAll(stampGroupListData);
            LeftBean bean = new LeftBean();
            bean.setName("敬请期待");
            leftList.add(bean);
            leftAdapter.notifyDataSetChanged();
            rightAdapter = new RightAdapter(CustomerCameraActivity.this, leftList.get(0).getStamps(), 0, leftList.get(0).isIsAvailable());
            rightAdapter.setmUpdateItemListening(CustomerCameraActivity.this);
            lv_right.setAdapter(rightAdapter);
        }

        @Override
        public void inProgress(float progress, long total, int id) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_start://拍照
                takePhoto();
                break;
            case R.id.img_switch://切换
                change(view);
                break;
            case R.id.img_xz://相册
                // 激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent, REQUEST_XC_CODE);
                break;
            case R.id.img_restart://重拍
                bmpWidth = 0;
                bmpHeight = 0;
                fl_root.removeAllViews();
                rl_finish.setVisibility(View.GONE);
                rl_root.setVisibility(View.GONE);
                helpButton.setVisibility(View.VISIBLE);
                getCamera();
                if (surfaceHolder != null) {
                    setPrive(camera, surfaceHolder);
                }

                break;
            case R.id.img_save://保存
                updateView();
                img_save.setVisibility(View.GONE);
                img_restart.setVisibility(View.GONE);
                img_paster.setVisibility(View.GONE);
                takeScreenShot();

                break;
            case R.id.img_share://分享
                ll_share.setVisibility(View.VISIBLE);

                break;
            case R.id.img_paster://贴纸
                drawer_layout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.img_again://再来一张
                bmpWidth = 0;
                bmpHeight = 0;
                fl_root.removeAllViews();
                rl_finish.setVisibility(View.GONE);
                img_paster.setVisibility(View.VISIBLE);
                img_share.setVisibility(View.GONE);
                img_restart.setVisibility(View.VISIBLE);
                img_save.setVisibility(View.VISIBLE);
                img_again.setVisibility(View.GONE);
                helpButton.setVisibility(View.VISIBLE);

                rl_root.setVisibility(View.GONE);
                getCamera();
                if (surfaceHolder != null) {
                    setPrive(camera, surfaceHolder);
                }
                break;
            case R.id.txt_frends://回话
                WechatManager.shareImage(this, imagePath, SendMessageToWX.Req.WXSceneSession);
                ll_share.setVisibility(View.GONE);
                break;
            case R.id.txt_friendsquare://朋友圈
                WechatManager.shareImage(this, imagePath, SendMessageToWX.Req.WXSceneTimeline);
                ll_share.setVisibility(View.GONE);
                break;
            case R.id.txt_cancel://取消
                ll_share.setVisibility(View.GONE);
                break;
            case R.id.ll_close://关闭侧滑
                drawer_layout.closeDrawer(Gravity.RIGHT);
                break;
            case R.id.img_ar://ar扫描
                startActivity(new Intent(this, ArActivity.class));
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
     * 设置照片格式
     */
    private void setParameter() {
        parameters = camera.getParameters(); // 获取各项参数
        parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        parameters.setJpegQuality(100); // 设置照片质量
        //获得相机支持的照片尺寸,选择合适的尺寸
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        int maxSize = Math.max(display.getWidth(), display.getHeight());
        int length = sizes.size();
        if (maxSize > 0) {
            for (int i = 0; i < length; i++) {
                if (maxSize <= Math.max(sizes.get(i).width, sizes.get(i).height)) {
                    parameters.setPictureSize(sizes.get(i).width, sizes.get(i).height);
                    break;
                }
            }
        }
        List<Camera.Size> ShowSizes = parameters.getSupportedPreviewSizes();
        int showLength = ShowSizes.size();
        if (maxSize > 0) {
            for (int i = 0; i < showLength; i++) {
                if (maxSize <= Math.max(ShowSizes.get(i).width, ShowSizes.get(i).height)) {
                    parameters.setPreviewSize(ShowSizes.get(i).width, ShowSizes.get(i).height);
                    break;
                }
            }
        }
        camera.setParameters(parameters);
    }

    public void takePhoto() {
        setParameter();
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bMap;
                        try {// 获得图片
                            bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            bMap = compressImage(bMap);
                            Bitmap bMapRotate;
                            Matrix matrix = new Matrix();
                            matrix.reset();
                            if (cameraPosition == 1) {
                                matrix.postRotate(90);
                            } else {
                                matrix.postRotate(270);
                                matrix.postScale(-1, 1);
                            }

                            bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
                                    bMap.getHeight(), matrix, true);
                            bMap = bMapRotate;

                            // Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "temp.jpeg");
                            BufferedOutputStream bos =
                                    new BufferedOutputStream(new FileOutputStream(file));
                            bMap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                            bos.flush();//输出
                            bos.close();//关闭

                            ll_save_pic.setVisibility(View.GONE);
                            rl_finish.setVisibility(View.VISIBLE);
                            rl_root.setVisibility(View.VISIBLE);
                            img_pic.setVisibility(View.VISIBLE);
                            helpButton.setVisibility(View.GONE);
                            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpeg");
                            //获取水印图片，如果图片过大，应对图片采样
//                            Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.splash);
//                            Bitmap bitmap2 = watermarkBitmap(bitmap, null, "ZHOUKAI");
                            img_pic.setImageBitmap(bitmap);
                            drawer_layout.openDrawer(Gravity.RIGHT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    public void getCamera() {
        //获取相机实例
        if (camera == null)
            camera = Camera.open();
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void setPrive(Camera camera, SurfaceHolder surfaceHolder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            //开始预览
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        getCamera();
        if (surfaceHolder != null) {
            setPrive(camera, surfaceHolder);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setPrive(camera, surfaceHolder);
        mHolder = holder;// SurfaceHolder是系统提供用来设置surfaceView的对象
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.stopPreview();
        setPrive(camera, surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();

    }

    public void change(View v) {
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(mHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    camera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(mHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    camera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_XC_CODE) {
            if (resultCode == RESULT_OK) {
                // 从相册返回的数据
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    Bitmap photoBmp = null;
                    if (uri != null) {
                        try {
                            photoBmp = MediaStore.Images.Media.getBitmap(resolver, uri);
                            rl_finish.setVisibility(View.VISIBLE);
                            rl_root.setVisibility(View.VISIBLE);
                            ll_save_pic.setVisibility(View.GONE);
                            img_pic.setVisibility(View.VISIBLE);
                            bmpHeight = photoBmp.getHeight();
                            bmpWidth = photoBmp.getWidth();
//                            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(width,height);
//                            img_pic.setLayoutParams(params);
                            img_pic.setImageBitmap(photoBmp);
                            drawer_layout.openDrawer(Gravity.RIGHT);
                        } catch (Exception e) {
                        }

                    }
                }
            }
        }
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

    public void downloadImg(final String imgUrl, final String fileName, int parentIndex, int pos, final SlefProgress v, final TextView view) {
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
            }

            @Override
            public void onResponse(File file, int id) {
                spUtils.setBoolean(imgUrl, true);
                view.setVisibility(View.GONE);
                rightAdapter.notifyDataSetChanged();
//                        Toast.makeText(CustomerCameraActivity.this, "onResponse :" + file.getAbsolutePath(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDeleteUI(View v) {
        rl_root.removeView(v);
    }

    @Override
    public void onItemClick(int parentIndex, int pos, String fileName) {

        SingleTouchView singleTouchView = new SingleTouchView(CustomerCameraActivity.this);
        singleTouchView.setmDeleteUIListening(CustomerCameraActivity.this);
        singleTouchView.setImageScale(1);
        singleTouchView.setControlLocation(SingleTouchView.RIGHT_BOTTOM);
        singleTouchView.setControlDelLocation(SingleTouchView.LEFT_TOP);
        Bitmap bmp = BitmapFactory.decodeFile(savePath + fileName);
        singleTouchView.setImageResource(bmp);
        singleTouchView.bringToFront();
        fl_root.addView(singleTouchView);
        drawer_layout.closeDrawer(Gravity.RIGHT);
    }

    @Override
    public void onDownloadItem(int parentIndex, int pos, SlefProgress v, TextView view) {
        String url = leftList.get(parentIndex).getStamps().get(pos).getImage();
        downloadImg(url, url.substring(url.lastIndexOf("/")), parentIndex, pos, v, view);
    }

    /**
     * 截屏
     */
    Bitmap screenBitmap;

    public void takeScreenShot() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display dy = wm.getDefaultDisplay();
        int sHight = dy.getHeight();
        int sWidth = dy.getWidth();
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmapScreen = dView.getDrawingCache();
        img_pic.setVisibility(View.GONE);
        ll_save_pic.setVisibility(View.VISIBLE);
        if (bmpWidth > 0 && bmpHeight > 0 && bmpHeight < sHight * 2 / 3) {
            screenBitmap = Bitmap.createBitmap(bitmapScreen, 0, (sHight - bmpHeight) / 2, sWidth, bmpHeight + 50);
            dView.destroyDrawingCache();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sWidth, bmpHeight);
            fl_root.removeAllViews();
            img_save_pic.setLayoutParams(params);
            img_save_pic.setImageBitmap(screenBitmap);
        } else {
            screenBitmap = Bitmap.createBitmap(bitmapScreen);
            dView.destroyDrawingCache();
            fl_root.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            img_save_pic.setLayoutParams(params);
            img_save_pic.setImageBitmap(screenBitmap);
        }
        img_again.setVisibility(View.VISIBLE);
        if (screenBitmap != null) {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                imagePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(imagePath);
                FileOutputStream os = new FileOutputStream(file);
                screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                img_savetip.setVisibility(View.VISIBLE);
                Message msg = new Message();
                msg.what = SAVE_PIC;
                img_savetip.bringToFront();
                mHandler.sendMessageDelayed(msg, 2000);
                img_share.setVisibility(View.VISIBLE);
            } catch (Exception e) {
            }
        }
    }


    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 1024 * 2) { // 循环判断如果压缩后图片是否大于10m
            // ,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        BitmapFactory.Options bmpoptions = new BitmapFactory.Options();
        bmpoptions.inSampleSize = 2;
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, bmpoptions);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public void onEventMainThread(RightBean event) {
    }


//    @Override
//    public void onLeftItem(int pos) {
//        if (leftList.get(pos).getStamps() != null) {
//            rightAdapter = new RightAdapter(CustomerCameraActivity.this, leftList.get(pos).getStamps(),pos,leftList.get(pos).isIsAvailable());
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
        img_save_pic.setImageBitmap(bitmap);
//        FileOutputStream fos = null;
//        //获得sd卡路径
//        String rootPath = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED) ? Environment
//                .getExternalStorageDirectory().getAbsolutePath() : null;
//        //不存在文件夹就新建一个
//        try {
//            File file = new File(rootPath + "/screenShot/");
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//
//            fos = new FileOutputStream(rootPath + "/screenShot/"
//                    + System.currentTimeMillis() + ".png");
//            //把bitmap压缩成png格式，并通过fos写入到目标文件
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

}

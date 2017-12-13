package com.youbohudong.kfccalendar2018.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.*;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.adapter.LeftAdapter;
import com.youbohudong.kfccalendar2018.adapter.RightAdapter;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.youbohudong.kfccalendar2018.bean.RightBean;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
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

public class CustomerCameraActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener, RightAdapter.UpdateItemListening, SingleTouchView.DeleteUIListening,LeftAdapter.OnLeftClickListening {
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
    private LinearLayout ll_share, ll_close;
    private TextView txt_frends, txt_cancel, txt_frendsquare;
    ;
    private static final int REQUEST_XC_CODE = 101;
    private static final int DOWNLOADING = 102;
    private static final int DOWNLOADED = 103;
    private static final int DOWNLOAD_FAILED = 104;
    private static final int SAVE_PIC = 105;
    private ContentResolver resolver;
    private ImageView img_restart, img_save, img_paster, img_share, second_paster, img_save_pic,img_ar;
    private ImageView img_again, img_savetip;
    private LeftAdapter leftAdapter;
    private RightAdapter rightAdapter;

    Display display;
    IWXAPI api;
    private String isCheck = "0";
    private static String filePath = "";
    private  SharedPreferencesUtils spUtils;
//    private Integer[] fArr = {R.mipmap.art_01, R.mipmap.art_02, R.mipmap.art_03, R.mipmap.art_04, R.mipmap.art_05, R.mipmap.art_06,
//            R.mipmap.art_07, R.mipmap.art_08, R.mipmap.art_09, R.mipmap.art_10, R.mipmap.art_11, R.mipmap.art_12};
//
//    private Integer[] sArr = {R.mipmap.cal_01, R.mipmap.cal_02, R.mipmap.cal_03, R.mipmap.cal_04, R.mipmap.cal_05, R.mipmap.cal_06,
//            R.mipmap.cal_07, R.mipmap.cal_08, R.mipmap.cal_09, R.mipmap.cal_10, R.mipmap.cal_11, R.mipmap.cal_12,};
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
        api = WXAPIFactory.createWXAPI(this, "wx9b7b3c02f132a518");
        setContentView(R.layout.activity_customer_camera);
        spUtils=new SharedPreferencesUtils(this);
        spUtils.putBln("is_frist",false);
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
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        img_start = (ImageView) findViewById(R.id.img_start);
        img_switch = (ImageView) findViewById(R.id.img_switch);
        img_xz = (ImageView) findViewById(R.id.img_xz);
        suf_camera = (SurfaceView) findViewById(R.id.suf_camera);
        rl_finish = (RelativeLayout) findViewById(R.id.rl_finish);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        fl_root=(FrameLayout) findViewById(R.id.fl_root);
        img_pic = (ImageView) findViewById(R.id.img_pic);
        lv_left = (ListView) findViewById(R.id.lv_left);
        lv_right = (ListView) findViewById(R.id.lv_right);
        img_restart = (ImageView) findViewById(R.id.img_restart);
        img_save = (ImageView) findViewById(R.id.img_save);
        img_paster = (ImageView) findViewById(R.id.img_paster);
        second_paster = (ImageView) findViewById(R.id.second_paster);
        img_share = (ImageView) findViewById(R.id.img_share);
        img_again = (ImageView) findViewById(R.id.img_again);
        img_savetip = (ImageView) findViewById(R.id.img_savetip);
        img_save_pic = (ImageView) findViewById(R.id.img_save_pic);
        img_ar = (ImageView) findViewById(R.id.img_ar);

        ll_close = (LinearLayout) findViewById(R.id.ll_close);
        ll_share = (LinearLayout) findViewById(R.id.ll_share);

        txt_frends = (TextView) findViewById(R.id.txt_frends);
        txt_frendsquare = (TextView) findViewById(R.id.txt_frendsquare);
        txt_cancel = (TextView) findViewById(R.id.txt_cancel);


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

    }

    @Override
    public void initData() {
        leftList = new ArrayList<>();
        rightList = new ArrayList<>();
        leftAdapter = new LeftAdapter(CustomerCameraActivity.this, leftList);
        leftAdapter.setmOnLeftClickListening(this);
        lv_left.setAdapter(leftAdapter);
        getData();

    }

    /**
     *从服务器获取数据
     */
    private void getData() {
        String url = "https://www.youbohudong.com/api/biz/vip/kfc/calendar-2018/stamps";
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }


    public class MyStringCallback extends StringCallback {
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
            List<LeftBean> retList = gson.fromJson(response, new TypeToken<List<LeftBean>>() {
            }.getType());
            leftList.clear();
            leftList.addAll(retList);
            LeftBean bean = new LeftBean();
            bean.setName("敬请期待");
            leftList.add(bean);
            leftAdapter.notifyDataSetChanged();
            rightAdapter = new RightAdapter(CustomerCameraActivity.this, leftList.get(0).getStamps(),0,leftList.get(0).isIsAvailable());
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
                updateView();
                rl_finish.setVisibility(View.GONE);
                rl_root.setVisibility(View.GONE);
                getCamera();
                if (surfaceHolder != null) {
                    setPrive(camera, surfaceHolder);
                }

                break;
            case R.id.img_save://保存
                updateView();
                img_save.setVisibility(View.GONE);
                img_restart.setVisibility(View.GONE);
                img_again.setVisibility(View.VISIBLE);
                img_paster.setVisibility(View.GONE);
                second_paster.setVisibility(View.GONE);
                takeScreenShot();

                break;
            case R.id.img_share://分享
                ll_share.setVisibility(View.VISIBLE);

                break;
            case R.id.img_paster://贴纸
                drawer_layout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.img_again://再来一张
                rl_finish.setVisibility(View.GONE);
                rl_root.setVisibility(View.GONE);
                rl_root.removeAllViews();
                getCamera();
                if (surfaceHolder != null) {
                    setPrive(camera, surfaceHolder);
                }
                break;
            case R.id.txt_frends://回话
                wxShare(filePath, SendMessageToWX.Req.WXSceneSession);
                ll_share.setVisibility(View.GONE);
                break;
            case R.id.txt_frendsquare://朋友圈
                wxShare(filePath, SendMessageToWX.Req.WXSceneTimeline);
                ll_share.setVisibility(View.GONE);
                break;
            case R.id.txt_cancel://取消
                ll_share.setVisibility(View.GONE);
                break;
            case R.id.ll_close://关闭侧滑
                drawer_layout.closeDrawer(Gravity.RIGHT);
                break;
            case R.id.img_ar://ar扫描
                startActivity(new Intent(this,ArActivity.class));
                break;

        }

    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
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

                        // TODO Auto-generated method stub
                        Bitmap bMap;
                        try {// 获得图片


                            bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            bMap=compressImage(bMap);
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

                            rl_finish.setVisibility(View.VISIBLE);
                            rl_root.setVisibility(View.VISIBLE);
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

    // 加水印 也可以加文字
    public Bitmap watermarkBitmap(Bitmap src, Bitmap watermark,
                                  String title) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        //需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        Paint paint = new Paint();
        //加入图片
        if (watermark != null) {
            int ww = watermark.getWidth();
            int wh = watermark.getHeight();
//            paint.setAlpha(80);
            cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, paint);// 在src的右下角画入水印
        }
        //加入文字
        if (title != null) {
            String familyName = "黑体";
            Typeface font = Typeface.create(familyName, Typeface.NORMAL);
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.RED);
            textPaint.setTypeface(font);
            textPaint.setTextSize(8);
            //这里是自动换行的
            StaticLayout layout = new StaticLayout(title, textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            layout.draw(cv);
        }
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        return newb;
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
                            int height=photoBmp.getHeight();
                            int width=photoBmp.getWidth();
                            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(width,height);
                            img_pic.setLayoutParams(params);
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

    public void downloadImg(final String imgUrl, final String fileName, int parentIndex, int pos, final SlefProgress v,final TextView view) {
        OkHttpUtils.get().url(imgUrl).build().execute(new FileCallBack(savePath, fileName)
                {

                    @Override
                    public void onBefore(Request request, int id)
                    {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                        v.setProgress((int)progress*100);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        spUtils.putBln(imgUrl,true);
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
    public void onItemClick(int parentIndex,int pos,String fileName) {

        SingleTouchView singleTouchView = new SingleTouchView(CustomerCameraActivity.this);
                        singleTouchView.setmDeleteUIListening(CustomerCameraActivity.this);
                        singleTouchView.setImageScale(1);
                        singleTouchView.setControlLocation(SingleTouchView.RIGHT_BOTTOM);
                        singleTouchView.setControlDelLocation(SingleTouchView.LEFT_TOP);
                         Bitmap bmp= BitmapFactory.decodeFile(savePath+fileName);
                        singleTouchView.setImageResource(bmp);
                        singleTouchView.bringToFront();
                        fl_root.addView(singleTouchView);
                        drawer_layout.closeDrawer(Gravity.RIGHT);
        }

    @Override
    public void onDownloadItem(int parentIndex, int pos, SlefProgress v,TextView view) {
       String url= leftList.get(parentIndex).getStamps().get(pos).getImage();
        downloadImg(url,url.substring(url.lastIndexOf("/")),parentIndex,pos,v,view);
    }

    /**
     * 截屏
     */
    Bitmap screenBitmap;

    public void takeScreenShot() {
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        screenBitmap = Bitmap.createBitmap(dView.getDrawingCache());
        rl_root.setVisibility(View.GONE);
        img_save_pic.setImageBitmap(screenBitmap);
        if (screenBitmap != null) {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                filePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(filePath);
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
     * 微信分享
     */
    public void wxShare(String path, int mTargetScene) {
        Bitmap bmp = getimage(filePath);
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        //设置缩略图
//            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//            bmp.recycle();
//            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // ?????????

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);
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
        return bitmap;
//        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
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


    @Override
    public void onLeftItem(int pos) {
        if (leftList.get(pos).getStamps() != null) {
            rightAdapter = new RightAdapter(CustomerCameraActivity.this, leftList.get(pos).getStamps(),pos,leftList.get(pos).isIsAvailable());
            rightAdapter.setmUpdateItemListening(CustomerCameraActivity.this);
            lv_right.setAdapter(rightAdapter);
            leftAdapter.setPos(pos);
            leftAdapter.notifyDataSetChanged();


        }
    }
}

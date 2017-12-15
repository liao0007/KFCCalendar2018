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
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.youbohudong.kfccalendar2018.camera.util.CameraParamUtil;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import com.youbohudong.kfccalendar2018.utils.Util;

import java.io.*;
import java.util.List;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceHolder cameraSurfaceHolder;

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//0代表前置摄像头，1代表后置摄像头

    private ImageButton helpButton;

    private static final int REQUEST_XC_CODE = 101;
    private ContentResolver contentResolver;

    private Display display;

    int bitmapHeight, bitmapWidth;

    private static String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        SharedPreferencesUtils spUtils = new SharedPreferencesUtils(this);
        spUtils.setBoolean("is_first", false);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        contentResolver = getContentResolver();

        SurfaceView cameraSurfaceView = findViewById(R.id.cameraSurfaceView);
        cameraSurfaceHolder = cameraSurfaceView.getHolder();
        cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraSurfaceHolder.addCallback(this);

        ImageButton arImageButton = findViewById(R.id.arImageButton);
        arImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraActivity.this, ArActivity.class));
            }
        });

        ImageButton takePhotoImageButton = findViewById(R.id.takePhotoImageButton);
        takePhotoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        ImageButton switchCameraImageButton = findViewById(R.id.switchCameraImageButton);
        switchCameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        ImageButton importFromAlbumImageButton = findViewById(R.id.importFromAlbumImageButton);
        importFromAlbumImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent, REQUEST_XC_CODE);
            }
        });

        helpButton = findViewById(R.id.helpImageButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraActivity.this, GuideActivity.class));
                CameraActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_XC_CODE && resultCode == RESULT_OK && data != null) {
            // 得到图片的全路径
            Uri uri = data.getData();
            Bitmap photoBmp;
            if (uri != null) {
                try {
                    photoBmp = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                    bitmapHeight = photoBmp.getHeight();
                    bitmapWidth = photoBmp.getWidth();
                    Intent intent = new Intent(CameraActivity.this, StampActivity.class);
                    intent.putExtra("image", Util.bmpToByteArray(photoBmp, true));
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Camera
     */
    private void startCamera() {
        setCamera(currentCameraId);
    }

    private void switchCamera() {
        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        setCamera(currentCameraId);
    }

    private void setCamera(int cameraFacing) {
        stopPreviewAndFreeCamera();
        camera = Camera.open(cameraFacing);

        if (camera != null) {
            configCamera(camera);

            try {
                camera.setPreviewDisplay(cameraSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            camera.startPreview();
        }
    }


    private void takePhoto() {
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
                            if (currentCameraId == 1) {
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

                            helpButton.setVisibility(View.GONE);
                            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpeg");
                            //获取水印图片，如果图片过大，应对图片采样
//                            Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.splash);
//                            Bitmap bitmap2 = watermarkBitmap(bitmap, null, "ZHOUKAI");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void stopPreviewAndFreeCamera() {

        if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
            camera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            camera.release();

            camera = null;
        }
    }

    private void configCamera(Camera camera) {
        camera.setDisplayOrientation(CameraParamUtil.getInstance().getCameraDisplayOrientation(CameraActivity.this, currentCameraId));
        Camera.Parameters cameraParameters = camera.getParameters();
        cameraParameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        cameraParameters.setJpegQuality(100); // 设置照片质量

        //获得相机支持的照片尺寸,选择合适的尺寸
        List<Camera.Size> supportedPictureSizes = cameraParameters.getSupportedPictureSizes();
        int maxSize = Math.max(display.getWidth(), display.getHeight());
        if (maxSize > 0) {
            for (Camera.Size size : supportedPictureSizes) {
                if (maxSize <= Math.max(size.width, size.height)) {
                    cameraParameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
        }

        List<Camera.Size> supportedPreviewSizes = cameraParameters.getSupportedPreviewSizes();
        if (maxSize > 0) {
            for (Camera.Size ShowSize : supportedPreviewSizes) {
                if (maxSize <= Math.max(ShowSize.width, ShowSize.height)) {
                    cameraParameters.setPreviewSize(ShowSize.width, ShowSize.height);
                    break;
                }
            }
        }

        camera.setParameters(cameraParameters);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (camera != null) {
            camera.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
        }
        super.onPause();
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        startPreview(camera, cameraSurfaceHolder);
//        mHolder = holder;// SurfaceHolder是系统提供用来设置surfaceView的对象
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        camera.stopPreview();
//        startPreview(camera, cameraSurfaceHolder);
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        stopPreview();
//    }

    public static final Bitmap getBitmap(ContentResolver cr, Uri url)
            throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
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


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraSurfaceHolder = holder;
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        cameraSurfaceHolder = holder;
        startCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSurfaceHolder = holder;
        stopPreviewAndFreeCamera();
    }

}

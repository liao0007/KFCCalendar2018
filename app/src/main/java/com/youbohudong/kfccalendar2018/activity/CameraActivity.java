package com.youbohudong.kfccalendar2018.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.youbohudong.kfccalendar2018.camera.util.CameraParamUtil;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import com.youbohudong.kfccalendar2018.utils.Util;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceHolder cameraSurfaceHolder;
    private ProgressBar savingProgressBar;

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//0代表前置摄像头，1代表后置摄像头
    private static final int REQUEST_XC_CODE = 101;
    private static final int XJ_CODE = 102;
    private static final int XC_CODE = 103;
    private ContentResolver contentResolver;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        SharedPreferencesUtils spUtils = new SharedPreferencesUtils(this);
        spUtils.setBoolean("is_first", false);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        contentResolver = getContentResolver();

        savingProgressBar = findViewById(R.id.savingProgressBar);

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

        ImageButton helpButton = findViewById(R.id.helpImageButton);
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
            if (uri != null) {
                try {

                    //根据图片的filepath获取到一个ExifInterface的对象

                    ExifInterface exif = null;

                    try {

                        exif = new ExifInterface(uri.getPath());

                    } catch (IOException e) {

                        e.printStackTrace();

                        exif = null;

                    }
                    int degree = 0;

                    if (exif != null) {

                        // 读取图片中相机方向信息

                        int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,

                                ExifInterface.ORIENTATION_UNDEFINED);

                        // 计算旋转角度

                        switch (ori) {

                            case ExifInterface.ORIENTATION_ROTATE_90:

                                degree = 90;

                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:

                                degree = 180;

                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:

                                degree = 270;

                                break;

                            default:

                                degree = 0;

                                break;

                        }

                        if (degree != 0) {

                            // 旋转图片

                            Matrix m = new Matrix();

                            m.postRotate(degree);
//
//                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//
//                                    bitmap.getHeight(), m, true);

                        }
                    }

//                    compressAndCacheImage(MediaStore.Images.Media.getBitmap(contentResolver, uri));

                    Intent intent = new Intent(CameraActivity.this, StampActivity.class);
                    intent.putExtra("URI", uri.toString());
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
            safeToTakePicture = true;
        }
    }

    private boolean safeToTakePicture = false;

    private void takePhoto() {
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                MediaPlayer mediaPlayer = MediaPlayer.create(CameraActivity.this, R.raw.shutter);
                mediaPlayer.start();

                if (safeToTakePicture) {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {

                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                             /* rotate */
                            Matrix matrix = new Matrix();
                            matrix.reset();
                            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                matrix.postRotate(90);
                            } else {
                                matrix.postRotate(-90);
                                matrix.postScale(-1, 1);
                            }

                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                            try {// 获得图片
                                compressAndCacheImage(bitmap);
                                startActivity(new Intent(CameraActivity.this, StampActivity.class));
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    safeToTakePicture = false;
                }

            }
        });
    }

    private void compressAndCacheImage(Bitmap image) {
        savingProgressBar.setVisibility(View.VISIBLE);

        /* compress */
        image = Util.compressImage(image);

        /* save to file */
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fileOutputStream = openFileOutput("temp", Context.MODE_PRIVATE);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        camera.setDisplayOrientation(90);

        Camera.Parameters cameraParameters = camera.getParameters();
//        setDispaly(cameraParameters, camera);

        cameraParameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        cameraParameters.setJpegQuality(80); // 设置照片质量

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

        camera.setParameters(cameraParameters);
    }

    // 控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    // 实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod(
                    "setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera != null) {
            startCamera();
        }
        savingProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            stopPreviewAndFreeCamera();
        }
        super.onPause();
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

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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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
    private ProgressBar savingProgressBar;

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//0代表前置摄像头，1代表后置摄像头
    private static final int REQUEST_XC_CODE = 101;
    private ContentResolver contentResolver;
    private DisplayMetrics displayMetrics;

    private boolean isTakingPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        SharedPreferencesUtils spUtils = new SharedPreferencesUtils(this);
        spUtils.setBoolean("is_first", false);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

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
                    compressAndCacheImage(MediaStore.Images.Media.getBitmap(contentResolver, uri));
                    startActivity(new Intent(CameraActivity.this, ShareActivity.class));
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
        if(isTakingPhoto) {
            return;
        }
        isTakingPhoto = true;
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(CameraActivity.this, R.raw.shutter);
                        mediaPlayer.start();

                        /* rotate */
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                                matrix, true);

                        compressAndCacheImage(bitmap);
                        startActivity(new Intent(CameraActivity.this, ShareActivity.class));
                    }
                });
            }
        });
    }


    private void compressAndCacheImage(Bitmap image) {
        savingProgressBar.setVisibility(View.VISIBLE);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        try {
            FileOutputStream fileOutputStream = openFileOutput("temp", Context.MODE_PRIVATE);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
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
        camera.setDisplayOrientation(CameraParamUtil.getInstance().getCameraDisplayOrientation(CameraActivity.this, currentCameraId));

        Camera.Parameters cameraParameters = camera.getParameters();

        //set color efects to none
        cameraParameters.setColorEffect(Camera.Parameters.EFFECT_NONE);

        //set antibanding to none
        if (cameraParameters.getAntibanding() != null) {
            cameraParameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
        }

        // set white ballance
        if (cameraParameters.getWhiteBalance() != null) {
            cameraParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        }

        //set flash
        if (cameraParameters.getFlashMode() != null) {
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }

        //set zoom
        if (cameraParameters.isZoomSupported()) {
            cameraParameters.setZoom(0);
        }

        //set focus mode
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        cameraParameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        cameraParameters.setJpegQuality(100); // 设置照片质量
        cameraParameters.setPictureSize(displayMetrics.heightPixels, displayMetrics.widthPixels);

        camera.setParameters(cameraParameters);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (camera != null) {
            camera.startPreview();
        }
        savingProgressBar.setVisibility(View.GONE);
        isTakingPhoto = false;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
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

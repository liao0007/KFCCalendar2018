package com.youbohudong.kfccalendar2018.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.youbohudong.kfccalendar2018.R;

/**
 * Created by ${bcq} on 2017/12/13.
 */

public class ToastUtils {
    public static final int LENGTH_LONG = 2000;
    public static final int LENGTH_SHORT = 3000;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View toastView;
    private Context mContext;
    private Handler mHandler;
    private String mToastContent = "";
    private int duration = 0;
    private int animStyleId = android.R.style.Animation_Toast;

    private TextView txt_title;


    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            removeView();
        }
    };


    public ToastUtils(Context context) {
        Context ctx = context.getApplicationContext();
        if (ctx == null) {
            ctx = context;
        }
        this.mContext = ctx;
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        init();
    }

    private void init() {
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.alpha = 1.0f;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.gravity = Gravity.CENTER_HORIZONTAL;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowParams.setTitle("ToastUtils");
        mWindowParams.packageName = mContext.getPackageName();
        mWindowParams.windowAnimations = animStyleId;// TODO
        mWindowParams.y = mContext.getResources().getDisplayMetrics().widthPixels / 12;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private View getDefaultToastView(Context context) {
        Context ctx = context.getApplicationContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View layout = inflater.inflate(R.layout.layout_toast, null);

        return layout;
    }

    public void show(Context context, String title) {
        removeView();
        if (toastView == null) {
            toastView = getDefaultToastView(context);
        }
        txt_title = (TextView) toastView.findViewById(R.id.tv_loading_dialog);
        LinearLayout ll_loading = (LinearLayout) toastView.findViewById(R.id.ll_loading);
        ll_loading.getBackground().setAlpha(159);
        txt_title.setText(title);
        mWindowParams.gravity = android.support.v4.view.GravityCompat
                .getAbsoluteGravity(Gravity.CENTER_HORIZONTAL, android.support.v4.view.ViewCompat.getLayoutDirection(toastView));
        removeView();
        mWindowManager.addView(toastView, mWindowParams);
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(timerRunnable, LENGTH_LONG);
    }

    public void removeView() {
        if (toastView != null && toastView.getParent() != null) {
            mWindowManager.removeView(toastView);
            mHandler.removeCallbacks(timerRunnable);
        }
    }

    /**
     * @param context
     * @param content
     * @param duration
     * @return
     */
    public static ToastUtils makeText(Context context, String content,
                                      int duration) {
        ToastUtils helper = new ToastUtils(context);
        helper.setDuration(duration);
        helper.setContent(content);
        return helper;
    }

    /**
     * @param context
     * @param strId
     * @param duration
     * @return
     */
    public static ToastUtils makeText(Context context, int strId, int duration) {
        ToastUtils helper = new ToastUtils(context);
        helper.setDuration(duration);
        helper.setContent(context.getString(strId));
        return helper;
    }


    public ToastUtils setContent(String content) {
        this.mToastContent = content;
        return this;
    }

    public ToastUtils setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public ToastUtils setAnimation(int animStyleId) {
        this.animStyleId = animStyleId;
        mWindowParams.windowAnimations = this.animStyleId;
        return this;
    }

    /**
     * custom view
     *
     * @param view
     */
    public ToastUtils setView(View view) {
        this.toastView = view;
        return this;
    }

}
package com.youbohudong.kfccalendar2018.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youbohudong.kfccalendar2018.R;

/**
 * Created by ${bcq} on 2017/12/13.
 */

public class CustomProgress extends Dialog
{
    private  CustomProgress dialog;
    private Context ctx;
    private TextView tv_loading_dialog;

    public CustomProgress(Context context)
    {
        this(context, R.style.customDailog);
        this.ctx = context;
    }

    public CustomProgress(Context context, int theme)
    {
        super(context, theme);
    }

    /**
     * 当窗口焦点改变时调用
     */
    public void onWindowFocusChanged(boolean hasFocus)
    {
        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_toast, null);
        LinearLayout ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
       TextView tv_loading_dialog=(TextView) view.findViewById(R.id.tv_loading_dialog);
        ll_loading.getBackground().setAlpha(80);
    }

    /**
     * 弹出自定义ProgressDialog
     *
     * @param context 上下文
     *                按下返回键监听
     * @return
     */
    public void showInit(Context context)
    {
        dialog = null;
        dialog = new CustomProgress(context);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        LinearLayout ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
        tv_loading_dialog = (TextView) view.findViewById(R.id.tv_loading_dialog);

        ll_loading.getBackground().setAlpha(159);
//      draw = (AnimationDrawable) imageView.getDrawable();
        dialog.setContentView(view);
        // 按返回键是否取消
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(lp);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

    }

    public void showTips(String content)
    {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }else if(dialog!=null&&(!((Activity)ctx).isFinishing())){
            tv_loading_dialog.setText(content);
            dialog.show();
        }
    }

    public void CustomDismis()
    {
        if (dialog != null || dialog.isShowing()) {
            dialog.cancel();
            dialog.dismiss();
        }
    }

}
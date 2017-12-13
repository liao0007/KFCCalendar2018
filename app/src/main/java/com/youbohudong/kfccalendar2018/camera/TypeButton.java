package com.youbohudong.kfccalendar2018.camera;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import com.youbohudong.kfccalendar2018.R;


/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.0.4
 * 创建日期：2017/4/26
 * 描    述：拍照或录制完成后弹出的确认和返回按钮
 * =====================================
 */
public class TypeButton extends View {
    public static final int TYPE_CANCEL = 0x001;
    public static final int TYPE_CONFIRM = 0x002;
    private int button_type;
    private int button_size;

    private float center_X;
    private float center_Y;
    private float button_radius;

    private Paint mPaint;
    private Path path;
    private float strokeWidth;

    private float index;
    private RectF rectF;

    public TypeButton(Context context) {
        super(context);
    }

    public TypeButton(Context context, int type, int size) {
        super(context);
        this.button_type = type;
        button_size = size;
        button_radius = size / 2.0f;
        center_X = size / 2.0f;
        center_Y = size / 2.0f;

        mPaint = new Paint();
        path = new Path();
        strokeWidth = size / 50f;
        index = button_size / 12f;
        rectF = new RectF(center_X, center_Y - index, center_X + index * 2, center_Y + index);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(button_size, button_size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //如果类型为取消，则绘制内部为返回箭头
        if (button_type == TYPE_CANCEL) {
            Rect rect = new Rect(0, 0, 200, 100);
            RectF rectF = new RectF(0, 0, 200, 100);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.retake);
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(bitmapShader);
            canvas.drawBitmap(bitmap, rect, rectF, mPaint);


        }
        //如果类型为确认，则绘制绿色勾
        if (button_type == TYPE_CONFIRM) {
            Rect rect = new Rect(0, 0, 200, 100);
            RectF rectF = new RectF(0, 0, 200, 100);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.save);
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(bitmapShader);
            canvas.drawBitmap(bitmap, rect, rectF, mPaint);

        }
    }
}

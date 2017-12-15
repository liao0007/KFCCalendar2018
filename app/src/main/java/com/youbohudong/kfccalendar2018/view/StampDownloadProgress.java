package com.youbohudong.kfccalendar2018.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 作者：bcq on 2017/3/22 11:03
 * 自定义进度条
 */

public class StampDownloadProgress extends ProgressBar {
    public StampDownloadProgress(Context context) {
        this(context, null);
    }

    public StampDownloadProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StampDownloadProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

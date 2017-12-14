package com.youbohudong.kfccalendar2018.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ${bcq} on 2017/11/6.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public static final String ArActivity = "ArActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

    }

    public abstract void initView();

    public abstract void initData();

    public abstract void initListening();


}

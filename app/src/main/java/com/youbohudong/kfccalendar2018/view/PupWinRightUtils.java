package com.youbohudong.kfccalendar2018.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.youbohudong.kfccalendar2018.R;

/**
 * Created by ${bcq} on 2017/12/9.
 */

public class PupWinRightUtils {
    private Context ctx;
    private LayoutInflater mInflater;
    private PopupWindow popWin;
    private TextView txt_content;

    public PupWinRightUtils(Context ctx) {
        this.ctx = ctx;
        mInflater=LayoutInflater.from(ctx);
        initView(ctx);
    }

    private void initView(Context ctx) {
       View v= mInflater.inflate(R.layout.pop_win,null);
        txt_content=(TextView)v.findViewById(R.id.txt_content);
        popWin=new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWin.setContentView(v);
    }
    public void show(View view,String str){
        if(popWin!=null){
            txt_content.setText(str);
//            popWin.showAsDropDown(view);
            popWin.showAsDropDown(view,-500, (int)(view.getY()/2));
//            popWin.showAtLocation(view, Gravity.LEFT,350,(int)(view.getY()/2));
        }
    }
    public void close(){
        if(popWin!=null&&popWin.isShowing()){
            popWin.dismiss();
        }
    }
}

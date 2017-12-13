package com.youbohudong.kfccalendar2018.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.youbohudong.kfccalendar2018.R;


/**
 * Created by ${bcq} on 2017/11/15.
 */

public class MyView extends RelativeLayout implements View.OnClickListener{
    private ImageView img_content,img_scale,img_delete;
    private int imagurl;
    public MyView(Context context,int imagurl) {
        super(context);
        this.imagurl=imagurl;
        initView(context);
    }

    private void initView(Context context) {
        View view=LayoutInflater.from(context).inflate(R.layout.view_changes,null);
        img_content=(ImageView) view.findViewById(R.id.img_content);
         img_scale=(ImageView) view.findViewById(R.id.img_scale);
         img_delete=(ImageView) view.findViewById(R.id.img_delete);
        img_content.setImageResource(imagurl);
        initListening();
        addView(view);
        invalidate();
    }

    private float startX,startY,endX,endY;
    private void initListening() {
        img_delete.setOnClickListener(this);
        img_scale.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX=getX();
                        startY=getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                      endX=  getX()-endX;
                        endY=getY()-endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        setX(endX);
                        setY(endY);
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=getX();
                startY=getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX=  event.getX();
                endY=event.getY();
                setX(endX);
                setY(endY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_delete://删除
                break;
        }
    }
}

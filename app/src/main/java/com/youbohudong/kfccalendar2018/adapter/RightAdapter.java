package com.youbohudong.kfccalendar2018.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.activity.StampActivity;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import com.youbohudong.kfccalendar2018.utils.ToastUtils;
import com.youbohudong.kfccalendar2018.view.PupWinRightUtils;
import com.youbohudong.kfccalendar2018.view.StampDownloadProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${bcq} on 2017/11/7.
 */

public class RightAdapter extends BaseAdapter {
    private Context ctx;
    private List<LeftBean.StampsBean> list;
    private LayoutInflater mInflater;
    private List<View> viewList;                    //View对象集合
    private int parentIndex;
    private boolean isAvaliable;
    private ToastUtils toastUtils;
    public RightAdapter(Context ctx, List<LeftBean.StampsBean> list, int parentIndex, boolean isAvaliable) {
        this.ctx = ctx;
        this.list = list;
        this.isAvaliable = isAvaliable;
        this.parentIndex = parentIndex;
        this.viewList = new ArrayList<>();
        mInflater = LayoutInflater.from(ctx);
        toastUtils=new ToastUtils(ctx);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.right_layout, null);
            holder.img_pic = (ImageView) view.findViewById(R.id.img_pic);
            holder.rl_bg = (RelativeLayout) view.findViewById(R.id.rl_bg);
            holder.sprogrss = (StampDownloadProgress) view.findViewById(R.id.sprogrss);
            holder.fl_shade = (RelativeLayout) view.findViewById(R.id.fl_shade);
            holder.txt_down = (TextView) view.findViewById(R.id.txt_down);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final LeftBean.StampsBean bean = list.get(i);
        if (bean != null) {
            Glide.with(ctx).load(bean.getThumb()).into(holder.img_pic);
            final boolean isDown = new SharedPreferencesUtils(ctx).getBoolean(bean.getImage(), false);
            if (isDown) {
                holder.fl_shade.setVisibility(View.GONE);
                holder.sprogrss.setVisibility(View.GONE);
            } else {
                holder.fl_shade.setVisibility(View.VISIBLE);
                holder.fl_shade.getBackground().setAlpha(100);
            }

            if (!TextUtils.isEmpty(bean.getTaskKey())) {
                holder.rl_bg.setBackgroundResource(R.drawable.rl_shape_bg);
            } else {
                holder.rl_bg.setBackgroundResource(R.drawable.rl_shape_normal);
            }


            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (!TextUtils.isEmpty(bean.getNote())) {
                                toastUtils.show(ctx, bean.getNote());
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (isAvaliable) {
                                if (isDown) {
                                    holder.sprogrss.setVisibility(View.GONE);
                                    mUpdateItemListening.onItemClick(parentIndex, i, bean.getImage().substring(bean.getImage().lastIndexOf("/")));
                                } else {
                                    holder.sprogrss.setVisibility(View.VISIBLE);
                                    mUpdateItemListening.onDownloadItem(parentIndex, i, holder.sprogrss, holder.txt_down);
                                }
                            } else {
                                holder.sprogrss.setVisibility(View.GONE);
                            }

                            break;
                    }
                    return true;
                }
            });
        }
        return view;
    }

    public class ViewHolder {
        public ImageView img_pic;
        RelativeLayout rl_bg;
        public StampDownloadProgress sprogrss;
        RelativeLayout fl_shade;
        TextView txt_down;
    }

    UpdateItemListening mUpdateItemListening;

    public UpdateItemListening getmUpdateItemListening() {
        return mUpdateItemListening;
    }

    public void setmUpdateItemListening(UpdateItemListening mUpdateItemListening) {
        this.mUpdateItemListening = mUpdateItemListening;
    }


    public void Update() {
        notifyDataSetChanged();
    }

    public interface UpdateItemListening {
        void onItemClick(int parentIndex, int pos, String fileName);

        void onDownloadItem(int parentIndex, int pos, StampDownloadProgress v, TextView view);

    }
}
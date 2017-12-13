package com.youbohudong.kfccalendar2018.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.youbohudong.kfccalendar2018.R;


/**
 * Created by ${bcq} on 2017/11/16.
 */

public class RightLocalAdapter extends BaseAdapter {
    private Context ctx;
    private Integer[] list;
    private LayoutInflater mInflater;

    public RightLocalAdapter(Context ctx, Integer[] list) {
        this.ctx = ctx;
        this.list = list;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int i) {
        return list[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.right_local_layout, null);
            holder.img_pic = (ImageView) view.findViewById(R.id.img_pic);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.img_pic.setImageResource(list[i]);

        return view;
    }

    class ViewHolder {
        ImageView img_pic;
    }

    RightAdapter.UpdateItemListening mUpdateItemListening;

    public RightAdapter.UpdateItemListening getmUpdateItemListening() {
        return mUpdateItemListening;
    }

    public void setmUpdateItemListening(RightAdapter.UpdateItemListening mUpdateItemListening) {
        this.mUpdateItemListening = mUpdateItemListening;
    }

    public interface UpdateItemListening {
        void onItemClick(int pos);
    }
}
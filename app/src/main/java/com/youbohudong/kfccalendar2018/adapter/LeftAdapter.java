package com.youbohudong.kfccalendar2018.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.bean.LeftBean;

import java.util.List;

/**
 * Created by ${bcq} on 2017/11/7.
 */

public class LeftAdapter extends BaseAdapter {
    private Context context;
    private List<LeftBean> list;
    private LayoutInflater mInflater;
    private int pos;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public LeftAdapter(Context context, List<LeftBean> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
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
            view = mInflater.inflate(R.layout.left_layout, null);
            holder.txt_title = (TextView) view.findViewById(R.id.txt_title);
            holder.img_newtag = (ImageView) view.findViewById(R.id.img_newtag);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (i == pos) {
            holder.txt_title.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.left_shape_click));
        } else {
            holder.txt_title.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.left_shape));
        }

        final LeftBean bean = list.get(i);
        if (bean != null) {
            holder.txt_title.setText(bean.getName());

            if (bean.isIsNew()) {
                holder.img_newtag.setVisibility(View.VISIBLE);
            } else {
                holder.img_newtag.setVisibility(View.GONE);
            }
            if (bean.isIsAvailable()) {
                holder.txt_title.setTextColor(context.getResources().getColor(R.color.red_txt));
            } else {
                holder.txt_title.setTextColor(context.getResources().getColor(R.color.light_red));
            }
            if (bean.getName().equals("敬请期待")) {
                holder.txt_title.setTextColor(context.getResources().getColor(R.color.app_white));
                holder.txt_title.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.left_shape_qidai));
            }
        }

        return view;
    }

    class ViewHolder {
        TextView txt_title;
        ImageView img_newtag;
    }

}

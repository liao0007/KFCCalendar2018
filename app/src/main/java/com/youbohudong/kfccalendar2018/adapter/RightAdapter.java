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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.bean.LeftBean;
import com.youbohudong.kfccalendar2018.bean.UserDataBean;
import com.youbohudong.kfccalendar2018.utils.DeviceUuidFactory;
import com.youbohudong.kfccalendar2018.utils.SharedPreferencesUtils;
import com.youbohudong.kfccalendar2018.utils.ToastUtils;
import com.youbohudong.kfccalendar2018.view.StampDownloadProgress;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;

import java.util.List;
import java.util.UUID;

/**
 * Created by ${bcq} on 2017/11/7.
 */

public class RightAdapter extends BaseAdapter {
    private static List<UserDataBean> userDataList;

    private Context context;
    private List<LeftBean.StampsBean> list;
    private LayoutInflater mInflater;
    private int parentIndex;
    private boolean isAvailable;
    private ToastUtils toastUtils;

    public RightAdapter(Context context, List<LeftBean.StampsBean> list, int parentIndex, boolean isAvailable) {
        this.context = context;
        this.list = list;
        this.isAvailable = isAvailable;
        this.parentIndex = parentIndex;
        mInflater = LayoutInflater.from(context);
        toastUtils = new ToastUtils(context);

        if (userDataList == null) {
            requestUserData();
        }
    }

    /**
     * 从服务器获取数据
     */
    private void requestUserData() {
        UUID uuid = new DeviceUuidFactory(context).getDeviceUuid();
        String url = "https://www.youbohudong.com/api/biz/vip/kfc/calendar-2018/tasks/" + uuid;
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new UserDataCallback());
    }


    public class UserDataCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            // json转为带泛型的list

            userDataList = gson.fromJson(response, new TypeToken<List<UserDataBean>>() {
            }.getType());
        }

        @Override
        public void inProgress(float progress, long total, int id) {
        }
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
            holder.img_pic = (ImageView) view.findViewById(R.id.stampPhotoImageView);
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
            Glide.with(context).load(bean.getThumb()).into(holder.img_pic);
            final boolean isDownloaded = new SharedPreferencesUtils(context).getBoolean(bean.getImage(), false);
            if (isDownloaded) {
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
                    String taskKey = bean.getTaskKey();
                    String note = bean.getNote();

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (isAvailable && !TextUtils.isEmpty(taskKey) && !checkTaskKeyCompleted(taskKey) && !TextUtils.isEmpty(note)) {
                                toastUtils.show(context, bean.getNote());
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (isAvailable) {
                                if (isDownloaded) {
                                    holder.sprogrss.setVisibility(View.GONE);
                                    mUpdateItemListening.onItemClick(parentIndex, i, bean.getImage().substring(bean.getImage().lastIndexOf("/")));
                                } else {
                                    if (TextUtils.isEmpty(taskKey) || !TextUtils.isEmpty(taskKey) && checkTaskKeyCompleted(taskKey)) {
                                        holder.sprogrss.setVisibility(View.VISIBLE);
                                        mUpdateItemListening.onDownloadItem(parentIndex, i, holder.sprogrss, holder.txt_down);
                                    }
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

    private boolean checkTaskKeyCompleted(String taskKey) {
        for (UserDataBean userData :
                userDataList) {
            if (userData.getTaskKey().equals(taskKey) && userData.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    public class ViewHolder {
        ImageView img_pic;
        RelativeLayout rl_bg;
        public StampDownloadProgress sprogrss;
        RelativeLayout fl_shade;
        TextView txt_down;
    }

    UpdateItemListening mUpdateItemListening;

    public void setmUpdateItemListening(UpdateItemListening mUpdateItemListening) {
        this.mUpdateItemListening = mUpdateItemListening;
    }

    public interface UpdateItemListening {
        void onItemClick(int parentIndex, int pos, String fileName);
        void onDownloadItem(int parentIndex, int pos, StampDownloadProgress v, TextView view);
    }
}
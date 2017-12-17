package com.youbohudong.kfccalendar2018.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import com.youbohudong.kfccalendar2018.R;
import com.youbohudong.kfccalendar2018.base.BaseActivity;
import com.youbohudong.kfccalendar2018.bean.GuideBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${bcq} on 2017/12/1.
 */

public class GuideActivity extends BaseActivity {
    private VideoView videoView;
    private ViewPager viewPager;
    private List<GuideBean> guideBeanList;
    private int currentItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initListening();
        initData();
    }

    @Override
    public void initView() {
        guideBeanList = new ArrayList<>();
        guideBeanList.add(new GuideBean("欢迎来到K记大玩家", "本上校为你带来了整整一年的惊喜…"));
        guideBeanList.add(new GuideBean("AR黑科技 扫扫有惊喜", "玩转AR黑科技 扫海报 扫汉堡…\n扫得越多 惊喜越多"));
        guideBeanList.add(new GuideBean("收集贴纸 秀翻朋友圈", "收集限定精美贴纸\n分享朋友圈秀出独一无二的你"));
        guideBeanList.add(new GuideBean("参加主题活动 赢惊喜礼物", "开启消息推送获取最新活动讯息\n参加店内活动赢取免费礼物"));

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        videoView = findViewById(R.id.videoView);
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.guide;
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListening() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onPause() {
        videoView.stopPlayback();
        super.onResume();
    }

    public class ViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(GuideActivity.this).inflate(R.layout.item_guide, null);
            ImageView img_icon = (ImageView) v.findViewById(R.id.img_icon);
            ImageView img_dot1 = v.findViewById(R.id.img_dot1);
            ImageView img_dot2 = v.findViewById(R.id.img_dot2);
            ImageView img_dot3 = v.findViewById(R.id.img_dot3);
            ImageView img_dot4 = v.findViewById(R.id.img_dot4);

            TextView txt_title = (TextView) v.findViewById(R.id.txt_title);
            TextView txt_content = (TextView) v.findViewById(R.id.txt_content);
            ImageView img_next = (ImageView) v.findViewById(R.id.img_next);
            TextView txt_skip = (TextView) v.findViewById(R.id.txt_skip);

            if (position == 0) {
                img_icon.setVisibility(View.VISIBLE);
            } else {
                img_icon.setVisibility(View.INVISIBLE);
            }
            if (position == 0) {
                img_dot1.setBackgroundResource(R.mipmap.dot_red);
                img_dot2.setBackgroundResource(R.mipmap.dot_light);
                img_dot3.setBackgroundResource(R.mipmap.dot_light);
                img_dot4.setBackgroundResource(R.mipmap.dot_light);
                img_next.setBackgroundResource(R.mipmap.next_page);
            } else if (position == 1) {
                img_dot1.setBackgroundResource(R.mipmap.dot_light);
                img_dot2.setBackgroundResource(R.mipmap.dot_red);
                img_dot3.setBackgroundResource(R.mipmap.dot_light);
                img_dot4.setBackgroundResource(R.mipmap.dot_light);
                img_next.setBackgroundResource(R.mipmap.next_page);
            } else if (position == 2) {
                img_dot1.setBackgroundResource(R.mipmap.dot_light);
                img_dot2.setBackgroundResource(R.mipmap.dot_light);
                img_dot3.setBackgroundResource(R.mipmap.dot_red);
                img_dot4.setBackgroundResource(R.mipmap.dot_light);
                img_next.setBackgroundResource(R.mipmap.next_page);
            } else {
                img_dot1.setBackgroundResource(R.mipmap.dot_light);
                img_dot2.setBackgroundResource(R.mipmap.dot_light);
                img_dot3.setBackgroundResource(R.mipmap.dot_light);
                img_dot4.setBackgroundResource(R.mipmap.dot_red);
                img_next.setBackgroundResource(R.mipmap.start);
            }

            txt_title.setText(guideBeanList.get(position).getTitle());
            txt_content.setText(guideBeanList.get(position).getContent());
            container.addView(v);
            img_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentItem == (guideBeanList.size() - 1)) {
                        finish();
                    } else {
                        viewPager.setCurrentItem(currentItem + 1);
                    }

                }
            });
            txt_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return guideBeanList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}

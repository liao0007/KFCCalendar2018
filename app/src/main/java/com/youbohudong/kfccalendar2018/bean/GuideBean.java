package com.youbohudong.kfccalendar2018.bean;

/**
 * Created by ${bcq} on 2017/12/1.
 */

public class GuideBean {
    private String title;
    private String content;

    public GuideBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

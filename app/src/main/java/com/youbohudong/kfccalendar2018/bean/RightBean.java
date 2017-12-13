package com.youbohudong.kfccalendar2018.bean;

import java.io.Serializable;

/**
 * Created by ${bcq} on 2017/11/7.
 */

public class RightBean implements Serializable {
    private String id;
    private String imgUrl;
    private String isDownload; //下载状态
    private int progress; //进度

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(String isDownload) {
        this.isDownload = isDownload;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean equals(Object obj){
        if(this == obj) return true;    //若指向同一个对象，直接返回true
        boolean flag = obj instanceof RightBean;    //判断obj是否属于RightBean这个类
        if(flag){
            RightBean one = (RightBean) obj;

            if(this.id == one.id && this.imgUrl.equals(one.imgUrl)&&this.progress==one.progress&&this.isDownload.equals(one.isDownload)) {       //基本数据类型直接用==，string为引用数据类型，调用String类本身重写的equals方法
                return true;
            }
            else return false;
        }
        else return false;
    }

}

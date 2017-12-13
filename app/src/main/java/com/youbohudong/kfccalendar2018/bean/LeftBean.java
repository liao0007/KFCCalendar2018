package com.youbohudong.kfccalendar2018.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ${bcq} on 2017/11/7.
 * 左面的实体
 */

public class LeftBean implements Serializable {


    /**
     * name : K记有态度
     * stamps : [{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/160d19162efcd6cd4e57b94f2907cc80.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/160d19162efcd6cd4e57b94f2907cc80.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/c6887e3c85aabe2221347ef2ae398845.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/c6887e3c85aabe2221347ef2ae398845.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/47c8aa6aa01ff4161c9056e4cd7d55ad.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/47c8aa6aa01ff4161c9056e4cd7d55ad.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/d5fbb0ee4ba775a43622ac741339b1e8.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/d5fbb0ee4ba775a43622ac741339b1e8.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/47f1bfddb74684252684f780235d5c8b.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/47f1bfddb74684252684f780235d5c8b.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/b9d43ac75fe406bc04b67c9b8e4aadde.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/b9d43ac75fe406bc04b67c9b8e4aadde.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/32a994e08066991d8080082b2194c221.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/32a994e08066991d8080082b2194c221.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/0a24b448be062798ddcfb5d25a8f89d9.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/0a24b448be062798ddcfb5d25a8f89d9.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/79a6480437059faa454a5169b2aee1b2.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/79a6480437059faa454a5169b2aee1b2.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/42356b537870fa3ae08aca358219fe2d.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/42356b537870fa3ae08aca358219fe2d.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/9e40e1f509968f7aeecf98c19dabe569.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/9e40e1f509968f7aeecf98c19dabe569.png"},{"thumb":"https://static.youbohudong.com/uploaded/2017/11/30/ac020711e5c7a11dd7b66dc085d0af9d.png?x-oss-process=image/resize,limit_0,w_180","image":"https://static.youbohudong.com/uploaded/2017/11/30/ac020711e5c7a11dd7b66dc085d0af9d.png"}]
     * isNew : false
     * isAvailable : true
     * note :
     */

    private String name;
    private boolean isNew;
    private boolean isAvailable;
    private String note;
    private List<StampsBean> stamps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<StampsBean> getStamps() {
        return stamps;
    }

    public void setStamps(List<StampsBean> stamps) {
        this.stamps = stamps;
    }

    public static class StampsBean {
        /**
         * thumb : https://static.youbohudong.com/uploaded/2017/11/30/160d19162efcd6cd4e57b94f2907cc80.png?x-oss-process=image/resize,limit_0,w_180
         * image : https://static.youbohudong.com/uploaded/2017/11/30/160d19162efcd6cd4e57b94f2907cc80.png
         */

        private String thumb;
        private String image;
        private String note;
        private String taskKey;
        private int progress;
        private boolean isDownload;


        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getTaskKey() {
            return taskKey;
        }

        public void setTaskKey(String taskKey) {
            this.taskKey = taskKey;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public boolean isDownload() {
            return isDownload;
        }

        public void setDownload(boolean download) {
            isDownload = download;
        }
    }
}

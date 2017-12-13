package com.youbohudong.kfccalendar2018.bean;

/**
 * Created by ${bcq} on 2017/12/12.
 * 扫描成功的bean
 */

public class ScanBean {

    /**
     * completionResource : https://static.youbohudong.com/uploaded/2017/12/11/0c35a2add36205d5cea5e0b10afb14c1.gif
     * completionDescription : 成功获得比利时巧克力冰淇淋限定版表情贴纸包！点击“去看看”立即领取第二个半价优惠券！
     * completionUrl : https://www.youbohudong.com/biz/vip/kfc/belgium-ice-cream-20171218/share
     */

    private String completionResource;
    private String completionDescription;
    private String completionUrl;

    public String getCompletionResource() {
        return completionResource;
    }

    public void setCompletionResource(String completionResource) {
        this.completionResource = completionResource;
    }

    public String getCompletionDescription() {
        return completionDescription;
    }

    public void setCompletionDescription(String completionDescription) {
        this.completionDescription = completionDescription;
    }

    public String getCompletionUrl() {
        return completionUrl;
    }

    public void setCompletionUrl(String completionUrl) {
        this.completionUrl = completionUrl;
    }
}

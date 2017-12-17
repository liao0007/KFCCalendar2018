package com.youbohudong.kfccalendar2018.bean;

import java.util.List;

public class TaskBean {
    private String key;
    private Boolean isOnPromotion;
    private String promotionUrl;
    private String name;
    private String brief;
    private String thumbnail;
    private String banner;
    private List<String> images;

    public TaskBean(String key, Boolean isOnPromotion, String promotionUrl, String name, String brief, String thumbnail, String banner, List<String> images, String description) {
        this.key = key;
        this.isOnPromotion = isOnPromotion;
        this.promotionUrl = promotionUrl;
        this.name = name;
        this.brief = brief;
        this.thumbnail = thumbnail;
        this.banner = banner;
        this.images = images;
        this.description = description;
    }

    public Boolean getOnPromotion() {
        return isOnPromotion;
    }

    public void setOnPromotion(Boolean onPromotion) {
        isOnPromotion = onPromotion;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPromotionUrl() {
        return promotionUrl;
    }

    public void setPromotionUrl(String promotionUrl) {
        this.promotionUrl = promotionUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

}

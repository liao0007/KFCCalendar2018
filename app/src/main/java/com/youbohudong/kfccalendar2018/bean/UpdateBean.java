package com.youbohudong.kfccalendar2018.bean;

public class UpdateBean {
    private float version;
    private String description;

    public UpdateBean(float version, String description) {
        this.version = version;
        this.description = description;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

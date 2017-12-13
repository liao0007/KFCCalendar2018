package com.youbohudong.kfccalendar2018.utils;

import android.app.Activity;
import android.content.Intent;
import com.youbohudong.kfccalendar2018.base.App;

import java.util.List;

/**
 * Created by ${bcq} on 2017/11/6.
 */

public class ActivityManager {

    private List<Activity> actList;

    public ActivityManager() {
    }

    public void startIntoAct(Class clazz){
        Intent intent=new Intent(App.getInstance(),clazz);
        App.getInstance().startActivity(intent);

    }
}

package com.youbohudong.kfccalendar2018.services;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import com.allenliu.versionchecklib.core.AVersionService;
import com.google.gson.Gson;
import com.youbohudong.kfccalendar2018.bean.UpdateBean;

public class VersionCheckService extends AVersionService {
    public VersionCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        Gson gson = new Gson();
        UpdateBean updateBean = gson.fromJson(response, UpdateBean.class);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            float clientVersion = Float.parseFloat(pInfo.versionName);

            if (updateBean.getVersion() > clientVersion) {
                showVersionDialog("https://static.youbohudong.com/biz/vip/kfc/calendar-2018/app-release.apk", "检测到新版本", updateBean.getDescription());
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }
}

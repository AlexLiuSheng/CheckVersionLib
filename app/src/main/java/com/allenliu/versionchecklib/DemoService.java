package com.allenliu.versionchecklib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DemoService extends AVersionService {
    @Override
    public void onResponses(AVersionService service, String response) {
        String downloadUrl = "http://www.apk3.com/uploads/soft/guiguangbao/UCllq.apk";
        int serverVersion = 2;
        int clientVersion = 1;
        String updateMsg="1.Allen is handsome\n2.楼上说的对\n3.不服就撂倒";
            if (serverVersion > clientVersion) {
                service.showVersionDialog(downloadUrl, updateMsg);
            } else {
                stopSelf();
            }
    }
}

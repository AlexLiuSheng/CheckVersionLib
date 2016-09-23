package com.allenliu.versionchecklib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DemoService extends AVersionService {
    public DemoService() {
    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        super.onBind(intent);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        String downloadUrl = "http://www.apk3.com/uploads/soft/guiguangbao/UCllq.apk";
        int serverVersion = 2;
        int clientVersion = 1;
        String updateMsg="1.Allen is handsome\n2.楼上说的对\n3.不服就撂倒";
        if (serverVersion > clientVersion) {
            service.showVersionDialog(downloadUrl,updateMsg );
        } else {
            stopSelf();
        }
    }
}

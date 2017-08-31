package com.allenliu.forceupdatedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.allenliu.versionchecklib.core.AVersionService;

public class MyService extends AVersionService {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        //这里你解析response，判断是否更新以及是否强制更新，将参数传过去\
        boolean isForceUpdate=true;
        Bundle bundle=new Bundle();
        bundle.putBoolean("isForceUpdate",isForceUpdate);
        showVersionDialog("downloadUrl","test","test",bundle);
    }
}

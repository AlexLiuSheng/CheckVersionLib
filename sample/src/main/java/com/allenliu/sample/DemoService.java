package com.allenliu.sample;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.allenliu.versionchecklib.AVersionService;

public class DemoService extends AVersionService {
    public DemoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
      Log.e("DemoService", response);
        service.showVersionDialog("http://www.apk3.com/uploads/soft/guiguangbao/UCllq.apk","检测到新版本",getString(R.string.updatecontent));
    }
}

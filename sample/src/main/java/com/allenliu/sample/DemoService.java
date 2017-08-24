package com.allenliu.sample;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.allenliu.versionchecklib.core.AVersionService;

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
        //可以在判断版本之后在设置是否强制更新或者VersionParams
        //eg
        // versionParams.isForceUpdate=true;
        showVersionDialog("https://wap3.ucweb.com/files/UCBrowser/zh-cn/999/UCBrowser_V11.6.6.951_android_pf145_(Build170821133354).apk?auth_key=1504169623-0-0-9f169358664b2d4ad6e924c75e5223b1&SESSID=7906d058b83c658a754eb25a9549e6e7", "检测到新版本", getString(R.string.updatecontent));
//        or
//        showVersionDialog("http://www.apk3.com/uploads/soft/guiguangbao/UCllq.apk", "检测到新版本", getString(R.string.updatecontent),bundle);

    }
}

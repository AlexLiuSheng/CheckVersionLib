package com.allenliu.versionchecklib.core;

import android.os.Bundle;

import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;
import com.allenliu.versionchecklib.v2.ui.AllenBaseActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * 不再库里做权限处理，默认下载路径为应用下载目录
 */
public class JumpActivity extends AllenBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(true);
        return;
        // Should we show an explanation?
        // Show an expanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
        //                if(!downloadUrl.isEmpty())
        //               downloadAPK(downloadUrl,null);
        // No explanation needed, we can request the permission.
        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }

    @Override
    public void showDefaultDialog() {

    }

    @Override
    public void showCustomDialog() {

    }

    private void sendBroadcast(boolean result) {

        //post event
        CommonEvent commonEvent = new CommonEvent();
        commonEvent.setEventType(AllenEventType.START_DOWNLOAD_APK);
        commonEvent.setSuccessful(true);
        commonEvent.setData(result);
        EventBus.getDefault().post(commonEvent);

        finish();
    }



}

package com.allenliu.versionchecklib.core;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.utils.AllenEventBusUtil;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;
import com.allenliu.versionchecklib.v2.ui.AllenBaseActivity;

import org.greenrobot.eventbus.EventBus;

import static com.allenliu.versionchecklib.core.VersionDialogActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class PermissionDialogActivity extends AllenBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//                if(!downloadUrl.isEmpty())
//               downloadAPK(downloadUrl,null);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            sendBroadcast(true);
        }
    }

    @Override
    public void showDefaultDialog() {

    }

    @Override
    public void showCustomDialog() {

    }

    private void sendBroadcast(boolean result) {
        Intent intent = new Intent();
        intent.setAction(AVersionService.PERMISSION_ACTION);
        intent.putExtra("result", result);
        sendBroadcast(intent);
        //post event
        CommonEvent commonEvent=new CommonEvent();
        commonEvent.setEventType(AllenEventType.REQUEST_PERMISSION);
        commonEvent.setSuccessful(true);
        commonEvent.setData(result);
        EventBus.getDefault().post(commonEvent);

        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    sendBroadcast(true);
                } else {
                    Toast.makeText(this, getString(R.string.versionchecklib_write_permission_deny), Toast.LENGTH_LONG).show();
                    sendBroadcast(false);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}

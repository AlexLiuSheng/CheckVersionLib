package com.allenliu.versionchecklib.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.utils.ALog;

import java.io.File;

/**
 * Created by allenliu on 2017/8/8.
 */

public class ApkBroadCastReceiver extends BroadcastReceiver {
    public static String downloadApkPath;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction()) || Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            // Log.e("receiver",intent.getDataString());
            ALog.e("receiver:"+ intent.getDataString());
            String pakageDescrption = intent.getDataString();
            if (pakageDescrption.contains(context.getPackageName())) {
                if (downloadApkPath != null && !downloadApkPath.isEmpty()) {
                    String downloadPath = checkAPKExists(context);
                    if (downloadPath != null)
                        new File(downloadPath).delete();
                } else {
                    return;
                }
            }
        }
    }

    private String checkAPKExists(Context context) {
        String downloadPath = downloadApkPath + context.getString(R.string.versionchecklib_download_apkname, context.getPackageName());
        File file = new File(downloadPath);
        if (file.exists()) {
            return downloadPath;
        }
        return null;
    }
}

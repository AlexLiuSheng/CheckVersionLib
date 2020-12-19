package com.allenliu.versionchecklib.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;


import java.io.File;

public class FileHelper {
    @Deprecated
    public static String getDownloadApkCachePath() {

        String appCachePath = null;


        if (checkSDCard()) {

            appCachePath = Environment.getExternalStorageDirectory() + "/AllenVersionPath/";
        } else {
            appCachePath = Environment.getDataDirectory().getPath() + "/AllenVersionPath/";
        }
        File file = new File(appCachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return appCachePath;
    }

    public static String getDownloadApkCachePath(Context context) {
        String appCachePath;
        if (checkSDCard()) {
            if (Build.VERSION.SDK_INT >= 29) {
                appCachePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/AllenVersionPath/";
            } else {
                appCachePath = context.getExternalCacheDir() + "/AllenVersionPath/";
            }

        } else {
            appCachePath = context.getFilesDir().getAbsolutePath() + "/AllenVersionPath/";

        }


        File file = new File(appCachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return appCachePath;
    }


    /**
     *
     */
    private static boolean checkSDCard() {

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }


    public static String dealDownloadPath(@NonNull String downloadAPKPath) {
        if (!downloadAPKPath.endsWith(File.separator)) {
            downloadAPKPath += File.separator;
        }
        return downloadAPKPath;

    }
}
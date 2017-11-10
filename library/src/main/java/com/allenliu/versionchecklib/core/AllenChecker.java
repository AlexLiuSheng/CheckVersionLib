package com.allenliu.versionchecklib.core;

import android.content.Context;
import android.content.Intent;

import com.allenliu.versionchecklib.core.http.AllenHttp;

/**
 * Created by allenliu on 2017/8/15.
 */

public class AllenChecker {
    private static boolean isDebug = true;
    private static Context globalContexst;
    private static VersionParams params;

    public static void startVersionCheck(Context context, VersionParams versionParams) {
        globalContexst = context;
        params = versionParams;
        Intent intent = new Intent(context, versionParams.getService());
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        context.stopService(intent);
        context.startService(intent);
    }

    public static void init(boolean debug) {
        isDebug = debug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * cancel all the https request
     */
    public static void cancelMission() {
        AllenHttp.getHttpClient().dispatcher().cancelAll();
        if (globalContexst != null && params != null) {
            Intent intent = new Intent(globalContexst, params.getService());
            globalContexst.stopService(intent);
        }
        if (VersionDialogActivity.instance != null) {
            VersionDialogActivity.instance.finish();
        }
        globalContexst = null;
        params = null;

    }


}

package com.allenliu.versionchecklib.core;

import android.content.Context;
import android.content.Intent;

/**
 * Created by allenliu on 2017/8/15.
 */

public class AllenChecker {
    private static boolean isDebug=true;
    public static void startVersionCheck(Context context, VersionParams versionParams) {
        Intent intent = new Intent(context, versionParams.getService());
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        context.startService(intent);
    }
    public static void init(boolean debug){
        isDebug=debug;
    }
    public static boolean isDebug(){
        return isDebug;
    }




}

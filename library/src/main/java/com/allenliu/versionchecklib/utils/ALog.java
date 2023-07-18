package com.allenliu.versionchecklib.utils;

import android.util.Log;

/**
 * Created by allenliu on 2017/8/16.
 */

public class ALog {
    private static final boolean debug = true;

    public static void e(String msg) {
        if (debug) {
            if (msg != null && !msg.isEmpty())
                Log.e("Allen Checker", msg);
        }
    }
}

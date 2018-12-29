package com.allenliu.versionchecklib.v2.callback;

import android.content.Context;
import android.net.Uri;

/**
 * @author weishu
 * date 2018/12/19.
 */
public interface CustomInstallListener {
    void install(Context context, Uri apk);
}

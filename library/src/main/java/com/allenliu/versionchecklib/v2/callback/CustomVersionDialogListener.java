package com.allenliu.versionchecklib.v2.callback;

import android.app.Dialog;
import android.os.Bundle;

import com.allenliu.versionchecklib.v2.builder.UIData;

/**
 * Created by allenliu on 2018/1/18.
 */

public interface CustomVersionDialogListener {
    Dialog getCustomVersionDialog(UIData versionBundle);
}

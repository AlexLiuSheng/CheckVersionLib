package com.allenliu.forceupdatedemo;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.allenliu.versionchecklib.callback.APKDownloadListener;
import com.allenliu.versionchecklib.callback.DialogDismissListener;
import com.allenliu.versionchecklib.core.VersionDialogActivity;

import java.io.File;

public class CustomDialogActivity extends VersionDialogActivity implements APKDownloadListener, DialogDismissListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setApkDownloadListener(this);
        setDialogDimissListener(this);
    }


    @Override
    public void onDownloading(int progress) {

    }
    @Override
    public void onDownloadFail() {

    }

    /**
     * @param file
     */
    @Override
    public void onDownloadSuccess(File file) {
        forceUpdate();
    }




    @Override
    public void dialogDismiss(DialogInterface dialog) {
        forceUpdate();
    }
    private void forceUpdate() {
        //获取传过来的ebundle
        Bundle bundle = getVersionParamBundle();
        boolean isForceUpdate = bundle.getBoolean("isForceUpdate");
        if (isForceUpdate) {
            //我这里为了方便直接关闭当前activity
            //在你的项目是关闭所有界面
            finish();
            MainActivity.activity.finish();
        }
    }
}

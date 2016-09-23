package com.allenliu.versionchecklib;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class VersionDialogActivity extends AppCompatActivity {

    private AlertDialog dialog;
    /**
     * 下载中的dialog
     */
    public static AlertDialog loadingDialog;
    private String downloadUrl;
    private Activity mActivity;
    private VersionParams versionField;

    /**
     * url msg versionField
     * 第一次打开activity时会调用这个方法，显示dialog
     */
    public void initialize() {
        mActivity = this;
        String title = getString(R.string.check_new_version);
        versionField = (VersionParams) getIntent().getSerializableExtra("versionField");
        downloadUrl = getIntent().getStringExtra("url");
        String msg = getIntent().getStringExtra("msg");
        dialog = showAlert(this, title, msg, getString(R.string.confirm), updateAction, null, null, getString(R.string.cancel), cancelAction, null, false, null, null);

    }

    private void dismiss() {
        if (dialog != null)
            dialog.dismiss();
        if (loadingDialog != null)
            loadingDialog.dismiss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String msg = intent.getStringExtra("msg");
        versionField = (VersionParams) getIntent().getSerializableExtra("versionField");
        downloadUrl = intent.getStringExtra("url");
        //失败 重试
        if (versionField.getIsForceUpdate()) {
            dismiss();
            dialog = showAlert(this, getString(R.string.download_fail_retry), msg, getString(R.string.retry), updateAction, null, null, null, null, null, false, null, null);
        }
        //非强制重试
        else {
            dismiss();
            dialog = showAlert(this, getString(R.string.download_fail_retry), msg, getString(R.string.retry), updateAction, null, null, getString(R.string.cancel), cancelAction, null, false, null, null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    DialogInterface.OnClickListener updateAction = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            startVersionService(downloadUrl);
            VersionDialogActivity.this.dialog.dismiss();
            loadingDialog = showAlert(mActivity, getString(R.string.check_new_version), getString(R.string.downloading), null, null, null, null, null, null, null, false, null, null);
        }
    };
    DialogInterface.OnClickListener cancelAction = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (versionField.getIsForceUpdate())
              android.os.Process.killProcess(android.os.Process.myPid());
            else {
                //关闭service
                Intent intent = new Intent();
                intent.setClassName(VersionDialogActivity.this,versionField.getVersionServiceName());
                stopService(intent);
                finish();
            }
        }
    };

    /**
     * 开启serveice
     *
     * @param url         下载的url
     */
    private void startVersionService(String url) {
        Intent intent = new Intent();
        intent.setClassName(this,versionField.getVersionServiceName());
        intent.putExtra("url", url);
        startService(intent);
    }

    /**
     * 显示一个对话框
     *
     * @param context                    上下文对象，最好给Activity，否则需要android.permission.SYSTEM_ALERT_WINDOW
     * @param title                      标题
     * @param message                    消息
     * @param confirmButton              确认按钮
     * @param confirmButtonClickListener 确认按钮点击监听器
     * @param centerButton               中间按钮
     * @param centerButtonClickListener  中间按钮点击监听器
     * @param cancelButton               取消按钮
     * @param cancelButtonClickListener  取消按钮点击监听器
     * @param onShowListener             显示监听器
     * @param cancelable                 是否允许通过点击返回按钮或者点击对话框之外的位置关闭对话框
     * @param onCancelListener           取消监听器
     * @param onDismissListener          销毁监听器
     * @return 对话框
     */
    public static AlertDialog showAlert(Context context, String title, String message, String confirmButton, DialogInterface.OnClickListener confirmButtonClickListener, String centerButton, DialogInterface.OnClickListener centerButtonClickListener, String cancelButton, DialogInterface.OnClickListener cancelButtonClickListener, DialogInterface.OnShowListener onShowListener, boolean cancelable, DialogInterface.OnCancelListener onCancelListener, DialogInterface.OnDismissListener onDismissListener) {
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(context);
        if (title != null) {
            promptBuilder.setTitle(title);
        }
        if (message != null) {
            promptBuilder.setMessage(message);
        }
        if (confirmButton != null) {
            promptBuilder.setPositiveButton(confirmButton,
                    confirmButtonClickListener);
        }
        if (centerButton != null) {
            promptBuilder.setNeutralButton(centerButton,
                    centerButtonClickListener);
        }
        if (cancelButton != null) {
            promptBuilder.setNegativeButton(cancelButton,
                    cancelButtonClickListener);
        }
        promptBuilder.setCancelable(cancelable);
        if (cancelable) {
            promptBuilder.setOnCancelListener(onCancelListener);
        }
        AlertDialog alertDialog = promptBuilder.create();
        if (!(context instanceof Activity)) {
            alertDialog.getWindow()
                    .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        alertDialog.setOnDismissListener(onDismissListener);
        alertDialog.setOnShowListener(onShowListener);
        alertDialog.show();
        return alertDialog;
    }
}

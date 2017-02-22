package com.allenliu.versionchecklib;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Response;

public class VersionDialogActivity extends Activity {

    private AlertDialog dialog;
    public static AlertDialog loadingDialog;
    AlertDialog failDialog;
    private String downloadUrl;
    private VersionParams versionParams;
    private String title;
    private String content;
    CommitClickListener commitListener;
    CancelClickListener cancelListener;
    onDownloadSuccessListener successListener;
    onDownloadingListener loadingListener;
    boolean isUseDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    /**
     * url msg versionField
     */
    private void initialize() {
        isUseDefault = getIntent().getBooleanExtra("isUseDefault", false);
        if (isUseDefault) {
            title = getIntent().getStringExtra("title");
            content = getIntent().getStringExtra("text");
            versionParams = (VersionParams) getIntent().getSerializableExtra(AVersionService.VERSION_PARAMS_KEY);
            downloadUrl = getIntent().getStringExtra("downloadUrl");
            if (title != null && content != null && downloadUrl != null && versionParams != null)
                showVersionDialog();
        }
    }

    private void showVersionDialog() {
        showDefaultDialog();
    }

    private void showDefaultDialog() {
        dialog = new AlertDialog.Builder(this).setTitle(title).setMessage(content).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (commitListener != null)
                    commitListener.onClick();
                downloadFile(downloadUrl);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cancelListener != null)
                    cancelListener.onClick();
            }
        }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        boolean isRetry = intent.getBooleanExtra("isRetry", true);
        Log.e("isRetry", isRetry + "");
        if (isRetry) {
            downloadFile(downloadUrl);
        }
    }

    interface CommitClickListener {
        void onClick();
    }

    interface CancelClickListener {
        void onClick();
    }

    interface onDownloadingListener {
        void onDownloading(float progress);
    }

    interface onDownloadSuccessListener {
        void onDownloadSuccess(File file);
    }

    public void setCommitClickListener(CommitClickListener commitListner) {
        this.commitListener = commitListner;
    }

    public void setCancelClickListener(CancelClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setOnDownloadSuccessListener(onDownloadSuccessListener successListener) {
        this.successListener = successListener;
    }

    public void setOnDownloadingListener(onDownloadingListener downloadingListner) {
        this.loadingListener = downloadingListner;
    }

    public void downloadFile(String url) {
        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setTicker(getString(R.string.downloading));
        OkGo.get(url).execute(new FileCallback(versionParams.getDownloadAPKPath(), getString(R.string.app_name) + ".apk") {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                builder.setContentText(String.format(getString(R.string.download_progress), 0));
                Notification notification = builder.build();
                notification.vibrate = new long[]{500, 500};
                notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
                manager.notify(0, notification);
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                if (successListener != null)
                    successListener.onDownloadSuccess(file);

                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                //设置intent的类型
                i.setDataAndType(uri,
                        "application/vnd.android.package-archive");
                PendingIntent pendingIntent = PendingIntent.getActivity(VersionDialogActivity.this, 0, i, 0);
                builder.setContentIntent(pendingIntent);
                builder.setContentText(getString(R.string.download_finish));
                builder.setProgress(100, 100, false);
                manager.notify(0, builder.build());
                AppUtils.installApk(getApplicationContext(), file);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                Log.e("VersionDilaogActivity", progress + "");
                builder.setContentText(String.format(getString(R.string.download_progress), (int) (progress * 100)));
                builder.setProgress(100, (int) (progress * 100), false);
                manager.notify(0, builder.build());
                if (loadingListener != null)
                    loadingListener.onDownloading(progress);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Intent intent = new Intent(VersionDialogActivity.this, VersionDialogActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(VersionDialogActivity.this, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
                builder.setContentText(getString(R.string.download_fail));
                builder.setProgress(100, 0, false);
                manager.notify(0, builder.build());
                showFailDialog();
            }
        });
    }

    private void showFailDialog() {
        if (failDialog == null) {
            failDialog = new AlertDialog.Builder(this).setMessage(getString(R.string.download_fail_retry)).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (commitListener != null)
                        commitListener.onClick();
                    downloadFile(downloadUrl);
                }
            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (cancelListener != null)
                        cancelListener.onClick();
                }
            }).create();
            failDialog.setCanceledOnTouchOutside(false);
            failDialog.setCancelable(false);
        }
        failDialog.show();
    }
}

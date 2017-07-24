package com.allenliu.versionchecklib;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.callback.CancelClickListener;
import com.allenliu.versionchecklib.callback.CommitClickListener;
import com.allenliu.versionchecklib.callback.DownloadSuccessListener;
import com.allenliu.versionchecklib.callback.DownloadingListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public class VersionDialogActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x123;
    private AlertDialog dialog;
    public AlertDialog loadingDialog;
    AlertDialog failDialog;
    private String downloadUrl;
    private VersionParams versionParams;
    private String title;
    private String content;
    CommitClickListener commitListener;
    CancelClickListener cancelListener;
    DownloadSuccessListener successListener;
    DownloadingListener loadingListener;
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
            versionParams = getIntent().getParcelableExtra(AVersionService.VERSION_PARAMS_KEY);
            downloadUrl = getIntent().getStringExtra("downloadUrl");
            if (title != null && content != null && downloadUrl != null && versionParams != null)
                showVersionDialog();
        }
    }

    public void showVersionDialog() {
        showDefaultDialog();
    }

    private void showDefaultDialog() {
        dialog = new AlertDialog.Builder(this).setTitle(title).setMessage(content).setPositiveButton(getString(R.string.versionchecklib_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (commitListener != null)
                    commitListener.onCommitClick();
                downloadFile(downloadUrl);
            }
        }).setNegativeButton(getString(R.string.versionchecklib_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cancelListener != null)
                    cancelListener.onCancelClick();
                finish();

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


    public void setCommitClickListener(CommitClickListener commitListner) {
        this.commitListener = commitListner;
    }

    public void setCancelClickListener(CancelClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setOnDownloadSuccessListener(DownloadSuccessListener successListener) {
        this.successListener = successListener;
    }

    public void setOnDownloadingListener(DownloadingListener downloadingListner) {
        this.loadingListener = downloadingListner;
    }

    int lastProgress = 0;

    public void downloadFile(String url, FileCallback callback) {
        if (callback == null) {
            lastProgress = 0;
            final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setTicker(getString(R.string.versionchecklib_downloading));
            OkGo.get(url).execute(new FileCallback(versionParams.getDownloadAPKPath(), getString(R.string.app_name) + ".apk") {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    builder.setContentText(String.format(getString(R.string.versionchecklib_download_progress), 0));
                    Notification notification = builder.build();
                    notification.vibrate = new long[]{500, 500};
                    notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
                    manager.notify(0, notification);
                }

                @Override
                public void onSuccess(File file, Call call, Response response) {
                    if (successListener != null)
                        successListener.onDownloadSuccess(file);
                    if (loadingDialog != null)
                        loadingDialog.dismiss();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = VersionFileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".versionProvider", file);
                        Log.e("versionLib", getApplicationContext().getPackageName() + "");
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    //设置intent的类型
                    i.setDataAndType(uri,
                            "application/vnd.android.package-archive");
                    PendingIntent pendingIntent = PendingIntent.getActivity(VersionDialogActivity.this, 0, i, 0);
                    builder.setContentIntent(pendingIntent);
                    builder.setContentText(getString(R.string.versionchecklib_download_finish));
                    builder.setProgress(100, 100, false);
                    manager.notify(0, builder.build());
                    AppUtils.installApk(getApplicationContext(), file);
                    finish();

                }

                @Override
                public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                    super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                    Log.e("VersionDilaogActivity", progress + "");
                    int currentProgress = (int) (progress * 100);
                    showLoadingDialog(currentProgress);
                    if (currentProgress - lastProgress >= 5) {
                        lastProgress = currentProgress;
                        builder.setContentText(String.format(getString(R.string.versionchecklib_download_progress), lastProgress));
                        builder.setProgress(100, lastProgress, false);
                        manager.notify(0, builder.build());
                    }
                    if (loadingListener != null)
                        loadingListener.onDownloading(progress);
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    Intent intent = new Intent(VersionDialogActivity.this, VersionDialogActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(VersionDialogActivity.this, 0, intent, 0);
                    builder.setContentIntent(pendingIntent);
                    builder.setContentText(getString(R.string.versionchecklib_download_fail));
                    builder.setProgress(100, 0, false);
                    manager.notify(0, builder.build());
                    showFailDialog();
                }
            });
        } else {
            OkGo.get(url).execute(callback);
        }

    }

    public void downloadFile(String url) {
        requestPermission();
    }

    View loadingView;

    public void showLoadingDialog(int currentProgress) {
        if (loadingDialog == null) {
            loadingView = LayoutInflater.from(this).inflate(R.layout.downloading_layout, null);
            loadingDialog = new AlertDialog.Builder(this).setTitle("").setView(loadingView).create();
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }
        ProgressBar pb = (ProgressBar) loadingView.findViewById(R.id.pb);
        TextView tvProgress = (TextView) loadingView.findViewById(R.id.tv_progress);
        tvProgress.setText(String.format(getString(R.string.versionchecklib_progress), currentProgress));
        pb.setProgress(currentProgress);
        loadingDialog.show();
    }

    public void showFailDialog() {
        if (failDialog == null) {
            failDialog = new AlertDialog.Builder(this).setMessage(getString(R.string.versionchecklib_download_fail_retry)).setPositiveButton(getString(R.string.versionchecklib_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (commitListener != null)
                        commitListener.onCommitClick();
                    downloadFile(downloadUrl);
                }
            }).setNegativeButton(getString(R.string.versionchecklib_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (cancelListener != null)
                        cancelListener.onCancelClick();
                    finish();
                }
            }).create();
            failDialog.setCanceledOnTouchOutside(false);
            failDialog.setCancelable(false);
        }
        failDialog.show();
    }

    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//                if(!downloadUrl.isEmpty())
//               downloadFile(downloadUrl,null);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            if(!downloadUrl.isEmpty())
                downloadFile(downloadUrl,null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(!downloadUrl.isEmpty())
                        downloadFile(downloadUrl,null);
                } else {
                    Toast.makeText(this,getString(R.string.versionchecklib_write_permission_deny),Toast.LENGTH_LONG).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

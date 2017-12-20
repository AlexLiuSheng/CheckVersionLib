package com.allenliu.versionchecklib.core;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationCompat;
//import android.support.v7.app.NotificationCompat;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.core.http.FileCallBack;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.utils.AppUtils;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by allenliu on 2017/8/16.
 */

public class DownloadManager {
    private static int lastProgress = 0;
    private static boolean isDownloadSuccess = false;

    //   private static final int TASK=Intent.FLAG_ACTIVITY_CLEAR_TOP;
    public static void downloadAPK(final Context context, final String url, final VersionParams versionParams, final DownloadListener listener) {
        lastProgress = 0;
        isDownloadSuccess = false;
        if (url == null || url.isEmpty()) {
            return;
        }
        String downloadPath = versionParams.getDownloadAPKPath() + context.getString(R.string.versionchecklib_download_apkname, context.getPackageName());
        //静默下载也判断本地是否有缓存
        if (versionParams.isSilentDownload()) {

            if (!versionParams.isForceRedownload()) {
                //判断本地文件是否存在
                if (checkAPKIsExists(context, downloadPath)) {
                    if (listener != null)
                        listener.onCheckerDownloadSuccess(new File(downloadPath));
                    return;
                }
                silentDownloadAPK(context, url, versionParams, listener);

            } else
                silentDownloadAPK(context, url, versionParams, listener);
            return;
        }

        if (!versionParams.isForceRedownload()) {
            //判断本地文件是否存在
            if (checkAPKIsExists(context, downloadPath)) {
                if (listener != null)
                    listener.onCheckerDownloadSuccess(new File(downloadPath));
                AppUtils.installApk(context, new File(downloadPath));
                if (context instanceof Activity)
                    ((Activity) context).finish();
                return;

            }
        }
        if (listener != null)
            listener.onCheckerStartDownload();
        NotificationCompat.Builder builder = null;
        NotificationManager manager = null;
        Notification notification;
        if (versionParams.isShowNotification()) {
            manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            builder = createNotification(context);
            manager.notify(0, builder.build());

        }
        final NotificationCompat.Builder finalBuilder = builder;
        final NotificationManager finalManager = manager;
        Request request = new Request.Builder().url(url).build();
        AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName())) {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                listener.onCheckerDownloadSuccess(file);
                isDownloadSuccess = true;
                if (versionParams.isShowNotification()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
                        ALog.e(context.getPackageName() + "");
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    ALog.e("APK download Success");
                    //设置intent的类型
                    i.setDataAndType(uri,
                            "application/vnd.android.package-archive");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
                    finalBuilder.setContentIntent(pendingIntent);
                    finalBuilder.setContentText(context.getString(R.string.versionchecklib_download_finish));
                    finalBuilder.setProgress(100, 100, false);
                    finalManager.cancelAll();
                    finalManager.notify(0, finalBuilder.build());
                }

                AppUtils.installApk(context, file);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onDownloading(int progress) {
                ALog.e("downloadProgress:" + progress + "");
                int currentProgress = progress;
//                showLoadingDialog(currentProgress);
                listener.onCheckerDownloading(currentProgress);
                if (currentProgress - lastProgress >= 5) {
                    lastProgress = currentProgress;
                    if (versionParams.isShowNotification() && !isDownloadSuccess) {
                        finalBuilder.setContentIntent(null);
                        finalBuilder.setContentText(String.format(context.getString(R.string.versionchecklib_download_progress), lastProgress));
                        finalBuilder.setProgress(100, lastProgress, false);
//                        finalBuilder.setDefaults(0);
                        finalManager.notify(0, finalBuilder.build());
                    }
                }
            }

            @Override
            public void onDownloadFailed() {
                if (versionParams.isShowNotification()) {
                    Intent intent = new Intent(context, versionParams.getCustomDownloadActivityClass());
                    intent.putExtra("isRetry", true);
//                intent.putExtra(AVersionService.VERSION_PARAMS_KEY, paramBundle);
                    intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
                    intent.putExtra("downloadUrl", url);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
                    finalBuilder.setContentIntent(pendingIntent);
                    finalBuilder.setContentText(context.getString(R.string.versionchecklib_download_fail));
                    finalBuilder.setProgress(100, 0, false);
                    finalManager.notify(0, finalBuilder.build());
                }
                ALog.e("file download failed");
//                showFailDialog();
                listener.onCheckerDownloadFail();

            }
        });
    }


    private static void silentDownloadAPK(final Context context, String url, final VersionParams versionParams, final DownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        if (listener != null)
            listener.onCheckerStartDownload();
        AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName())) {


            @Override
            public void onSuccess(File file, Call call, Response response) {
                listener.onCheckerDownloadSuccess(file);

            }

            @Override
            public void onDownloading(int progress) {
                ALog.e("silent downloadProgress:" + progress + "");
                int currentProgress = progress;
//                showLoadingDialog(currentProgress);
                if (currentProgress - lastProgress >= 5) {
                    lastProgress = currentProgress;
                }
                listener.onCheckerDownloading(currentProgress);
            }

            @Override
            public void onDownloadFailed() {
                ALog.e("file silent download failed");
//                showFailDialog();
                listener.onCheckerDownloadFail();
            }
        });
    }

    public static boolean checkAPKIsExists(Context context, String downloadPath) {
        File file = new File(downloadPath);
        boolean result = false;
        if (file.exists()) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(downloadPath,
                        PackageManager.GET_ACTIVITIES);
                //判断安装包存在并且包名一样并且版本号不一样
                ALog.e("本地安装包版本号：" + info.versionCode + "\n 当前app版本号：" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
                if (info != null && context.getPackageName().equalsIgnoreCase(info.packageName) && context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode != info.versionCode) {
                    result = true;
                }
            } catch (Exception e) {
                result = false;
            }
        }
        return result;

    }

    private static NotificationCompat.Builder createNotification(Context context) {
        final String CHANNEL_ID = "0", CHANNEL_NAME = "ALLEN_NOTIFICATION";
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);


        }
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setOnlyAlertOnce(true);
        builder.setContentTitle(context.getString(R.string.app_name));
//        builder.setSound(null);

        builder.setTicker(context.getString(R.string.versionchecklib_downloading));
        builder.setContentText(String.format(context.getString(R.string.versionchecklib_download_progress), 0));

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        return builder;
    }
}

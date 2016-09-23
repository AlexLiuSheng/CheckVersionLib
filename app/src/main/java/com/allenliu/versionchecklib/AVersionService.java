package com.allenliu.versionchecklib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;


import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Request;


public abstract class AVersionService extends Service {
    public static final int POST = 1;
    public static final int GET = 2;
    Notification notification;
    private float currentProgress = 0;
    VersionParams versionField;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //版本检测需要的字段
        VersionParams versionField2 = (VersionParams) intent.getSerializableExtra("versionField");
        if (versionField2 != null)
            versionField = versionField2;
        //下载所需要的url
        String url = intent.getStringExtra("url");
        //下载文件 并且升级
        if (!TextUtils.isEmpty(url) && versionField2 == null) {
            downloadFile(url);
        } else {
            versionCheck(versionField);
        }
        return super.onStartCommand(intent, flags, startId);
    }
   public abstract void onResponses(AVersionService service, String response);
    /**
     * 请求版本
     *
     * @param versionField
     * @param
     */
    protected void versionCheck(final VersionParams versionField) {
        String url = versionField.getRequestUrl();
        int requestMehod = versionField.getRequestMethod();
        Map<String,Object>params=versionField.getRequestParams();
        if (requestMehod == AVersionService.GET) {
            GetBuilder builder= OkHttpUtils.get().url(url);
            if(params!=null){
                Set<String>sets=params.keySet();
               Iterator<String> iterator=sets.iterator();
                while (iterator.hasNext()){
                    String key=iterator.next();
                    Object value=params.get(key);
                    builder.addParams(key,value+"");
                }
            }
           builder .build().execute(requestCallBack);
        } else {
            PostFormBuilder builder= OkHttpUtils.post().url(url);
            if(params!=null){
                Set<String>sets=params.keySet();
                Iterator<String> iterator=sets.iterator();
                while (iterator.hasNext()){
                    String key=iterator.next();
                    Object value=params.get(key);
                    builder.addParams(key,value+"");
                }
            }
            builder .build().execute(requestCallBack);
        }
    }
  StringCallback requestCallBack=new StringCallback() {
      @Override
      public void onError(Call call, Exception e, int id) {
          //请求失败,间隔10s继续请求
          //  Thred.sleep(1000*10);
          new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                  startVersionService(null);
              }
          },  versionField.getPauseRequestTime());
      }

      @Override
      public void onResponse(String response, int id) {
          onResponses(AVersionService.this,response);
      }
  };
    /**
     * 开启dialogactivity 显示dialog
     *
     * @param updateMsg
     */
    public void showVersionDialog(String downloadUrl, String updateMsg) {
        Intent intent = new Intent(getApplicationContext(), VersionDialogActivity.class);
        if (updateMsg != null)
            intent.putExtra("msg", updateMsg);
        if (downloadUrl != null)
            intent.putExtra("url", downloadUrl);
        intent.putExtra("versionField", versionField);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    /**
     * 开启serveice
     *
     * @param url 下载的url
     */
    private void startVersionService(String url) {
        Intent intent = new Intent();
        intent.setClassName(this,versionField.getVersionServiceName());
        intent.putExtra("url", url);
        intent.putExtra("versionField", versionField);
        startService(intent);
    }

    /**
     * 下载文件
     *
     * @param url
     */
    private void downloadFile(final String url) {
        final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(AVersionService.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setTicker(getString(R.string.downloading));
        String filename = "/" + System.currentTimeMillis() + ".apk";
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(FileHelper.getDownloadApkCachePath(getApplicationContext()), filename)//
                {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        builder.setContentText(String.format(getString(R.string.download_progress), 0f));
                        currentProgress = 0;
                        notification = builder.build();
                        notification.vibrate = new long[]{500, 500};
                        notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
                        manager.notify(0, notification);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        currentProgress = 0;
                        builder.setContentText(getString(R.string.download_fail));
                        manager.notify(0, builder.build());
                        showVersionDialog(url, getString(R.string.download_fail_retry));
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        //不是强制更新 就直接dismiss掉
                        if (!versionField.getIsForceUpdate()) {
                            if (VersionDialogActivity.loadingDialog != null)
                                VersionDialogActivity.loadingDialog.dismiss();
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(response);
                        //设置intent的类型
                        i.setDataAndType(uri,
                                "application/vnd.android.package-archive");
                        PendingIntent pendingIntent = PendingIntent.getActivity(AVersionService.this, 0, i, 0);
                        builder.setContentIntent(pendingIntent);
                        builder.setContentText(getString(R.string.download_finish));
                        builder.setProgress(100, 100, false);
                        manager.notify(0, builder.build());
                        AppUtils.installApk(getApplicationContext(), response);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        //计算每百分之5刷新一下通知栏
                        float progress2 = progress * 100;
                        if (progress2 - currentProgress > 5) {
                            currentProgress = progress2;
                            builder.setContentText(String.format(getString(R.string.download_progress), progress2));
                            builder.setProgress(100, (int) progress2, false);
                            manager.notify(0, builder.build());
                        }
                    }
                });

    }

}

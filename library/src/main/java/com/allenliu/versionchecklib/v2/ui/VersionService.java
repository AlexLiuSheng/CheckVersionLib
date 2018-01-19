package com.allenliu.versionchecklib.v2.ui;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.JobIntentService;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.core.DownloadManager;
import com.allenliu.versionchecklib.core.PermissionDialogActivity;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.utils.AllenEventBusUtil;
import com.allenliu.versionchecklib.utils.AppUtils;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.RequestVersionBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;
import com.allenliu.versionchecklib.v2.net.DownloadMangerV2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class VersionService extends Service {
    private static final int JOB_ID = 100011;
    public static DownloadBuilder builder;
    private BuilderHelper builderHelper;
    private NotificationHelper notificationHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        builderHelper = new BuilderHelper(getApplicationContext(), builder);
        notificationHelper = new NotificationHelper(getApplicationContext(), builder);
        new Thread() {
            public void run() {
                onHandleWork();
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ALog.e("service destroy");
        builder = null;
        builderHelper = null;
        notificationHelper = null;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void enqueueWork(Context context, DownloadBuilder downloadBuilder) {
        builder = downloadBuilder;
        Intent intent = new Intent(context, VersionService.class);
        context.startService(intent);
//        enqueueWork(context, VersionService.class, JOB_ID, new Intent());
    }


    protected void onHandleWork() {
        if (checkWhetherNeedRequestVersion()) {
            requestVersion();
        } else {
            downloadAPK();
        }
    }

    /**
     * 请求版本接口
     */
    private void requestVersion() {
        RequestVersionBuilder requestVersionBuilder = builder.getRequestVersionBuilder();
        OkHttpClient client = AllenHttp.getHttpClient();
        HttpRequestMethod requestMethod = requestVersionBuilder.getRequestMethod();
        Request request = null;
        switch (requestMethod) {
            case GET:
                request = AllenHttp.get(requestVersionBuilder).build();
                break;
            case POST:
                request = AllenHttp.post(requestVersionBuilder).build();
                break;
            case POSTJSON:
                request = AllenHttp.postJson(requestVersionBuilder).build();
                break;
        }
        RequestVersionListener requestVersionListener = requestVersionBuilder.getRequestVersionListener();
        if (requestVersionListener != null) {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    UIData versionBundle = requestVersionListener.onRequestVersionSuccess(result);
                    builder.setVersionBundle(versionBundle);
                    downloadAPK();
                } else {
                    AllenVersionChecker.getInstance().cancelAllMission(getApplicationContext());
                    requestVersionListener.onRequestVersionFailure(response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
                AllenVersionChecker.getInstance().cancelAllMission(getApplicationContext());
                requestVersionListener.onRequestVersionFailure(e.getMessage());

            }
        } else {
            throw new RuntimeException("using request version function,you must set a requestVersionListener");
        }
    }

    private boolean checkWhetherNeedRequestVersion() {
        if (builder.getRequestVersionBuilder() != null)
            return true;
        else
            return false;
    }

    private void downloadAPK() {
        if (builder.isSilentDownload()) {
            requestPermissionAndDownload();
        } else {
            showVersionDialog();
        }
    }


    /**
     * 开启UI展示界面
     */
    private void showVersionDialog() {
        Intent intent = new Intent(this, UIActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showDownloadingDialog() {
        if (builder.isShowDownloadingDialog()) {
            Intent intent = new Intent(this, DownloadingActivity.class);
//            intent.putExtra(DownloadingActivity.PROGRESS, progress);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    private void updateDownloadingDialogProgress(int progress) {
        CommonEvent commonEvent = new CommonEvent();
        commonEvent.setEventType(AllenEventType.UPDATE_DOWNLOADING_PROGRESS);
        commonEvent.setData(progress);
        commonEvent.setSuccessful(true);
        EventBus.getDefault().post(commonEvent);
    }
    private void showDownloadFailedDialog() {
        Intent intent = new Intent(this, DownloadFailedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void requestPermissionAndDownload() {
        Intent intent = new Intent(this, PermissionDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void install() {
        AllenEventBusUtil.sendEventBus(AllenEventType.DOWNLOAD_COMPLETE);
        final String downloadPath = builder.getDownloadAPKPath() + getString(R.string.versionchecklib_download_apkname, getPackageName());
        if (builder.isSilentDownload()) {
            showVersionDialog();
        } else {
            builderHelper.checkForceUpdate();
            AppUtils.installApk(getApplicationContext(), new File(downloadPath));
        }
    }

    @WorkerThread
    private void startDownloadApk() {
        //判断是否缓存并且是否强制重新下载
        final String downloadPath = builder.getDownloadAPKPath() + getString(R.string.versionchecklib_download_apkname, getPackageName());
        if (DownloadManager.checkAPKIsExists(getApplicationContext(), downloadPath) && !builder.isForceRedownload()) {
            ALog.e("using cache");
            install();
            return;
        }
        builderHelper.checkAndDeleteAPK();
        DownloadMangerV2.download(builder.getDownloadUrl(), builder.getDownloadAPKPath(), getString(R.string.versionchecklib_download_apkname, getPackageName()), new DownloadListener() {
            @Override
            public void onCheckerDownloading(int progress) {
                notificationHelper.updateNotification(progress);
                updateDownloadingDialogProgress(progress);
            }

            @Override
            public void onCheckerDownloadSuccess(File file) {
                notificationHelper.showDownloadCompleteNotifcation(file);
                install();
            }

            @Override
            public void onCheckerDownloadFail() {
                if (!builder.isSilentDownload() && builder.isShowDownloadFailDialog()) {
                    showDownloadFailedDialog();
                }
                notificationHelper.showDownloadFailedNotification();
            }

            @Override
            public void onCheckerStartDownload() {
                ALog.e("start download apk");
                notificationHelper.showNotification();
                showDownloadingDialog();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(CommonEvent commonEvent) {
        switch (commonEvent.getEventType()) {
            case AllenEventType.START_DOWNLOAD_APK:
                requestPermissionAndDownload();
                break;
            case AllenEventType.REQUEST_PERMISSION:
                boolean permissionResult = (boolean) commonEvent.getData();
                if (permissionResult)
                    startDownloadApk();
                else
                    stopSelf();
                break;
        }

    }
}

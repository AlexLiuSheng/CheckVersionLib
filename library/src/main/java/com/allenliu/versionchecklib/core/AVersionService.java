package com.allenliu.versionchecklib.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.utils.ALog;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public abstract class AVersionService extends Service implements DownloadListener {
    protected VersionParams versionParams;
    public static final String VERSION_PARAMS_KEY = "VERSION_PARAMS_KEY";
    public static final String VERSION_PARAMS_EXTRA_KEY = "VERSION_PARAMS_EXTRA_KEY";
    public static final String PERMISSION_ACTION = "com.allenliu.versionchecklib.filepermisssion.action";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null) {
                versionParams = intent.getParcelableExtra(VERSION_PARAMS_KEY);
                verfiyAndDeleteAPK();
                if (versionParams.isOnlyDownload()) {
                    showVersionDialog(versionParams.getDownloadUrl(), versionParams.getTitle(), versionParams.getUpdateMsg(), versionParams.getParamBundle());
                } else {
                    requestVersionUrlSync();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 验证安装包是否存在，并且在安装成功情况下删除安装包
     */
    private void verfiyAndDeleteAPK() {
        //判断versioncode与当前版本不一样的apk是否存在，存在删除安装包
        try {
            String downloadPath = versionParams.getDownloadAPKPath() + getApplicationContext().getString(R.string.versionchecklib_download_apkname, getApplicationContext().getPackageName());
            if (!DownloadManager.checkAPKIsExists(getApplicationContext(), downloadPath)) {
                ALog.e("删除本地apk");
                new File(downloadPath).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestVersionUrlSync() {
        requestVersionUrl();
    }

    public abstract void onResponses(AVersionService service, String response);

    Callback stringCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            pauseRequest();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                final String result = response.body().string();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onResponses(AVersionService.this, result);

                    }
                });
            } else {
                pauseRequest();
            }
        }

    };

    /**
     * 间隔请求
     */
    private void pauseRequest() {
        long pauseTime = versionParams.getPauseRequestTime();
        //不为-1 间隔请求
        if (pauseTime > 0) {
            ALog.e("请求版本接口失败，下次请求将在" + pauseTime + "ms后开始");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestVersionUrlSync();
                }
            }, pauseTime);
        }
    }

    private void requestVersionUrl() {
        OkHttpClient client = AllenHttp.getHttpClient();
        HttpRequestMethod requestMethod = versionParams.getRequestMethod();
        Request request = null;
        switch (requestMethod) {
            case GET:
                request = AllenHttp.get(versionParams).build();
                break;
            case POST:
                request = AllenHttp.post(versionParams).build();
                break;
            case POSTJSON:
                request = AllenHttp.postJson(versionParams).build();
                break;
        }
        client.newCall(request).enqueue(stringCallback);
    }


    String downloadUrl, title, updateMsg;
    Bundle paramBundle;

    public void showVersionDialog(String downloadUrl, String title, String updateMsg) {
        showVersionDialog(downloadUrl, title, updateMsg, null);
    }

    public void showVersionDialog(String downloadUrl, String title, String updateMsg, Bundle paramBundle) {
        this.downloadUrl = downloadUrl;
        this.title = title;
        this.updateMsg = updateMsg;
        this.paramBundle = paramBundle;
        if (versionParams.isSilentDownload()) {
            BroadcastReceiver receiver = new VersionBroadCastReceiver();
            IntentFilter intentFilter = new IntentFilter(PERMISSION_ACTION);
            registerReceiver(receiver, intentFilter);
            Intent intent = new Intent(this, PermissionDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            silentDownload();
        } else {
            goToVersionDialog();
        }
    }

    private void silentDownload() {
        DownloadManager.downloadAPK(downloadUrl, versionParams, this);
    }

    @Override
    public void onCheckerDownloading(int progress) {

    }

    @Override
    public void onCheckerStartDownload() {

    }

    @Override
    public void onCheckerDownloadSuccess(File file) {
        goToVersionDialog();
    }

    @Override
    public void onCheckerDownloadFail() {
        stopSelf();
    }

    private void goToVersionDialog() {
        Intent intent = new Intent(getApplicationContext(), versionParams.getCustomDownloadActivityClass());
        if (updateMsg != null)
            intent.putExtra("text", updateMsg);
        if (downloadUrl != null)
            intent.putExtra("downloadUrl", downloadUrl);
        if (title != null)
            intent.putExtra("title", title);
        if (paramBundle != null)
            versionParams.setParamBundle(paramBundle);
        intent.putExtra(VERSION_PARAMS_KEY, versionParams);
//        if (paramBundle != null)
//            intent.putExtra(VERSION_PARAMS_EXTRA_KEY, paramBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    public void setVersionParams(VersionParams versionParams) {
        this.versionParams = versionParams;
    }

    public class VersionBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PERMISSION_ACTION)) {


                boolean result = intent.getBooleanExtra("result", false);
                if (result){
                    silentDownload();

                }
                unregisterReceiver(this);
            }
        }
    }
}

package com.allenliu.versionchecklib.core;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;

import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.utils.ALog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Response;

import static java.util.logging.Level.SEVERE;


public abstract class AVersionService extends Service implements DownloadListener {
    protected VersionParams versionParams;
    public static final String VERSION_PARAMS_KEY = "VERSION_PARAMS_KEY";
    public static final String VERSION_PARAMS_EXTRA_KEY = "VERSION_PARAMS_EXTRA_KEY";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        versionParams = intent.getParcelableExtra(VERSION_PARAMS_KEY);
        requestVersionUrlSync();
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestVersionUrlSync() {
        requestVersionUrl();
    }

    public abstract void onResponses(AVersionService service, String response);

    StringCallback stringCallback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            onResponses(AVersionService.this, s);
        }

        @Override
        public void onError(Call call, Response response, Exception e) {
            long pauseTime = versionParams.getPauseRequestTime();
            //不为-1 间隔请求
            if (pauseTime > 0) {
                ALog.e("请求版本接口失败，下次请求将在" + pauseTime + "ms后开始");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestVersionUrlSync();
                    }
                }, pauseTime);
            }

        }
    };

    private void requestVersionUrl() {
        OkGo.init(getApplication());
        OkGo.getInstance().debug("Allen Checker", SEVERE, true);
        String url = versionParams.getRequestUrl();
        HttpRequestMethod requestMethod = versionParams.getRequestMethod();
        HttpParams params = versionParams.getRequestParams();
        HttpHeaders headers = versionParams.getHttpHeaders();
        switch (requestMethod) {
            case GET:
                OkGo.get(url).params(params).headers(headers).execute(stringCallback);
                break;
            case POST:
                OkGo.post(url).params(params).headers(headers).execute(stringCallback);
                break;
            case POSTJSON:
                String json = getRequestParams(params);
                if (json != null)
                    OkGo.post(url).upJson(json).headers(headers).execute(stringCallback);
                else
                    OkGo.post(url).headers(headers).execute(stringCallback);
                break;
        }


    }

    private String getRequestParams(HttpParams params) {
        String json;
        JSONObject jsonObject = new JSONObject();
        for (ConcurrentHashMap.Entry<String, List<String>> entry : params.urlParamsMap.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue().get(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        json = jsonObject.toString();
        return json;
    }

    String downloadUrl, title, updateMsg;
    Bundle paramBundle;

    public void showVersionDialog(String downloadUrl, String title, String updateMsg) {
     showVersionDialog(downloadUrl,title,updateMsg,null);
    }

    public void showVersionDialog(String downloadUrl, String title, String updateMsg,Bundle paramBundle) {
        this.downloadUrl = downloadUrl;
        this.title = title;
        this.updateMsg = updateMsg;
        this.paramBundle=paramBundle;
        if (versionParams.isSilentDownload()) {
            silentDownload();
        } else {
            goToVersionDialog();
        }
    }

        private void silentDownload() {
        DownloadManager.downloadAPK(getApplicationContext(), downloadUrl, versionParams, this);
    }

    @Override
    public void onCheckerDownloading(int progress) {

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
        intent.putExtra(VERSION_PARAMS_KEY, versionParams);
        if(paramBundle!=null)
        intent.putExtra(VERSION_PARAMS_EXTRA_KEY,paramBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    public void setVersionParams(VersionParams versionParams) {
        this.versionParams = versionParams;
    }
}

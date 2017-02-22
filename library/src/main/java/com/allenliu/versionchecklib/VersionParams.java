package com.allenliu.versionchecklib;

import android.widget.Button;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Allen Liu on 2016/9/22.
 */

public class VersionParams implements Serializable {
    String versionServiceName;
    String requestUrl;
    /**
     * 下载保存地址
     */
    String downloadAPKPath=FileHelper.getDownloadApkCachePath();
   HttpHeaders httpHeaders=new HttpHeaders();
    long pauseRequestTime = 30 * 1000;
    HttpRequestMethod requestMethod=HttpRequestMethod.POST;
   HttpParams requestParams=new HttpParams();

    public String getVersionServiceName() {
        return versionServiceName;
    }

    public VersionParams setVersionServiceName(String versionServiceName) {
        this.versionServiceName = versionServiceName;
        return this;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public VersionParams setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public String getDownloadAPKPath() {
        return downloadAPKPath;
    }

    public VersionParams setDownloadAPKPath(String downloadAPKPath) {
        this.downloadAPKPath = downloadAPKPath;
        return this;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public VersionParams setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
        return this;
    }

    public long getPauseRequestTime() {
        return pauseRequestTime;
    }

    public VersionParams setPauseRequestTime(long pauseRequestTime) {
        this.pauseRequestTime = pauseRequestTime;
        return this;
    }

    public HttpRequestMethod getRequestMethod() {
        return requestMethod;
    }

    public VersionParams setRequestMethod(HttpRequestMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public HttpParams getRequestParams() {
        return requestParams;
    }

    public VersionParams setRequestParams(HttpParams requestParams) {
        this.requestParams = requestParams;
        return this;
    }
}

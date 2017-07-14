package com.allenliu.versionchecklib;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Allen Liu on 2016/9/22.
 */

public class VersionParams implements Parcelable {

    private String requestUrl;
    /**
     * 下载保存地址
     */
    private String downloadAPKPath = FileHelper.getDownloadApkCachePath();
    private HttpHeaders httpHeaders = new HttpHeaders();
    private long pauseRequestTime = 30 * 1000;
    private HttpRequestMethod requestMethod = HttpRequestMethod.POST;
    private HttpParams requestParams = new HttpParams();
    private Class customDownloadActivityClass = VersionDialogActivity.class;

    public Class getCustomDownloadActivityClass() {
        return customDownloadActivityClass;
    }

    public VersionParams setCustomDownloadActivityClass(Class customDownloadActivityClass) {
        this.customDownloadActivityClass = customDownloadActivityClass;
        return this;
    }

//    public Class getVersionService() {
//        return versionServiceClass;
//    }
//
//    public VersionParams setVersionService(Class versionServiceClass) {
//        this.versionServiceClass = versionServiceClass;
//        return this;
//    }

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.requestUrl);
        dest.writeString(this.downloadAPKPath);
        dest.writeSerializable(this.httpHeaders);
        dest.writeLong(this.pauseRequestTime);
        dest.writeInt(this.requestMethod == null ? -1 : this.requestMethod.ordinal());
        dest.writeSerializable(this.requestParams);
        dest.writeSerializable(this.customDownloadActivityClass);
    }

    public VersionParams() {
    }

    protected VersionParams(Parcel in) {
        this.requestUrl = in.readString();
        this.downloadAPKPath = in.readString();
        this.httpHeaders = (HttpHeaders) in.readSerializable();
        this.pauseRequestTime = in.readLong();
        int tmpRequestMethod = in.readInt();
        this.requestMethod = tmpRequestMethod == -1 ? null : HttpRequestMethod.values()[tmpRequestMethod];
        this.requestParams = (HttpParams) in.readSerializable();
        this.customDownloadActivityClass = (Class) in.readSerializable();
    }

    public static final Parcelable.Creator<VersionParams> CREATOR = new Parcelable.Creator<VersionParams>() {
        @Override
        public VersionParams createFromParcel(Parcel source) {
            return new VersionParams(source);
        }

        @Override
        public VersionParams[] newArray(int size) {
            return new VersionParams[size];
        }
    };
}

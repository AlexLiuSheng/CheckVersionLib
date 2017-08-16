package com.allenliu.versionchecklib;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import okhttp3.internal.http.HttpMethod;

/**
 * Created by allenliu on 2017/8/15.
 */

public class VersionParams implements Parcelable {
    private String requestUrl;
    private String downloadAPKPath;
    private HttpHeaders httpHeaders;
    private long pauseRequestTime;
    private HttpRequestMethod requestMethod;
    private HttpParams requestParams;
    private Class customDownloadActivityClass;
    private boolean isForceUpdate;
    private boolean isForceRedownload;
    private boolean isSilentDownload;
    private Class<? extends AVersionService> service;

    private VersionParams(String requestUrl, String downloadAPKPath, HttpHeaders httpHeaders, long pauseRequestTime, HttpRequestMethod requestMethod, HttpParams requestParams, Class customDownloadActivityClass, boolean isForceUpdate, boolean isForceRedownload, boolean isSilentDownload, Class<? extends AVersionService> service) {
        this.requestUrl = requestUrl;
        this.downloadAPKPath = downloadAPKPath;
        this.httpHeaders = httpHeaders;
        this.pauseRequestTime = pauseRequestTime;
        this.requestMethod = requestMethod;
        this.requestParams = requestParams;
        this.customDownloadActivityClass = customDownloadActivityClass;
        this.isForceUpdate = isForceUpdate;
        this.isForceRedownload = isForceRedownload;
        this.isSilentDownload = isSilentDownload;
        this.service = service;
        if (this.service == null) {
            throw new RuntimeException("you must define your service which extends AVService.");
        }
        if (requestUrl == null) {
            throw new RuntimeException("requestUrl is needed.");

        }
    }

    public Class<? extends AVersionService> getService() {
        return service;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getDownloadAPKPath() {
        return downloadAPKPath;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public long getPauseRequestTime() {
        return pauseRequestTime;
    }

    public HttpRequestMethod getRequestMethod() {
        return requestMethod;
    }

    public HttpParams getRequestParams() {
        return requestParams;
    }

    public Class getCustomDownloadActivityClass() {
        return customDownloadActivityClass;
    }

    public boolean isForceUpdate() {
        return isForceUpdate;
    }

    public boolean isForceRedownload() {
        return isForceRedownload;
    }

    public boolean isSilentDownload() {
        return isSilentDownload;
    }

    public static class Builder {
        private String requestUrl;
        private String downloadAPKPath;
        private HttpHeaders httpHeaders;
        private long pauseRequestTime;
        private HttpRequestMethod requestMethod;
        private HttpParams requestParams;
        private Class customDownloadActivityClass;
        private boolean isForceUpdate;
        private boolean isForceRedownload;
        private boolean isSilentDownload;
        private Class<? extends AVersionService> service;

        public Builder() {
            this.downloadAPKPath = FileHelper.getDownloadApkCachePath();
            this.pauseRequestTime = 1000 * 30;
            this.requestMethod = HttpRequestMethod.GET;
            this.customDownloadActivityClass = VersionDialogActivity.class;
            this.isForceUpdate = false;
            this.isForceRedownload = false;
            this.isSilentDownload = false;
        }

        public Builder setRequestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
            return this;
        }

        public Builder setDownloadAPKPath(String downloadAPKPath) {
            this.downloadAPKPath = downloadAPKPath;
            return this;
        }

        public Builder setHttpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder setPauseRequestTime(long pauseRequestTime) {
            this.pauseRequestTime = pauseRequestTime;
            return this;
        }

        public Builder setRequestMethod(HttpRequestMethod requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder setRequestParams(HttpParams requestParams) {
            this.requestParams = requestParams;
            return this;
        }

        public Builder setCustomDownloadActivityClass(Class customDownloadActivityClass) {
            this.customDownloadActivityClass = customDownloadActivityClass;
            return this;
        }

        public Builder setForceUpdate(boolean forceUpdate) {
            isForceUpdate = forceUpdate;
            return this;
        }

        public Builder setForceRedownload(boolean forceRedownload) {
            isForceRedownload = forceRedownload;
            return this;
        }

        public Builder setSilentDownload(boolean silentDownload) {
            isSilentDownload = silentDownload;
            return this;
        }

        public Builder setService(Class<? extends AVersionService> service) {
            this.service = service;
            return this;
        }

        public VersionParams build() {
            return new VersionParams(requestUrl, downloadAPKPath, httpHeaders, pauseRequestTime, requestMethod, requestParams, customDownloadActivityClass, isForceUpdate, isForceRedownload, isSilentDownload, service);
        }
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
        dest.writeByte(this.isForceUpdate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isForceRedownload ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSilentDownload ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.service);
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
        this.isForceUpdate = in.readByte() != 0;
        this.isForceRedownload = in.readByte() != 0;
        this.isSilentDownload = in.readByte() != 0;
        this.service = (Class<? extends AVersionService>) in.readSerializable();
    }

    public static final Creator<VersionParams> CREATOR = new Creator<VersionParams>() {
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

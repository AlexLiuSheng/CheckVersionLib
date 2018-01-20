package com.allenliu.versionchecklib.core;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.allenliu.versionchecklib.core.http.HttpHeaders;
import com.allenliu.versionchecklib.core.http.HttpParams;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.utils.FileHelper;


/**
 * Created by allenliu on 2017/8/15.
 *
 */
@Deprecated
public class VersionParams implements Parcelable {
    private String requestUrl;
    private String downloadAPKPath;
    private com.allenliu.versionchecklib.core.http.HttpHeaders httpHeaders;
    private long pauseRequestTime;
    private HttpRequestMethod requestMethod;
    private HttpParams requestParams;
    private Class<? extends VersionDialogActivity> customDownloadActivityClass;
    //    public boolean isForceUpdate;
    public boolean isForceRedownload;
    public boolean isSilentDownload;
    private Class<? extends AVersionService> service;
    private boolean onlyDownload;
    private String title;
    private String downloadUrl;
    private String updateMsg;
    private Bundle paramBundle;
    private boolean isShowDownloadingDialog;
    private boolean isShowNotification;
    private boolean isShowDownloadFailDialog;

    public boolean isShowDownloadingDialog() {
        return isShowDownloadingDialog;
    }

    public boolean isShowDownloadFailDialog() {
        return isShowDownloadFailDialog;
    }

    public boolean isShowNotification() {
        return isShowNotification;
    }

    public String getTitle() {
        return title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public Bundle getParamBundle() {
        return paramBundle;
    }

    private VersionParams() {
    }


    public VersionParams(String requestUrl, String downloadAPKPath, HttpHeaders httpHeaders, long pauseRequestTime, HttpRequestMethod requestMethod, HttpParams requestParams, Class<? extends VersionDialogActivity> customDownloadActivityClass, boolean isForceRedownload, boolean isSilentDownload, Class<? extends AVersionService> service, boolean onlyDownload, String title, String downloadUrl, String updateMsg, Bundle paramBundle) {
        this.requestUrl = requestUrl;
        this.downloadAPKPath = downloadAPKPath;
        this.httpHeaders = httpHeaders;
        this.pauseRequestTime = pauseRequestTime;
        this.requestMethod = requestMethod;
        this.requestParams = requestParams;
        this.customDownloadActivityClass = customDownloadActivityClass;
        this.isForceRedownload = isForceRedownload;
        this.isSilentDownload = isSilentDownload;
        this.service = service;
        this.onlyDownload = onlyDownload;
        this.title = title;
        this.downloadUrl = downloadUrl;
        this.updateMsg = updateMsg;
        this.paramBundle = paramBundle;
        if (this.service == null) {
            throw new RuntimeException("you must define your service which extends AVService.");
        }
        if (requestUrl == null) {
            throw new RuntimeException("requestUrl is needed.");
        }
    }

//    private VersionParams(String requestUrl, String downloadAPKPath, HttpHeaders httpHeaders, long pauseRequestTime, HttpRequestMethod requestMethod, HttpParams requestParams, Class customDownloadActivityClass, boolean isForceRedownload, boolean isSilentDownload, Class<? extends AVersionService> service, boolean onlyDownload) {
//        this.requestUrl = requestUrl;
//        this.downloadAPKPath = downloadAPKPath;
//        this.httpHeaders = httpHeaders;
//        this.pauseRequestTime = pauseRequestTime;
//        this.requestMethod = requestMethod;
//        this.requestParams = requestParams;
//        this.customDownloadActivityClass = customDownloadActivityClass;
////        this.isForceUpdate = isForceUpdate;
//        this.isForceRedownload = isForceRedownload;
//        this.isSilentDownload = isSilentDownload;
//        this.service = service;
//        this.onlyDownload = onlyDownload;
//
//    }

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

//    public boolean isForceUpdate() {
//        return isForceUpdate;
//    }

    public boolean isForceRedownload() {
        return isForceRedownload;
    }

    public boolean isSilentDownload() {
        return isSilentDownload;
    }

    public boolean isOnlyDownload() {
        return onlyDownload;
    }

    public void setParamBundle(Bundle paramBundle) {
        this.paramBundle = paramBundle;
    }


    public static class Builder {

        VersionParams params;

        public Builder() {
            params = new VersionParams();
            params.downloadAPKPath = FileHelper.getDownloadApkCachePath();
            params.pauseRequestTime = 1000 * 30;
            params.requestMethod = HttpRequestMethod.GET;
            params.customDownloadActivityClass = VersionDialogActivity.class;
//            this.isForceUpdate = false;
            params.isForceRedownload = false;
            params.isSilentDownload = false;
            params.onlyDownload = false;
            params.isShowDownloadFailDialog = true;
            params.service = MyService.class;
            params.isShowNotification = true;
            params.isShowDownloadingDialog = true;
        }

        public Builder setParamBundle(Bundle paramBundle) {
            params.paramBundle = paramBundle;
            return this;
        }

        public Builder setDownloadUrl(String downloadUrl) {
            params.downloadUrl = downloadUrl;
            return this;
        }

        public Builder setTitle(String title) {
            params.title = title;
            return this;
        }

        public Builder setUpdateMsg(String updateMsg) {
            params.updateMsg = updateMsg;
            return this;
        }

        public Builder setOnlyDownload(boolean onlyDownload) {
            params.onlyDownload = onlyDownload;
            return this;
        }

        public Builder setRequestUrl(String requestUrl) {
            params.requestUrl = requestUrl;
            return this;
        }

        public Builder setDownloadAPKPath(String downloadAPKPath) {
            params.downloadAPKPath = downloadAPKPath;
            return this;
        }

        public Builder setHttpHeaders(HttpHeaders httpHeaders) {
            params.httpHeaders = httpHeaders;
            return this;
        }

        public Builder setPauseRequestTime(long pauseRequestTime) {
            params.pauseRequestTime = pauseRequestTime;
            return this;
        }

        public Builder setRequestMethod(HttpRequestMethod requestMethod) {
            params.requestMethod = requestMethod;
            return this;
        }

        public Builder setRequestParams(HttpParams requestParams) {
            params.requestParams = requestParams;
            return this;
        }

        public Builder setCustomDownloadActivityClass(Class customDownloadActivityClass) {
            params.customDownloadActivityClass = customDownloadActivityClass;
            return this;
        }

//        public Builder setForceUpdate(boolean forceUpdate) {
//            isForceUpdate = forceUpdate;
//            return this;
//        }

        public Builder setForceRedownload(boolean forceRedownload) {
            params.isForceRedownload = forceRedownload;
            return this;
        }

        public Builder setSilentDownload(boolean silentDownload) {
            params.isSilentDownload = silentDownload;
            return this;
        }

        public Builder setService(Class<? extends AVersionService> service) {
            params.service = service;
            return this;
        }

        public Builder setShowDownloadingDialog(boolean showDownloadingDialog) {
            params.isShowDownloadingDialog = showDownloadingDialog;
            return this;
        }

        public Builder setShowNotification(boolean showNotification) {
            params.isShowNotification = showNotification;
            return this;
        }

        public Builder setShowDownLoadFailDialog(boolean showDownLoadFailDialog) {
            params.isShowDownloadFailDialog = showDownLoadFailDialog;
            return this;
        }

        public VersionParams build() {
            return params;
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
        dest.writeByte(this.isForceRedownload ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSilentDownload ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.service);
        dest.writeByte(this.onlyDownload ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.updateMsg);
        dest.writeBundle(this.paramBundle);
        dest.writeByte(this.isShowDownloadingDialog ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowNotification ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowDownloadFailDialog ? (byte) 1 : (byte) 0);
    }

    protected VersionParams(Parcel in) {
        this.requestUrl = in.readString();
        this.downloadAPKPath = in.readString();
        this.httpHeaders = (HttpHeaders) in.readSerializable();
        this.pauseRequestTime = in.readLong();
        int tmpRequestMethod = in.readInt();
        this.requestMethod = tmpRequestMethod == -1 ? null : HttpRequestMethod.values()[tmpRequestMethod];
        this.requestParams = (HttpParams) in.readSerializable();
        this.customDownloadActivityClass = (Class<? extends VersionDialogActivity>) in.readSerializable();
        this.isForceRedownload = in.readByte() != 0;
        this.isSilentDownload = in.readByte() != 0;
        this.service = (Class<? extends AVersionService>) in.readSerializable();
        this.onlyDownload = in.readByte() != 0;
        this.title = in.readString();
        this.downloadUrl = in.readString();
        this.updateMsg = in.readString();
        this.paramBundle = in.readBundle();
        this.isShowDownloadingDialog = in.readByte() != 0;
        this.isShowNotification = in.readByte() != 0;
        this.isShowDownloadFailDialog = in.readByte() != 0;
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

package com.allenliu.versionchecklib;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Allen Liu on 2016/9/22.
 */

public class VersionParams implements Serializable {

    /**
     * 请求的版本号url
     */
    private String requestUrl;
    private int requestMethod=AVersionService.GET;
    private boolean isForceUpdate=false;
    private String versionServiceName;
    /**
     * 请求失败，重试等待时间 默认间隔10s
     */
    private long pauseRequestTime=10*1000;
    private Map<String,Object>requestParams;

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    /**
     * 请求接口需要的参数
     * @param requestParams
     */
    public VersionParams setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public long getPauseRequestTime() {
        return pauseRequestTime;
    }
    /**
     * 请求失败，重试等待时间 默认间隔10s
     */
    public VersionParams setPauseRequestTime(long pauseRequestTime) {
        this.pauseRequestTime = pauseRequestTime;
        return this;
    }

    public String getVersionServiceName() {
        return versionServiceName;
    }

    public VersionParams setVersionServiceName(String versionClassName) {
        this.versionServiceName = versionClassName;
        return this;
    }

    public boolean getIsForceUpdate() {
        return isForceUpdate;
    }

    /**
     * 当检测到新版本的时候 是否强制更新 ,默认不强制更新
     * @param isForceUpdate
     */
    public VersionParams setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
        return this;
    }


    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * 请求的版本号url
     */
    public VersionParams setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return  this;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    /**
     * 设置请求方式 默认为GET
     * @param requestMethod
     * @return
     */
    public VersionParams setRequestMethod(int requestMethod) {
        this.requestMethod = requestMethod;
        return  this;
    }


}

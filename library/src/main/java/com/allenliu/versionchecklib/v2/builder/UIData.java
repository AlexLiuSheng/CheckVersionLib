package com.allenliu.versionchecklib.v2.builder;

import android.os.Bundle;

/**
 * Created by allenliu on 2018/1/18.
 */

public class UIData {
    private final String TITLE = "title", CONTENT = "content", DOWNLOAD_URL = "download_url";

    private Bundle versionBundle;

    public static UIData create() {
        return new UIData();
    }

    public String getDownloadUrl() {
        return versionBundle.getString(DOWNLOAD_URL);
    }

    public UIData setDownloadUrl(String downloadUrl) {
        versionBundle.putString(DOWNLOAD_URL, downloadUrl);
        return this;
    }


    private UIData() {
        versionBundle = new Bundle();
        versionBundle.putString(TITLE, "by `UIData.setTitle()` to set your update title");
        versionBundle.putString(CONTENT, "by `UIData.setContent()` to set your update content ");
    }

    public UIData setTitle(String title) {
        versionBundle.putString(TITLE, title);
        return this;
    }

    public UIData setContent(String content) {
        versionBundle.putString(CONTENT, content);
        return this;
    }

    public String getTitle() {
        return versionBundle.getString(TITLE);
    }

    public String getContent() {
        return versionBundle.getString(CONTENT);
    }

    public Bundle getVersionBundle() {
        return versionBundle;
    }
}

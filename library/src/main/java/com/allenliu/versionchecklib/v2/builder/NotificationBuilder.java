package com.allenliu.versionchecklib.v2.builder;

import com.allenliu.versionchecklib.R;

/**
 * Created by allenliu on 2018/1/19.
 */

public class NotificationBuilder {
    private int icon;
    private String contentTitle;
    private String ticker;
    private String contentText;
    private boolean isRingtone;

    public static NotificationBuilder create(){
        return new NotificationBuilder();
    }

    private NotificationBuilder() {
        icon = R.mipmap.ic_launcher;
        isRingtone = true;
    }

    public int getIcon() {
        return icon;
    }

    public NotificationBuilder setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public NotificationBuilder setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
        return this;
    }

    public String getTicker() {
        return ticker;
    }

    public NotificationBuilder setTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public String getContentText() {
        return contentText;
    }

    public NotificationBuilder setContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }

    public boolean isRingtone() {
        return isRingtone;
    }

    public NotificationBuilder setRingtone(boolean ringtone) {
        isRingtone = ringtone;
        return this;
    }
}

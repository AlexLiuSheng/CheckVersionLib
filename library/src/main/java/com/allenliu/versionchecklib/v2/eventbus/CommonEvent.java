package com.allenliu.versionchecklib.v2.eventbus;

/**
 * Created by Allen Liu on 2018/01/18.
 */

public class CommonEvent<T> extends BaseEvent {
    public boolean isSuccessful() {
        return isSuccessful;
    }

    public CommonEvent setSuccessful(boolean successful) {
        isSuccessful = successful;
        return this;
    }

    private boolean isSuccessful;
    private String message;
    private T data;

    public int getResponseCode() {
        return responseCode;
    }

    public CommonEvent setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    private int responseCode;

    public String getMessage() {
        return message;
    }

    public CommonEvent setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public CommonEvent setData(T data) {
        this.data = data;
        return this;
    }
    public static CommonEvent getSimpleEvent(int type){
        CommonEvent commonEvent=new CommonEvent();
        commonEvent.setSuccessful(true);
        commonEvent.setEventType(type);
        return commonEvent;
    }
}

package com.allenliu.versionchecklib.v2.builder;

import android.os.Bundle;

/**
 * Created by allenliu on 2018/1/18.
 */

public class UIData  {
    private final String TITLE="title",CONTENT="content";
    private Bundle versionBundle;

    public UIData() {
        versionBundle=new Bundle();
    }
    public void setTitle(String title){
        versionBundle.putString(TITLE,title);
    }
    public void setContent(String content){
        versionBundle.putString(CONTENT,content);
    }
    public String getTitle(){
        return versionBundle.getString(TITLE);
    }
    public String getContent(){
        return versionBundle.getString(CONTENT);
    }

    public Bundle getVersionBundle() {
        return versionBundle;
    }
}

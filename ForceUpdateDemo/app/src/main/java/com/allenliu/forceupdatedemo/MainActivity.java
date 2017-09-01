package com.allenliu.forceupdatedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionParams;

public class MainActivity extends AppCompatActivity {
public static MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VersionParams.Builder builder=new VersionParams.Builder().setRequestUrl("http://www.baidu.com")
                .setCustomDownloadActivityClass(CustomDialogActivity.class)
                .setService(MyService.class);
        AllenChecker.startVersionCheck(this,builder.build());
        activity=this;
    }
}

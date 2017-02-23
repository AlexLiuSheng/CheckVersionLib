package com.allenliu.sample;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.allenliu.versionchecklib.AVersionService;
import com.allenliu.versionchecklib.HttpRequestMethod;
import com.allenliu.versionchecklib.VersionParams;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        VersionParams versionParams=null;
       stopService(new Intent(this,DemoService.class));
        switch (view.getId()) {
            case R.id.btn1:
                versionParams = new VersionParams()
                        .setRequestUrl("http://www.baidu.com")
                        .setRequestMethod(HttpRequestMethod.GET);
                break;
            case R.id.btn2:
                HttpParams params = new HttpParams();
                HttpHeaders header = new HttpHeaders();
                versionParams = new VersionParams()
                        .setRequestUrl("http://www.baidu.com")
                        .setRequestParams(params)
                        .setRequestMethod(HttpRequestMethod.GET)
                        .setHttpHeaders(header)
                        .setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
                break;
            case R.id.btn3:
                versionParams = new VersionParams()
                        .setRequestUrl("http://www.baidu.com")
                        .setRequestMethod(HttpRequestMethod.GET)
                        .setCustomDownloadActivityClass(CustomVersionDialogTwoActivity.class);
                break;

        }
        Intent intent = new Intent(this, DemoService.class);
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        startService(intent);

    }
}

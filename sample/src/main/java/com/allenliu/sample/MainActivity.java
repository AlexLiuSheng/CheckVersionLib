package com.allenliu.sample;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        HttpParams params = new HttpParams();
        params.put("access_token", "IlRrVrdaauYbX2i7NiTHm6ExqEZT9IHs");
        params.put("version_code", "1");
        params.put("client_type", "2");
        HttpHeaders header = new HttpHeaders();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        header.put("X-Geridge-Appid", "geridge-mobile");
        VersionParams versionParams = new VersionParams()
                .setVersionServiceName(DemoService.class.getName())
                .setRequestUrl("http://www.baidu.com")
                .setRequestParams(params)
                .setRequestMethod(HttpRequestMethod.GET)
                .setHttpHeaders(header);
        Intent intent = new Intent(this, DemoService.class);
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        startService(intent);
    }
}

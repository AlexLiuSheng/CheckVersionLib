package com.allenliu.versionchecklib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

public class VersionDemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_demo);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object>param=new HashMap<String, Object>();
                param.put("user_type",1);
                param.put("client_type",0);
                String url="";
                VersionParams versionField = new VersionParams()
                        .setIsForceUpdate(false)
                        .setRequestMethod(AVersionService.POST)
                        .setRequestUrl(url)
                        .setRequestParams(param)
                        .setVersionServiceName("com.allenliu.versionchecklib.DemoService");
                Intent intent = new Intent(VersionDemoActivity.this, DemoService.class);
                intent.putExtra("versionField", versionField);
                startService(intent);
            }
        });
    }
}

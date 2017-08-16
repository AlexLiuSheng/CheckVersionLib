package com.allenliu.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.allenliu.versionchecklib.AVersionService;
import com.allenliu.versionchecklib.AllenChecker;
import com.allenliu.versionchecklib.VersionDialogActivity;
import com.allenliu.versionchecklib.VersionParams;

public class MainActivity extends AppCompatActivity {
    private EditText etPauseTime;
    private EditText etAddress;
    private RadioGroup radioGroup;
    private CheckBox forceUpdateCheckBox;
    private RadioGroup radioGroup2;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPauseTime = (EditText) findViewById(R.id.etTime);
        etAddress = (EditText) findViewById(R.id.etAddress);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);

        forceUpdateCheckBox = (CheckBox) findViewById(R.id.checkbox);
        mainActivity = this;
    }

    public void onClick(View view) {
        //you can add your request params and request method
        //eg.
//        HttpRequestMethod requestMethod=HttpRequestMethod.GET;
//        HttpHeaders httpHeaders=new HttpHeaders();
//        HttpParams httpParams=new HttpParams();

        //只有requsetUrl 是必须值 其他参数都有默认值，可选

        VersionParams.Builder builder = new VersionParams.Builder()
//                .setHttpHeaders(httpHeaders)
//                .setRequestMethod(requestMethod)
//                .setRequestParams(httpParams)
                .setRequestUrl("http://www.baidu.com")
                .setService(DemoService.class);

        stopService(new Intent(this, DemoService.class));
        switch (view.getId()) {
            case R.id.sendbtn:
                String pauseTime = etPauseTime.getText().toString();
                String address = etAddress.getText().toString();
                try {
                    if (!pauseTime.isEmpty() && Long.valueOf(pauseTime) > 0) {
                        builder.setPauseRequestTime(Long.valueOf(pauseTime));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!address.isEmpty())
                    builder.setDownloadAPKPath(address);
                //更新界面选择
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.btn1:
                        CustomVersionDialogActivity.customVersionDialogIndex = 3;
                        //这里其实不用设置的，库默认就会使用的 我是为了展示demo，来回切换界面的原因才写的
                        builder.setCustomDownloadActivityClass(VersionDialogActivity.class);
                        break;
                    case R.id.btn2:
                        CustomVersionDialogActivity.customVersionDialogIndex = 1;
                        builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
                        break;
                    case R.id.btn3:
                        CustomVersionDialogActivity.customVersionDialogIndex = 2;
                        builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
                        break;
                }
                //下载进度界面选择
                switch (radioGroup2.getCheckedRadioButtonId()) {
                    case R.id.btn21:
                        //同理
                        CustomVersionDialogActivity.isCustomDownloading = false;
                        builder.setCustomDownloadActivityClass(VersionDialogActivity.class);
                        break;
                    case R.id.btn22:
                        //可以看到 更改更新界面或者是更改下载界面都是重写VersionDialogActivity
                        CustomVersionDialogActivity.isCustomDownloading = true;
                        builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
                        break;
                }
                //强制更新
                if (forceUpdateCheckBox.isChecked()) {
                    CustomVersionDialogActivity.isForceUpdate = true;
                    builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
                } else {
                    //同理
                    CustomVersionDialogActivity.isForceUpdate = false;
                    builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
                }

                AllenChecker.startVersionCheck(this, builder.build());
                break;

        }

    }
}

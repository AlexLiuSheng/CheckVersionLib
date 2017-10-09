package com.allenliu.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionDialogActivity;
import com.allenliu.versionchecklib.core.VersionParams;

public class MainActivity extends AppCompatActivity {
    private EditText etPauseTime;
    private EditText etAddress;
    private RadioGroup radioGroup;
    private CheckBox forceUpdateCheckBox;
    private CheckBox silentDownloadCheckBox;
    private CheckBox forceDownloadCheckBox;
    private CheckBox onlyDownloadCheckBox;
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
        silentDownloadCheckBox = (CheckBox) findViewById(R.id.checkbox2);
        forceUpdateCheckBox = (CheckBox) findViewById(R.id.checkbox);
        forceDownloadCheckBox = (CheckBox) findViewById(R.id.checkbox3);
        onlyDownloadCheckBox = (CheckBox) findViewById(R.id.checkbox4);
        mainActivity = this;

    }

    public void onClick(View view) {
        //you can add your request params and request method
        //eg.
        //只有requsetUrl service 是必须值 其他参数都有默认值，可选

//        com.allenliu.versionchecklib.core.http.HttpHeaders headers=new com.allenliu.versionchecklib.core.http.HttpHeaders();
//        headers.put("a","b");
        VersionParams.Builder builder = new VersionParams.Builder()
//                .setHttpHeaders(headers)
//                .setRequestMethod(requestMethod)
//                .setRequestParams(httpParams)
                .setRequestUrl("http://www.baidu.com")
//                .setDownloadAPKPath(getApplicationContext().getFilesDir()+"/")
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
                //静默下载
                if (silentDownloadCheckBox.isChecked()) {
                    builder.setSilentDownload(true);
                } else {
                    builder.setSilentDownload(false);
                }
                //强制重新下载
                if (forceDownloadCheckBox.isChecked()) {
                    builder.setForceRedownload(true);
                } else {
                    builder.setForceRedownload(false);
                }
                //是否仅使用下载功能
                if (onlyDownloadCheckBox.isChecked()) {
                    //如果仅使用下载功能，downloadUrl是必须的
                    builder.setOnlyDownload(true);
                    builder.setDownloadUrl("http://down1.uc.cn/down2/zxl107821.uc/miaokun1/UCBrowser_V11.5.8.945_android_pf145_bi800_(Build170627172528).apk")
                            .setTitle("检测到新版本").setUpdateMsg(getString(R.string.updatecontent));
                } else
                    builder.setOnlyDownload(false);

                AllenChecker.startVersionCheck(this, builder.build());
                break;

        }

    }
}

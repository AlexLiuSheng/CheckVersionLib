package com.allenliu.sample.v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.allenliu.sample.R;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.core.VersionDialogActivity;
import com.allenliu.versionchecklib.core.VersionParams;

public class V1Activity extends AppCompatActivity {
    private EditText etPauseTime;
    private EditText etAddress;
    private RadioGroup radioGroup;
    private CheckBox forceUpdateCheckBox;
    private CheckBox silentDownloadCheckBox;
    private CheckBox forceDownloadCheckBox;
    private CheckBox onlyDownloadCheckBox;
    private CheckBox showNotificationCheckBox;
    private CheckBox showDownloadingCheckBox;
    private RadioGroup radioGroup2;

    public static V1Activity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v1);
        etPauseTime = (EditText) findViewById(R.id.etTime);
        etAddress = (EditText) findViewById(R.id.etAddress);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        silentDownloadCheckBox = (CheckBox) findViewById(R.id.checkbox2);
        forceUpdateCheckBox = (CheckBox) findViewById(R.id.checkbox);
        forceDownloadCheckBox = (CheckBox) findViewById(R.id.checkbox3);
        onlyDownloadCheckBox = (CheckBox) findViewById(R.id.checkbox4);
        showNotificationCheckBox = (CheckBox) findViewById(R.id.checkbox5);
        showDownloadingCheckBox = (CheckBox) findViewById(R.id.checkbox6);
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
                .setRequestUrl("https://www.baidu.com")
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
                    builder.setOnlyDownload(true)
                            .setDownloadUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk")
                            .setTitle("检测到新版本")
                            .setUpdateMsg(getString(R.string.updatecontent));
                } else
                    builder.setOnlyDownload(false);
                //是否显示通知栏
                if (showNotificationCheckBox.isChecked()) {
                    builder.setShowNotification(true);
                } else
                    builder.setShowNotification(false);
                if (showDownloadingCheckBox.isChecked()) {
                    builder.setShowDownloadingDialog(true);
                } else
                    builder.setShowDownloadingDialog(false);

                builder.setShowDownLoadFailDialog(false);
//                builder.setDownloadAPKPath("/storage/emulated/0/AllenVersionPath2/");
                AllenChecker.startVersionCheck(getApplication(), builder.build());
                break;

            case R.id.cancelBtn:
                AllenChecker.cancelMission();
//                VersionParams.Builder builder2 = new VersionParams.Builder();
//                builder2.setOnlyDownload(true)
//                        .setDownloadUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk")
//                        .setTitle("检测到新版本")
//                        .setForceRedownload(true)
//                        .setUpdateMsg(getString(R.string.updatecontent));
//                AllenChecker.startVersionCheck(this, builder2.build());
                break;

        }

    }
}

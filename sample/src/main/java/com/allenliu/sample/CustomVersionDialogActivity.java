package com.allenliu.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.allenliu.versionchecklib.VersionDialogActivity;

public class CustomVersionDialogActivity extends VersionDialogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_version_dialog);
    }
}

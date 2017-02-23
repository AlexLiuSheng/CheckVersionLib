package com.allenliu.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.allenliu.versionchecklib.VersionDialogActivity;

public class CustomVersionDialogTwoActivity extends VersionDialogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_version_dialog_two);
    }

    @Override
    public void showVersionDialog() {
      //  super.showVersionDialog();
        final BaseDialog baseDialog = new BaseDialog(this, R.style.BaseDialog, R.layout.custom_dialog_two_layout);
        baseDialog.show();
        TextView tvCancel= (TextView) baseDialog.findViewById(R.id.tv_cancel);
        TextView tvUpdate= (TextView) baseDialog.findViewById(R.id.tv_update);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseDialog.dismiss();
                downloadFile("http://www.apk3.com/uploads/soft/guiguangbao/UCllq.apk");
            }
        });
        baseDialog.show();
    }
}

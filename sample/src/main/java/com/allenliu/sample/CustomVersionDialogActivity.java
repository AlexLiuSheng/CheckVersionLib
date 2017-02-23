package com.allenliu.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.VersionDialogActivity;
import com.allenliu.versionchecklib.callback.CancelClickListener;
import com.allenliu.versionchecklib.callback.CommitClickListener;
import com.allenliu.versionchecklib.callback.DownloadSuccessListener;
import com.allenliu.versionchecklib.callback.DownloadingListener;

import java.io.File;

public class CustomVersionDialogActivity extends VersionDialogActivity implements CommitClickListener, CancelClickListener, DownloadingListener, DownloadSuccessListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_version_dialog);
        setOnDownloadSuccessListener(this);
        setOnDownloadingListener(this);
        setCommitClickListener(this);
        setCancelClickListener(this);
    }

    @Override
    public void onDownloadSuccess(File file) {
        Log.e("CustomVersionDialogActi", "文件下载成功");
    }

    @Override
    public void onDownloading(float progress) {
        Log.e("CustomVersionDialogActi", "正在下载中...");
    }

    @Override
    public void onCancelClick() {
        Log.e("CustomVersionDialogActi", "取消按钮点击");
    }

    @Override
    public void onCommitClick() {
        Log.e("CustomVersionDialogActi", "确认按钮点击");
    }

    @Override
    public void showVersionDialog() {
        //使用默认的提示框直接调用父类的方法,如果需要自定义的对话框，那么直接重写此方法
        // super.showVersionDialog();
        final BaseDialog baseDialog = new BaseDialog(this, R.style.BaseDialog, R.layout.custom_dialog_one_layout);
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
        Toast.makeText(this, "显示自定义对话框", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFailDialog() {
        // super.showFailDialog();
        Toast.makeText(this, "使用自定义失败加载框", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog(int currentProgress) {
        super.showLoadingDialog(currentProgress);
//        Toast.makeText(this, "显示自定义的下载加载框", Toast.LENGTH_SHORT).show();
    }

}

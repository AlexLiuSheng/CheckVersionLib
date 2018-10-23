package com.allenliu.versionchecklib.v2.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;

import okhttp3.Call;
import okhttp3.Dispatcher;

public class DownloadingActivity extends AllenBaseActivity implements DialogInterface.OnCancelListener {
    public static final String PROGRESS = "progress";
    private Dialog downloadingDialog;
    private int currentProgress = 0;
    protected boolean isDestroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.e("loading activity create");

        showLoadingDialog();
    }

    public void onCancel(boolean isDownloadCompleted) {
        if (!isDownloadCompleted) {
            AllenHttp.getHttpClient().dispatcher().cancelAll();
            cancelHandler();
            checkForceUpdate();
        }
        finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        onCancel(false);
    }


    @Override
    public void receiveEvent(CommonEvent commonEvent) {
        switch (commonEvent.getEventType()) {
            case AllenEventType.UPDATE_DOWNLOADING_PROGRESS:
                int progress = (int) commonEvent.getData();
                currentProgress = progress;
                updateProgress();
                break;
            case AllenEventType.DOWNLOAD_COMPLETE:
                onCancel(true);
                break;
            case AllenEventType.CLOSE_DOWNLOADING_ACTIVITY:
                destroy();
                break;
        }
    }

    @Override
    public void showDefaultDialog() {
        View loadingView = LayoutInflater.from(this).inflate(R.layout.downloading_layout, null);
        downloadingDialog = new AlertDialog.Builder(this).setTitle("").setView(loadingView).create();
        if (getVersionBuilder().getForceUpdateListener() != null)
            downloadingDialog.setCancelable(false);
        else
            downloadingDialog.setCancelable(true);

        downloadingDialog.setCanceledOnTouchOutside(false);
        ProgressBar pb = loadingView.findViewById(R.id.pb);
        TextView tvProgress = loadingView.findViewById(R.id.tv_progress);
        tvProgress.setText(String.format(getString(R.string.versionchecklib_progress), currentProgress));
        pb.setProgress(currentProgress);
        downloadingDialog.show();
    }

    @Override
    public void showCustomDialog() {
        if(getVersionBuilder()!=null) {
            downloadingDialog = getVersionBuilder().getCustomDownloadingDialogListener().getCustomDownloadingDialog(this, currentProgress, getVersionBuilder().getVersionBundle());
            View cancelView = downloadingDialog.findViewById(R.id.versionchecklib_loading_dialog_cancel);
            if (cancelView != null) {
                cancelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCancel(false);
                    }
                });
            }
            downloadingDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyWithOutDismiss();
        isDestroy = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDestroy = false;
        if (downloadingDialog != null && !downloadingDialog.isShowing())
            downloadingDialog.show();
    }

    private void destroyWithOutDismiss() {
        if (downloadingDialog != null && downloadingDialog.isShowing()) {
            downloadingDialog.dismiss();
//            onCancel(false);
        }
    }

    private void destroy() {
        ALog.e("loading activity destroy");

        if (downloadingDialog != null && downloadingDialog.isShowing()) {
            downloadingDialog.dismiss();
//            onCancel(false);
        }
        finish();
    }

    private void updateProgress() {
        if (!isDestroy) {
            if (getVersionBuilder() != null && getVersionBuilder().getCustomDownloadingDialogListener() != null) {
                getVersionBuilder().getCustomDownloadingDialogListener().updateUI(downloadingDialog, currentProgress, getVersionBuilder().getVersionBundle());
            } else {
                ProgressBar pb = downloadingDialog.findViewById(R.id.pb);
                pb.setProgress(currentProgress);
                TextView tvProgress = downloadingDialog.findViewById(R.id.tv_progress);
                tvProgress.setText(String.format(getString(R.string.versionchecklib_progress), currentProgress));
                if (!downloadingDialog.isShowing())
                    downloadingDialog.show();
            }
        }
    }

    private void showLoadingDialog() {
        ALog.e("show loading");
        if (!isDestroy) {
            if (getVersionBuilder() != null && getVersionBuilder().getCustomDownloadingDialogListener() != null) {
                showCustomDialog();
            } else {
                showDefaultDialog();
            }
            downloadingDialog.setOnCancelListener(this);
        }
    }


}

package com.allenliu.versionchecklib.v2.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.allenliu.versionchecklib.R
import com.allenliu.versionchecklib.utils.ALog
import com.allenliu.versionchecklib.utils.AllenEventBusUtil
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.BuilderManager
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType

class DownloadFailedActivity : AllenBaseActivity(), DialogInterface.OnCancelListener {
    private var downloadFailedDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDowloadFailedDialog()
    }

    override fun showDefaultDialog() {
        downloadFailedDialog = AlertDialog
                .Builder(this)
                .setMessage(getString(R.string.versionchecklib_download_fail_retry))
                .setPositiveButton(getString(R.string.versionchecklib_confirm)) { _, _ -> retryDownload() }
                .setNegativeButton(getString(R.string.versionchecklib_cancel)) { d, _ -> onCancel(d) }
                .create().apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(true)
                    show()
                }

    }

    override fun showCustomDialog() {
        BuilderManager.doWhenNotNull {
            downloadFailedDialog = customDownloadFailedListener.getCustomDownloadFailed(this@DownloadFailedActivity, versionBundle).apply {
                val retryView = findViewById<View?>(R.id.versionchecklib_failed_dialog_retry)
                retryView?.setOnClickListener { retryDownload() }
                val cancelView = findViewById<View?>(R.id.versionchecklib_failed_dialog_cancel)
                cancelView?.setOnClickListener { onCancel(this) }
                show()

            }

        }
    }

    private fun showDowloadFailedDialog() {
        AllenEventBusUtil.sendEventBusStick(AllenEventType.CLOSE_DOWNLOADING_ACTIVITY)
        BuilderManager.doWhenNotNull {
            if (customDownloadFailedListener != null) {
                ALog.e("show customization failed dialog")
                showCustomDialog()
            } else {
                ALog.e("show default failed dialog")
                showDefaultDialog()
            }
            downloadFailedDialog?.setOnCancelListener(this@DownloadFailedActivity)
        }

    }

    override fun onCancel(dialogInterface: DialogInterface) {
        ALog.e("on cancel" +
                "")
        cancelHandler()
        checkForceUpdate()
        AllenVersionChecker.getInstance().cancelAllMission()
        finish()
    }

    private fun retryDownload() {
        //增加commit 回调
        BuilderManager.doWhenNotNull {
            downloadFailedCommitClickListener?.onCommitClick()
        }
        AllenEventBusUtil.sendEventBus(AllenEventType.START_DOWNLOAD_APK)
        finish()
    }

    override fun onPause() {
        super.onPause()
        if (downloadFailedDialog != null && downloadFailedDialog!!.isShowing) downloadFailedDialog!!.dismiss()
        //        finish();
    }

    override fun onResume() {
        super.onResume()
        if (downloadFailedDialog != null && !downloadFailedDialog!!.isShowing) downloadFailedDialog!!.show()
    }
}
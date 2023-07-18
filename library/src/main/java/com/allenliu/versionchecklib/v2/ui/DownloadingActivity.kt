package com.allenliu.versionchecklib.v2.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.allenliu.versionchecklib.R
import com.allenliu.versionchecklib.core.http.AllenHttp
import com.allenliu.versionchecklib.utils.ALog
import com.allenliu.versionchecklib.v2.builder.BuilderManager
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent
import org.greenrobot.eventbus.EventBus

class DownloadingActivity : AllenBaseActivity(), DialogInterface.OnCancelListener {
    private var downloadingDialog: Dialog? = null
    private var currentProgress = 0
    protected var isDestroy = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ALog.e("loading activity create")
        showLoadingDialog()
    }

    fun onCancel(isDownloadCompleted: Boolean) {
        if (!isDownloadCompleted) {
            //should cancel downloading http request if it is force update action
            BuilderManager.doWhenNotNull {
                forceUpdateListener?.let {
                    AllenHttp.getHttpClient().dispatcher.cancelAll()
                }
            }
            cancelHandler()
            checkForceUpdate()
        }
        finish()
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancel(false)
    }

    override fun receiveEvent(commonEvent: CommonEvent<*>) {
        super.receiveEvent(commonEvent)
        when (commonEvent.eventType) {
            AllenEventType.UPDATE_DOWNLOADING_PROGRESS -> {
                val progress = commonEvent.data as Int
                currentProgress = progress
                updateProgress()
            }

            AllenEventType.DOWNLOAD_COMPLETE -> onCancel(true)
            AllenEventType.CLOSE_DOWNLOADING_ACTIVITY -> {
                destroy()
                EventBus.getDefault().removeStickyEvent(commonEvent)
            }
        }
    }

    override fun showDefaultDialog() {
        val loadingView = LayoutInflater.from(this).inflate(R.layout.downloading_layout, null)
        downloadingDialog =
            AlertDialog.Builder(this).setTitle("").setView(loadingView).create().apply {
                BuilderManager.doWhenNotNull {
                    if (forceUpdateListener != null) setCancelable(false) else setCancelable(true)
                    setCanceledOnTouchOutside(false)
                    val pb = loadingView.findViewById<ProgressBar>(R.id.pb)
                    val tvProgress = loadingView.findViewById<TextView>(R.id.tv_progress)
                    tvProgress.text =
                        String.format(getString(R.string.versionchecklib_progress), currentProgress)
                    pb.progress = currentProgress
                    show()
                }

            }

    }

    override fun showCustomDialog() {
        BuilderManager.doWhenNotNull {
            downloadingDialog = customDownloadingDialogListener.getCustomDownloadingDialog(
                this@DownloadingActivity,
                currentProgress,
                versionBundle
            ).apply {
                if (forceUpdateListener != null) setCancelable(false) else setCancelable(true)
                val cancelView = findViewById<View?>(R.id.versionchecklib_loading_dialog_cancel)
                cancelView?.setOnClickListener { onCancel(false) }
                show()
            }
        }


    }

    override fun onStop() {
        super.onStop()
        //#issue350
        destroyWithOutDismiss()
        isDestroy = true
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        isDestroy = false
        downloadingDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }

    }

    private fun destroyWithOutDismiss() {
        downloadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun destroy() {
        ALog.e("loading activity destroy")
        destroyWithOutDismiss()
        finish()
    }

    private fun updateProgress() {
        if (!isDestroy) {
            BuilderManager.doWhenNotNull {
                if (customDownloadingDialogListener != null) {
                    customDownloadingDialogListener.updateUI(
                        downloadingDialog,
                        currentProgress,
                        versionBundle
                    )
                } else {
                    val pb = downloadingDialog?.findViewById<ProgressBar>(R.id.pb)
                    pb?.progress = currentProgress
                    val tvProgress = downloadingDialog?.findViewById<TextView>(R.id.tv_progress)
                    tvProgress?.text =
                        String.format(getString(R.string.versionchecklib_progress), currentProgress)
                    downloadingDialog?.show()
                }
            }

        }
    }

    private fun showLoadingDialog() {
        ALog.e("show loading")
        if (!isDestroy) {
            BuilderManager.doWhenNotNull {
                if (customDownloadingDialogListener != null) {
                    showCustomDialog()
                } else {
                    showDefaultDialog()
                }
            }
            downloadingDialog?.setOnCancelListener(this)
        }
    }

    companion object {
        const val PROGRESS = "progress"
    }
}
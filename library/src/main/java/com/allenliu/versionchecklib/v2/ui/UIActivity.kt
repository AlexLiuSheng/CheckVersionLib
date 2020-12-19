package com.allenliu.versionchecklib.v2.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.allenliu.versionchecklib.R
import com.allenliu.versionchecklib.utils.ALog
import com.allenliu.versionchecklib.utils.AllenEventBusUtil
import com.allenliu.versionchecklib.utils.AppUtils
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.BuilderManager
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder
import com.allenliu.versionchecklib.v2.builder.UIData
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType
import java.io.File

class UIActivity : AllenBaseActivity(), DialogInterface.OnCancelListener {
    private var versionDialog: Dialog? = null
    private var isDestroy = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ALog.e("version activity create")
        showVersionDialog()
    }

    override fun onDestroy() {
        isDestroy = true
        ALog.e("version activity destroy")
        super.onDestroy()
    }

    override fun showDefaultDialog() {
        val builder = BuilderManager.getDownloadBuilder()
        if (builder != null) {
            val uiData: UIData? = builder.versionBundle
            var title: String? = "提示"
            var content: String = "检测到新版本"
            if (uiData != null) {
                title = uiData.title
                content = uiData.content
            }
            val alertBuilder = AlertDialog.Builder(this).setTitle(title).setMessage(content).setPositiveButton(getString(R.string.versionchecklib_confirm)) { _, _ -> dealVersionDialogCommit() }
            if (builder.forceUpdateListener == null) {
                alertBuilder.setNegativeButton(getString(R.string.versionchecklib_cancel)) { d, _ -> onCancel(d) }
                alertBuilder.setCancelable(false)
            } else {
                alertBuilder.setCancelable(false)
            }
            versionDialog = alertBuilder.create().apply {
                setCanceledOnTouchOutside(false)
                show()
            }

        }
    }

    override fun showCustomDialog() {
        val builder = BuilderManager.getDownloadBuilder() ?: return
        ALog.e("show customization dialog")
        versionDialog = builder.customVersionDialogListener.getCustomVersionDialog(this, builder.versionBundle)
                .apply {
                    try {
                        //自定义dialog，commit button 必须存在
                        val view = findViewById<View?>(R.id.versionchecklib_version_dialog_commit)
                        if (view != null) {
                            ALog.e("view not null")
                            view.setOnClickListener {
                                ALog.e("click")
                                dealVersionDialogCommit()
                            }
                        } else {
                            throwWrongIdsException()
                        }
                        //如果有取消按钮，id也必须对应
                        val cancelView = findViewById<View?>(R.id.versionchecklib_version_dialog_cancel)
                        cancelView?.setOnClickListener { onCancel(this) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throwWrongIdsException()
                    }
                    show()

                }

    }

    private fun showVersionDialog() {
        BuilderManager.doWhenNotNull {
            if (customVersionDialogListener != null) {
                showCustomDialog()
            } else {
                showDefaultDialog()
            }
        }
        versionDialog?.setOnCancelListener(this)

    }

    override fun onPause() {
        super.onPause()

        versionDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        versionDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    private fun dealVersionDialogCommit() {
        val versionBuilder: DownloadBuilder? = BuilderManager.getDownloadBuilder()
        if (versionBuilder != null) {
            //增加commit 回调
            if (versionBuilder.readyDownloadCommitClickListener != null) {
                versionBuilder.readyDownloadCommitClickListener.onCommitClick()
            }
            //如果是静默下载直接安装
            if (versionBuilder.isSilentDownload) {
                val downloadPath = versionBuilder.downloadAPKPath + getString(R.string.versionchecklib_download_apkname, if (versionBuilder.apkName != null) versionBuilder.apkName else packageName)
                AppUtils.installApk(this, File(downloadPath), versionBuilder.customInstallListener)
                checkForceUpdate()
                //否定开始下载
            } else {
                AllenEventBusUtil.sendEventBus(AllenEventType.START_DOWNLOAD_APK)
            }
            finish()
        }
    }

    override fun onCancel(dialogInterface: DialogInterface) {
        cancelHandler()
        checkForceUpdate()
        AllenVersionChecker.getInstance().cancelAllMission()
        finish()
    }
}
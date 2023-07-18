package com.allenliu.versionchecklib.v2.ui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.WorkerThread
import com.allenliu.versionchecklib.R
import com.allenliu.versionchecklib.core.http.AllenHttp
import com.allenliu.versionchecklib.utils.ALog
import com.allenliu.versionchecklib.utils.AllenEventBusUtil
import com.allenliu.versionchecklib.utils.AppUtils
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.BuilderManager
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder
import com.allenliu.versionchecklib.v2.callback.DownloadListenerKt
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent
import com.allenliu.versionchecklib.v2.net.DownloadMangerV2
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VersionService : Service() {
    private var notificationHelper: NotificationHelper? = null
    private var isServiceAlive = false
    private var executors: ExecutorService? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        ALog.e("version service create")



        init()
        return START_REDELIVER_INTENT
    }

    companion object {
        fun enqueueWork(context: Context, builder: DownloadBuilder) {
            //清除之前的任务，如果有
//            AllenVersionChecker.getInstance().cancelAllMission()
            BuilderManager.init(context, builder)
            val intent = Intent(context, VersionService::class.java)
            //显示通知栏的情况 才设置为前台服务
            if (builder.isRunOnForegroundService && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ALog.e("version service destroy")
        BuilderManager.doWhenNotNull {
            if (isRunOnForegroundService) {
                stopForeground(true)
            }
        }
        BuilderManager.destroy()
        notificationHelper?.onDestroy()
        isServiceAlive = false
        executors?.shutdown()
        AllenHttp.getHttpClient().dispatcher.cancelAll()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun onHandleWork() {
        downloadAPK()
    }

    private fun downloadAPK() {
        BuilderManager.doWhenNotNull {
            if (versionBundle != null) {
                if (isDirectDownload) {
                    startDownloadApk()
                } else {
                    if (isSilentDownload) {
                        startDownloadApk()
                    } else {
                        showVersionDialog()
                    }
                }
            }
        }

    }

    /**
     * 开启UI展示界面
     */
    private fun showVersionDialog() {
        BuilderManager.doWhenNotNull {
            val intent = Intent(this@VersionService, UIActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun showDownloadingDialog() {
        BuilderManager.doWhenNotNull {
            if (isShowDownloadingDialog) {
                val intent = Intent(this@VersionService, DownloadingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }

    }

    private fun updateDownloadingDialogProgress(progress: Int) {
        val commonEvent: CommonEvent<Any> = CommonEvent()
        commonEvent.eventType = AllenEventType.UPDATE_DOWNLOADING_PROGRESS
        commonEvent.data = progress
        commonEvent.isSuccessful = true
        EventBus.getDefault().post(commonEvent)
    }

    private fun showDownloadFailedDialog() {
        BuilderManager.doWhenNotNull {
            val intent = Intent(this@VersionService, DownloadFailedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }

    private fun requestPermissionAndDownload() {
        //不再请求权限
        startDownloadApk()
//        BuilderManager.doWhenNotNull {
//            val intent = Intent(this@VersionService, PermissionDialogActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }

    }

    private fun install() {
        BuilderManager.doWhenNotNull {
            AllenEventBusUtil.sendEventBus(AllenEventType.DOWNLOAD_COMPLETE)
            val downloadPath = downloadFilePath
            if (isSilentDownload) {
                showVersionDialog()
            } else {
                AppUtils.installApk(applicationContext, File(downloadPath), customInstallListener)
                BuilderManager.checkForceUpdate()
            }
        }

    }

    private val downloadFilePath: String
        get() {
            return BuilderManager.doWhenNotNull {
                downloadAPKPath + getString(
                    R.string.versionchecklib_download_apkname,
                    if (apkName != null) apkName else packageName
                )

            } ?: ""
        }

    @WorkerThread
    private fun startDownloadApk() {
        //判断是否缓存并且是否强制重新下载
        fun inner() {
            BuilderManager.doWhenNotNull {
                val downloadPath = downloadFilePath
                if (AppUtils.checkAPKIsExists(
                        applicationContext,
                        downloadPath,
                        newestVersionCode
                    ) && !isForceRedownload
                ) {
                    ALog.e("using cache")
                    install()
                    return@doWhenNotNull
                }
                BuilderManager.checkAndDeleteAPK()
                var downloadUrl: String? = downloadUrl
                if (downloadUrl == null && versionBundle != null) {
                    downloadUrl = versionBundle.downloadUrl
                }
                if (downloadUrl == null) {
                    AllenVersionChecker.getInstance().cancelAllMission()
                    throw RuntimeException("you must set a download url for download function using")
                }
                ALog.e("downloadPath:$downloadPath")
                DownloadMangerV2.download(
                    downloadUrl,
                    downloadAPKPath,
                    getString(
                        R.string.versionchecklib_download_apkname,
                        if (apkName != null) apkName else packageName
                    ),
                    downloadListener
                )
            }
        }
        executors?.submit { inner() }


    }

    private val downloadListener: DownloadListenerKt = object : DownloadListenerKt {
        override fun onCheckerDownloading(progress: Int) {
            BuilderManager.doWhenNotNull {
                ALog.e("download progress $progress")
                if (isServiceAlive) {
                    if (!isSilentDownload) {
                        if (isShowNotification)
                            notificationHelper?.updateNotification(progress)
                        updateDownloadingDialogProgress(progress)
                    }
                    apkDownloadListener?.onDownloading(progress)
                }
            }

        }

        override fun onCheckerDownloadSuccess(file: File) {
            BuilderManager.doWhenNotNull {
                if (isServiceAlive) {
                    if (!isSilentDownload && isShowNotification) notificationHelper?.showDownloadCompleteNotifcation(
                        file
                    )
                    apkDownloadListener?.onDownloadSuccess(file)
                    install()
                }
            }

        }


        override fun onCheckerDownloadFail() {
            BuilderManager.doWhenNotNull {
                ALog.e("download failed")
                if (!isServiceAlive) return@doWhenNotNull
                apkDownloadListener?.onDownloadFail()
                if (!isSilentDownload) {
                    AllenEventBusUtil.sendEventBusStick(AllenEventType.CLOSE_DOWNLOADING_ACTIVITY)
                    if (isShowDownloadFailDialog) {
                        showDownloadFailedDialog()
                    }
                    if (isShowNotification)
                        notificationHelper?.showDownloadFailedNotification()
                } else {
                    AllenVersionChecker.getInstance().cancelAllMission()
                }
            }

        }

        override fun onCheckerStartDownload() {
            BuilderManager.doWhenNotNull {
                ALog.e("start download apk")
                if (!isSilentDownload) {
                    if (isShowNotification)
                        notificationHelper?.showNotification()
                    showDownloadingDialog()
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveEvent(commonEvent: CommonEvent<*>) {
        when (commonEvent.eventType) {
            AllenEventType.START_DOWNLOAD_APK -> startDownloadApk()
            AllenEventType.STOP_SERVICE -> {
                stopSelf()
                EventBus.getDefault().removeStickyEvent(commonEvent)
            }
        }
    }

    //    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    //    public synchronized void onReceiveDownloadBuilder(DownloadBuilder downloadBuilder) {
    //        builder = downloadBuilder;
    //        init();
    //        EventBus.getDefault().removeStickyEvent(downloadBuilder);
    //    }
    private fun init() {
        BuilderManager.doWhenNotNull {
            //https://issuetracker.google.com/issues/76112072
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isRunOnForegroundService) {
                startForeground(
                    NotificationHelper.NOTIFICATION_ID,
                    NotificationHelper.createSimpleNotification(this@VersionService)
                )
                Thread.sleep(500)
            }
            isServiceAlive = true
            notificationHelper = NotificationHelper(applicationContext)
            executors = Executors.newSingleThreadExecutor()
            executors?.submit { onHandleWork() }
        }

    }


}
package com.allenliu.versionchecklib.v2.builder

import android.content.Context
import com.allenliu.versionchecklib.R
import com.allenliu.versionchecklib.core.DownloadManager
import com.allenliu.versionchecklib.utils.ALog
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import java.io.File

/**
 *    @author : shengliu7
 *    @e-mail : shengliu7@iflytek.com
 *    @date   : 2020/12/19 12:34 PM
 *    @desc   :
 *
 */
object BuilderManager {
    private var downloadBuilder: DownloadBuilder? = null
    lateinit var context: Context
    fun getDownloadBuilder(): DownloadBuilder? {
        return downloadBuilder
    }

    fun init(context: Context, downloadBuilder: DownloadBuilder): BuilderManager {
        this.context = context
        this.downloadBuilder = downloadBuilder
        return this
    }

    fun destroy() {
        downloadBuilder = null
    }

    /**
     * 验证安装包是否存在，并且在安装成功情况下删除安装包
     */
    fun checkAndDeleteAPK() {
        doWhenNotNull {
            //判断versioncode与当前版本不一样的apk是否存在，存在删除安装包
            try {
                val downloadPath: String = downloadAPKPath + context.getString(R.string.versionchecklib_download_apkname, context.packageName)
                if (!DownloadManager.checkAPKIsExists(context, downloadPath)) {
                    ALog.e("删除本地apk")
                    File(downloadPath).delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun checkForceUpdate() {
        doWhenNotNull {
            forceUpdateListener?.onShouldForceUpdate()
        }
    }

    fun <T> doWhenNotNull(nullBlock: (() -> T)? = null, block: DownloadBuilder.() -> T): T? {
        val builder = downloadBuilder
        if (builder != null) {
            return builder.block()

        } else {
            nullBlock?.invoke()
            AllenVersionChecker.getInstance().cancelAllMission()
        }
        return null
    }

}
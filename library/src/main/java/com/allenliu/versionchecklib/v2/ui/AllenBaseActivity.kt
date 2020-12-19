package com.allenliu.versionchecklib.v2.ui
import android.R
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.allenliu.versionchecklib.v2.builder.BuilderManager.doWhenNotNull
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by allenliu on 2018/1/18.
 */
abstract class AllenBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
        setTransparent(this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun transparentStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            activity.window.statusBarColor = Color.TRANSPARENT
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 设置根布局参数
     */
    private fun setRootView(activity: Activity) {
        val parent = activity.findViewById<View>(R.id.content) as ViewGroup
        var i = 0
        val count = parent.childCount
        while (i < count) {
            val childView = parent.getChildAt(i)
            if (childView is ViewGroup) {
                childView.setFitsSystemWindows(true)
                childView.clipToPadding = true
            }
            i++
        }
    }

    fun setTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        transparentStatusBar(activity)
        setRootView(activity)
    }

    protected fun throwWrongIdsException() {
        throw RuntimeException("customize dialog must use the specify id that lib gives")
    }

    protected fun checkForceUpdate() {
        doWhenNotNull {
            if (forceUpdateListener != null) {
                forceUpdateListener?.onShouldForceUpdate()
                finish()
            }
        }
    }

    protected fun cancelHandler() {
        doWhenNotNull {
            onCancelListener?.onCancel()
            if (this@AllenBaseActivity is UIActivity && readyDownloadCancelListener != null) {
                readyDownloadCancelListener?.onCancel()
            } else if (this@AllenBaseActivity is DownloadFailedActivity && downloadFailedCancelListener != null) {
                downloadFailedCancelListener?.onCancel()
            } else if (this@AllenBaseActivity is DownloadingActivity && downloadingCancelListener != null) {
                downloadingCancelListener?.onCancel()
            } else {
                null
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun receiveEvent(commonEvent: CommonEvent<*>) {
        if (commonEvent.eventType == AllenEventType.CLOSE) {
            finish()
            EventBus.getDefault().removeStickyEvent(commonEvent)
        }
    }

    abstract fun showDefaultDialog()
    abstract fun showCustomDialog()
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}



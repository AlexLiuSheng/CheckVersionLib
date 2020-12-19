package com.allneliu.appcompatsample

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder
import com.allenliu.versionchecklib.v2.builder.UIData
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendDefaultReuqest()
    }

    private fun sendDefaultReuqest() {
        val builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl("https://www.baidu.com")
                .request(object : RequestVersionListener {
                    @Nullable
                    override fun onRequestVersionSuccess(downloadBuilder: DownloadBuilder, result: String): UIData? {
//                            V2.1.1可以根据服务器返回的结果，动态在此设置是否强制更新等
//                            downloadBuilder.setForceUpdateListener(() -> {
//                                forceUpdate();
//                            });
                        Toast.makeText(this@MainActivity, "request successful", Toast.LENGTH_SHORT).show()
                        return UIData.create().setDownloadUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk")
                    }

                    override fun onRequestVersionFailure(message: String) {
                        Toast.makeText(this@MainActivity, "request failed", Toast.LENGTH_SHORT).show()
                    }
                })

        builder.setForceRedownload(true)
        builder.executeMission(this)
    }
}
package com.allenliu.sample.v1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.sample.R;
import com.allenliu.versionchecklib.core.VersionDialogActivity;
import com.allenliu.versionchecklib.callback.APKDownloadListener;
import com.allenliu.versionchecklib.callback.DialogDismissListener;
import com.allenliu.versionchecklib.callback.CommitClickListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;

import java.io.File;

/**
 * @author allenliu
 * @email alexliusheng@163.com
 * @link :http://github.com/alexliusheng
 * 注意为了展示本库的所有功能
 * 所以代码看上去会比较多，不过都是重写方法和监听回调
 * 如果不想自定义界面和一些自定义功能不用设置
 * versionParams.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
 * 使用库默认自带的就行了
 * @important 如果要重写几个ui:
 * ，请分别使用父类的versionDialog／loadingDialog/failDialog以便库管理显示和消失
 */
public class CustomVersionDialogActivity extends VersionDialogActivity implements CommitClickListener, DialogDismissListener, APKDownloadListener {
    public static int customVersionDialogIndex = 3;
    public static boolean isForceUpdate = false;
    public static boolean isCustomDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这里是几个回调
        setApkDownloadListener(this);
        setCommitClickListener(this);
        setDialogDimissListener(this);
    }

    /**
     * 下载文件成功也关闭app
     * 也判断是否强制更新
     *
     * @param file
     */
    @Override
    public void onDownloadSuccess(File file) {
        forceCloseApp();
        Log.e("CustomVersionDialogActi", "文件下载成功回调");
    }

    @Override
    public void onDownloadFail() {

    }

    @Override
    public void onDownloading(int progress) {

//        Log.e("CustomVersionDialogActi", "正在下载中回调...");
    }

    @Override
    public void onCommitClick() {
        Log.e("CustomVersionDialogActi", "确认按钮点击回调");
    }


    /**
     * 自定义更新展示界面 直接重写此方法就好
     */
    @Override
    public void showVersionDialog() {
        //使用默认的提示框直接调用父类的方法,如果需要自定义的对话框，那么直接重写此方法
        // super.showVersionDialog();
        if (customVersionDialogIndex == 1) {
            customVersionDialogOne();
        } else if (customVersionDialogIndex == 2) {
            customVersionDialogTwo();
        } else {
            super.showVersionDialog();
        }
        Toast.makeText(this, "重写此方法显示自定义对话框", Toast.LENGTH_SHORT).show();
    }

    /**
     * 自定义dialog one
     * 使用父类的versionDialog字段来初始化
     *
     * @see customVersionDialogTwo
     */
    private void customVersionDialogOne() {
        versionDialog = new BaseDialog(this, R.style.BaseDialog, R.layout.custom_dialog_one_layout);
        //设置dismiss listener 用于强制更新会回调dialogDismiss方法
        versionDialog.setOnDismissListener(this);
        TextView tvCancel = (TextView) versionDialog.findViewById(R.id.versionchecklib_version_dialog_cancel);
        TextView tvUpdate = (TextView) versionDialog.findViewById(R.id.versionchecklib_version_dialog_commit);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                versionDialog.dismiss();
                finish();
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                versionDialog.dismiss();
                //交给父类处理 （包括了静默下载判断）
                CustomVersionDialogActivity.super.dealAPK();
            }
        });
        versionDialog.show();
    }

    /**
     * 自定义dialog two
     */
    private void customVersionDialogTwo() {
        versionDialog = new BaseDialog(this, R.style.BaseDialog, R.layout.custom_dialog_two_layout);
        TextView tvTitle = (TextView) versionDialog.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) versionDialog.findViewById(R.id.tv_msg);
        Button btnUpdate = (Button) versionDialog.findViewById(R.id.versionchecklib_version_dialog_commit);

        versionDialog.show();
        //设置dismiss listener 用于强制更新,dimiss会回调dialogDismiss方法
        versionDialog.setOnDismissListener(this);
        //可以使用之前从service传过来的一些参数比如：title。msg，downloadurl，parambundle
        tvTitle.setText(getVersionTitle());
        tvMsg.setText(getVersionUpdateMsg());
        //可以使用之前service传过来的值
        Bundle bundle = getVersionParamBundle();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                versionDialog.dismiss();
                CustomVersionDialogActivity.super.dealAPK();

            }
        });
        versionDialog.show();
    }

    /**
     * 自定义下载失败重试对话框
     * 使用父类的failDialog
     */
    @Override
    public void showFailDialog() {
        super.showFailDialog();
        Toast.makeText(this, "重写此方法使用自定义失败加载框", Toast.LENGTH_SHORT).show();
    }


    View loadingView;

    /**
     * 要更改下载中界面 只需要重写此方法即可
     * 因为下载的时候会不断回调此方法
     * dialog使用全局 只初始化一次
     * 使用父类的loadingDialog保证下载成功会dimiss掉dialog
     *
     * @param currentProgress
     */
    @Override
    public void showLoadingDialog(int currentProgress) {
        if (!isCustomDownloading) {
            super.showLoadingDialog(currentProgress);
        } else {
            //使用父类的loadingDialog保证下载成功会dimiss掉dialog
            if (loadingDialog == null) {
                loadingView = LayoutInflater.from(this).inflate(R.layout.custom_download_layout, null);
                loadingDialog = new AlertDialog.Builder(this).setTitle("").setView(loadingView).create();
                loadingDialog.setCancelable(true);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        AllenHttp.getHttpClient().dispatcher().cancelAll();
                    }
                });
            }
            ProgressBar pb = (ProgressBar) loadingView.findViewById(com.allenliu.versionchecklib.R.id.pb);
            TextView tvProgress = (TextView) loadingView.findViewById(com.allenliu.versionchecklib.R.id.tv_progress);
            tvProgress.setText(String.format(getString(com.allenliu.versionchecklib.R.string.versionchecklib_progress), currentProgress));
            pb.setProgress(currentProgress);
            loadingDialog.show();
        }
//        Toast.makeText(this, "显示自定义的下载加载框", Toast.LENGTH_SHORT).show();
    }


    /**
     * versiondialog dismiss 的时候会回调此方法
     * 这里面可以进行强制更新操作
     * <p>
     * 建议用一个ActivityManger记录每个Activity出入堆栈
     * 最后全部关闭activity 实现app exit
     * ActivityTaskManger.finishAllActivity();
     *
     * @param dialog
     */
    @Override
    public void dialogDismiss(DialogInterface dialog) {
        Log.e("CustomVersionDialogActi", "dialog dismiss 回调");
//        finish();
        forceCloseApp();
    }

    /**
     * 在dialogDismiss和onDownloadSuccess里面强制更新
     * 分别表示两种情况：
     * 一种用户取消下载  关闭app
     * 一种下载成功安装的时候 应该也关闭app
     */
    private void forceCloseApp() {
        if (isForceUpdate) {
            //我这里为了简便直接finish 就行了
            V1Activity.mainActivity.finish();
        }
    }

}

package com.allenliu.versionchecklib.utils; /**
 * Copyright 2014 Zhenguo Jin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.allenliu.versionchecklib.core.VersionFileProvider;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.callback.CustomInstallListener;

import java.io.File;

/**
 *
 */
public final class AppUtils {

    /**
     * Don't let anyone instantiate this class.
     */
    private AppUtils() {
        throw new Error("Do not need instantiate!");
    }

    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri,
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        AllenVersionChecker.getInstance().cancelAllMission();
    }

    public static void installApk(Context context, File file, CustomInstallListener listener) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        if (listener != null) {
            listener.install(context, uri);
        } else {
            intent.setDataAndType(uri,
                    "application/vnd.android.package-archive");
            context.startActivity(intent);

            AllenVersionChecker.getInstance().cancelAllMission();
        }
    }

    public static boolean checkAPKIsExists(Context context, String downloadPath) {
        return checkAPKIsExists(context, downloadPath, null);

    }

    /**
     * @param context
     * @param downloadPath
     * @param newestVersionCode 开发者认为的最新的版本号
     * @return
     */
    public static boolean checkAPKIsExists(Context context, String downloadPath, Integer newestVersionCode) {
        File file = new File(downloadPath);
        boolean result = false;
        if (file.exists()) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(downloadPath,
                        PackageManager.GET_ACTIVITIES);
                //判断安装包存在并且包名一样并且版本号不一样
                ALog.e("本地安装包版本号：" + info.versionCode + "\n 当前app版本号：" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
                if (context.getPackageName().equalsIgnoreCase(info.packageName) && context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode != info.versionCode) {
                    //判断开发者传入的最新版本号是否大于缓存包的版本号，大于那么相当于没有缓存
                    if (newestVersionCode != null && info.versionCode < newestVersionCode) {
                        result = false;
                    } else
                        result = true;

                }
            } catch (Exception e) {
                result = false;
            }
        }
        return result;

    }

}
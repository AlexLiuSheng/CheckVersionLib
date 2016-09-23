package com.allenliu.versionchecklib;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileHelper {
	
	private static final int FILE_BUFFER_SIZE = 51200;

	/**
	 * 系统软件更新缓存路径
	 */
	public static String getDownloadApkCachePath(Context context) {

		String appCachePath = null;

		String apkDownPath = "" + System.currentTimeMillis();
		/**
		 * 内存卡是否可用
		 */
		if (checkSDCard()) {
			appCachePath = Environment.getExternalStorageDirectory() + "/AllenVersionPath/" + apkDownPath;
		} else {
			appCachePath = Environment.getDataDirectory().getPath() + "/AllenVersionPath/" + apkDownPath;
		}
		File file = new File(appCachePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		LogUtil.e("当前DownloadApkCachePath缓存地址------->" + appCachePath);
		return appCachePath;
	}



	/**
	 * 获取SD卡的路径
	 * @return
	 */
	public static boolean checkSDCard() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

		return sdCardExist;

	}



}
package com.allenliu.versionchecklib.core.http;


import android.os.Handler;
import android.os.Looper;

import com.allenliu.versionchecklib.utils.ALog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by allenliu on 2017/8/31.
 */

public abstract class FileCallBack implements Callback {
    private String path;
    private String name;
    private Handler handler;

    public FileCallBack(String path, String name) {
        this.path = path;
        this.name = name;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, IOException e) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                onDownloadFailed();
            }
        });

    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        // 储存下载文件的目录
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        try {
            is = response.body().byteStream();
            long total = response.body().contentLength();


            final File file = new File(path, name);
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                total = response.body().contentLength();
//                ALog.e("file total size:"+total);
                fos.write(buf, 0, len);
                sum += len;
                final int progress = (int) (((double) sum / total) * 100);
                // 下载中
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onDownloading(progress);
                    }
                });

            }
            fos.flush();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(file, call, response);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadFailed();

                }
            });
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public abstract void onSuccess(File file, Call call, Response response);

    public abstract void onDownloading(int progress);

    public abstract void onDownloadFailed();
}

package com.allenliu.versionchecklib.v2.net;

import android.os.Handler;
import android.os.Looper;

import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.core.http.FileCallBack;
import com.allenliu.versionchecklib.v2.callback.DownloadListenerKt;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by allenliu on 2018/1/18.
 */

public class DownloadMangerV2 {
    private static int sLastProgress = 0;

    public static void download(final String url, final String downloadApkPath, final String fileName, final DownloadListenerKt listener) {
        sLastProgress = 0;
        if (url != null && !url.isEmpty()) {
            Request request = new Request
                    .Builder()
                    //#issue 220
                    .addHeader("Accept-Encoding", "identity")
                    .url(url).build();
//            mockDownload(listener);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null && !listener.isDisposed())
                        listener.onCheckerStartDownload();
                }
            });

            AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(downloadApkPath, fileName) {
                @Override
                public void onSuccess(final File file, Call call, Response response) {
                    sLastProgress = 0;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null && !listener.isDisposed())
                                listener.onCheckerDownloadSuccess(file);
                        }
                    });
                }

                @Override
                public void onDownloading(final int progress) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null && !listener.isDisposed() && progress - sLastProgress > 5) {
                                listener.onCheckerDownloading(progress);
                                sLastProgress = progress;
                            }


                        }
                    });

                }

                @Override
                public void onDownloadFailed() {
                    sLastProgress = 0;
                    handleFailed(listener);
                }
            });
//

        } else {
            throw new RuntimeException("you must set download url for download function using");
        }
    }

    private static void mockDownload(DownloadListenerKt listener) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (listener != null && !listener.isDisposed())
                    listener.onCheckerStartDownload();
            }
        });
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int finalI = i;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null && !listener.isDisposed())
                        listener.onCheckerDownloading(finalI);
                }
            });
        }
        handleFailed(listener);
    }

    private static void handleFailed(final DownloadListenerKt listener) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (listener != null && !listener.isDisposed())
                    listener.onCheckerDownloadFail();

            }
        });
    }

}

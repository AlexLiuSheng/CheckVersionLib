package com.allenliu.versionchecklib.v2.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.RequestVersionBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/4/30
 * @since 1.0
 */
public class RequestVersionManager {
//    private boolean isCanceled=false;
    private Handler handler = new Handler(Looper.getMainLooper());
    public static RequestVersionManager getInstance() {
        return Holder.instance;
    }

    public static class Holder {
        static RequestVersionManager instance = new RequestVersionManager();

    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receiveEvent(CommonEvent commonEvent) {
//        if(commonEvent.getEventType()== AllenEventType.CLOSE){
//           isCanceled=true;
//        }
//    }
//
//    private void registerEventBus() {
//        isCanceled=false;
//        if(!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
//    }
//    private void unregister(){
//
//        if(EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//    }

    /**
     * 请求版本接口
     * #issue 239
     */
    public void requestVersion(final DownloadBuilder builder, final Context context) {
//        registerEventBus();
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                RequestVersionBuilder requestVersionBuilder = builder.getRequestVersionBuilder();
                OkHttpClient client = AllenHttp.getHttpClient();
                HttpRequestMethod requestMethod = requestVersionBuilder.getRequestMethod();
                Request request = null;
                switch (requestMethod) {
                    case GET:
                        request = AllenHttp.get(requestVersionBuilder).build();
                        break;
                    case POST:
                        request = AllenHttp.post(requestVersionBuilder).build();
                        break;
                    case POSTJSON:
                        request = AllenHttp.postJson(requestVersionBuilder).build();
                        break;
                }
                final RequestVersionListener requestVersionListener = requestVersionBuilder.getRequestVersionListener();
                if (requestVersionListener != null) {
                    try {
                        final Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            final String result = response.body() != null ? response.body().string() : null;
                              post(new Runnable() {
                                  @Override
                                  public void run() {

                                          UIData versionBundle = requestVersionListener.onRequestVersionSuccess(builder, result);
                                          if (versionBundle != null) {
                                              builder.setVersionBundle(versionBundle);
                                              builder.download(context);
                                          }
                                      }


                              });
                        } else {
                          post(new Runnable() {
                                @Override
                                public void run() {
                                    requestVersionListener.onRequestVersionFailure(response.message());
                                    AllenVersionChecker.getInstance().cancelAllMission();
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                requestVersionListener.onRequestVersionFailure(e.getMessage());
                                AllenVersionChecker.getInstance().cancelAllMission();
                            }
                        });
                    }
                } else {
                    throw new RuntimeException("using request version function,you must set a requestVersionListener");
                }
//                unregister();
            }
        });

    }
    private void post(Runnable r){
        handler.post(r);
    }
}

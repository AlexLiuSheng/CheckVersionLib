package com.allenliu.versionchecklib.v2;

import android.content.Context;

import androidx.annotation.Nullable;

import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.utils.AllenEventBusUtil;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.RequestVersionBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;

/**
 * Created by allenliu on 2018/1/12.
 */

public class AllenVersionChecker {

    //    public AllenVersionChecker() {
//        throw new RuntimeException("AllenVersionChecker can not be instantiated from outside");
//    }
    private AllenVersionChecker() {

    }

    public static AllenVersionChecker getInstance() {
        return AllenVersionCheckerHolder.allenVersionChecker;
    }

    private static class AllenVersionCheckerHolder {
        public static final AllenVersionChecker allenVersionChecker = new AllenVersionChecker();
    }

    @Deprecated
    public void cancelAllMission(Context context) {
        cancelAllMission();
    }

    public void cancelAllMission() {
        AllenHttp.getHttpClient().dispatcher().cancelAll();
//        Intent intent = new Intent(context.getApplicationContext(), VersionService.class);
//        context.getApplicationContext().stopService(intent);
        AllenEventBusUtil.sendEventBus(AllenEventType.CLOSE);
        AllenEventBusUtil.sendEventBus(AllenEventType.STOP_SERVICE);
    }

    /**
     * @param versionBundle developer should return version bundle ,to use when showing UI page,could be null
     * @return download builder for download setting
     */
    public DownloadBuilder downloadOnly(@Nullable UIData versionBundle) {
        return new DownloadBuilder(null, versionBundle);
    }

    /**
     * use request version function
     *
     * @return requestVersionBuilder
     */
    public RequestVersionBuilder requestVersion() {
        return new RequestVersionBuilder();
    }

}

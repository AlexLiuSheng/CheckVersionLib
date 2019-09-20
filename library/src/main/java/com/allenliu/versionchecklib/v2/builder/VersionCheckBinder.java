package com.allenliu.versionchecklib.v2.builder;

import android.os.Binder;

import com.allenliu.versionchecklib.v2.ui.VersionService;

/**
 * @author AllenLiu
 * @date 2019/9/20
 */
public class VersionCheckBinder extends Binder {
    private VersionService versionService;

    public VersionCheckBinder(VersionService versionService) {
        this.versionService = versionService;
    }

    public VersionCheckBinder setDownloadBuilder(DownloadBuilder downloadBuilder) {
        versionService.setBuilder(downloadBuilder);
        return this;
    }

}

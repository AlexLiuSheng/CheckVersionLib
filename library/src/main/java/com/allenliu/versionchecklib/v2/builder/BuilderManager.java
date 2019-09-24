package com.allenliu.versionchecklib.v2.builder;

/**
 * @author AllenLiu
 * @date 2019/9/24
 */
public class BuilderManager {
    private DownloadBuilder downloadBuilder;

    public DownloadBuilder getDownloadBuilder() {
        return downloadBuilder;
    }

    public BuilderManager setDownloadBuilder(DownloadBuilder downloadBuilder) {
        this.downloadBuilder = downloadBuilder;
        return this;
    }

    public void destory() {
        if(downloadBuilder!=null){
            downloadBuilder.destory();
        }
    }

    private static class Holder{
        static BuilderManager instance=new BuilderManager();
    }
    public static BuilderManager getInstance(){
        return Holder.instance;
    }

}

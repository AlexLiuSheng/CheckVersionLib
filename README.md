## CheckVersionLib[ ![Download](https://api.bintray.com/packages/zkxy/maven/VersionCheckLib/images/download.svg) ](https://bintray.com/zkxy/maven/VersionCheckLib/_latestVersion)
## V2 Version has been born with shocking, strong functions,chain programing, easy to integrate,strong extension
[中文文档](https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/README_UN.MD)

The strongest feature is easier to integrate than version  of V1.+

### Effect
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/v2.jpg" width=200/><img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/V2.gif" width=200/>
 
### Features
- [x] Invoke everywhere you want

- [x] **Easy**

- [x] **Strong Extension**

- [x] Adapt to all applications that have update function

- [x] **Customize Ui**

- [x] Support Force Update（one line code）

- [x] Support Silence Download （one line code）

- [x] Adapt to Android O

### include

```
compile 'com.allenliu.versionchecklib:library:2.1.9'
```

### usage



> Only using download mode

the easiest way to use

```
        AllenVersionChecker
                .getInstance()
                .downloadOnly(
                        UIData.create().setDownloadUrl(downloadUrl)
                )
                .excuteMission(context);
```

`UIData`：UIData is the type of Bundle，it saves some data for displaying ui page，it can use in your customization page/



> Request Version + Download mode

the easiest way to call 
```
   AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(requestUrl)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        //get the data response from server,parse,get the `downloadUlr` and some other ui date
                      
                        ...
                        //return null if you dont want to update application
                        return UIData.create().setDownloadUrl(downloadUrl);
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                })
                .executeMission(context);


```
Some other http params for request app version,as follows

```
 AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setHttpHeaders(httpHeader)
                .setRequestMethod(HttpRequestMethod.POSTJSON)
                .setRequestParams(httpParam)
                .setRequestUrl(requestUrl)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        //get the data response from server,parse,get the `downloadUlr` and some other ui date
                        ...
                        UIData uiData = UIData
                                .create()
                                .setDownloadUrl(downloadUrl)
                                .setTitle(updateTitle)
                                .setContent(updateContent);
                        //return null if you dont want to update application
                        uiData.getVersionBundle().putString("key", "your value");
                        return uiData;

                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                })
                .executeMission(context);
```

the instructions above is the basic using for integrating(library has a set of default ui page),you can use some other params,if it does not fit your requirement the above.

### some other functions
first of all,the builder of follow is called `DownloadBuilder`
```
 DownloadBuilder builder=AllenVersionChecker
                .getInstance()
                .downloadOnly();
                
                
      or          
                
                
                
 DownloadBuilder builder=AllenVersionChecker
                 .getInstance()
                 .requestVersion()
                 .request()
```
> cancel mission

 ```
  AllenVersionChecker.getInstance().cancelAllMission(this);

```
> silent download
  
  ```
   builder.setSilentDownload(true); false for default
  ```
> set the newest version code of your server returned，it is used to verify if use file cache.
 
  - Cache category：first check running app's versionCode whether equal with the installation package.Then check developer whether pass the newest VersionCode ,if so, check the 
   VersionCode is greater than local,if it is truth ,download apk from server, otherwise use cache.
  ```
   builder.setNewestVersionCode(int); null for default 
  ```
> Force Update

  set the listener represent need force update function,it will be call when user cancel the download operation,developer need close all the activities of application.
  ```
  builder.setForceUpdateListener(() -> {
                forceUpdate();
            });
```    
> Force ReDownload no matter there is cache


  
```
 builder.setForceRedownload(true); false for default
``` 

> set whether show downloading dialog
```
builder.setShowDownloadingDialog(false); true for default
```
> set whether  show notification

```
builder.setShowNotification(false);  true for default 
```
> customize notification
```
      builder.setNotificationBuilder(
                 NotificationBuilder.create()
                         .setRingtone(true)
                         .setIcon(R.mipmap.dialog4)
                         .setTicker("custom_ticker")
                         .setContentTitle("custom title")
                         .setContentText(getString(R.string.custom_content_text))
         );
```
> set whether show download failed dialog

```
  builder.setShowDownloadFailDialog(false); true for default
```
> customize download apk path

```
  builder.setDownloadAPKPath(address); default：/storage/emulated/0/AllenVersionPath/
```
> customize download apk name
```
  builder.setApkName(apkName); default：getPackageName()
```
> set download listener

```
   builder.setApkDownloadListener(new APKDownloadListener() {
             @Override
             public void onDownloading(int progress) {
                 
             }

             @Override
             public void onDownloadSuccess(File file) {

             }

             @Override
             public void onDownloadFail() {

             }
         });
```
> cancel listener
```

 builder.setOnCancelListener(() -> {
            Toast.makeText(V2Activity.this,"Cancel Hanlde",Toast.LENGTH_SHORT).show();
        });
```
> silent download+install directly（dont popup update dialog）
```
    builder.setDirectDownload(true);
           builder.setShowNotification(false);
           builder.setShowDownloadingDialog(false);
           builder.setShowDownloadFailDialog(false);
```
> customize install callback
```
  setCustomDownloadInstallListener(CustomInstallListener customDownloadInstallListener)
```

### customize the ui page

Customization page used the way of listener,developer need return the Dialog(parent:android.app) that you customized


 - all the dialog must initiate with the context inside the listener.
 
 - the data fo page takes from UIData

> **Customize Show Version Dialog**

   set`CustomVersionDialogListener`
   

- define the page **must** have a commit download button,the id of button must be `@id/versionchecklib_version_dialog_commit`

- if has cancel button(ignore if not),the id of button must be `@id/versionchecklib_version_dialog_cancel`

eg.

```
  builder.setCustomVersionDialogListener((context, versionBundle) -> {
            BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_dialog_one_layout);
            //versionBundle is instance of UIData，passed from developer,it can be use to display 
            TextView textView = baseDialog.findViewById(R.id.tv_msg);
            textView.setText(versionBundle.getContent());
            return baseDialog;
        });

```

> **customize downloading dialog page**

set`CustomDownloadingDialogListener`


- if has cancel button(ignore if not),the id of button must be`@id/versionchecklib_loading_dialog_cancel`


```
    builder.setCustomDownloadingDialogListener(new CustomDownloadingDialogListener() {
            @Override
            public Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
                BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_layout);
                return baseDialog;
            }
// loop invoke the updateUI method when downloading
            @Override
            public void updateUI(Dialog dialog, int progress, UIData versionBundle) {
                TextView tvProgress = dialog.findViewById(R.id.tv_progress);
                ProgressBar progressBar = dialog.findViewById(R.id.pb);
                progressBar.setProgress(progress);
                tvProgress.setText(getString(R.string.versionchecklib_progress, progress));
            }
        });
```

> **customize download failed page**

setCustomDownloadFailedListener

- if having button of **retry**,the id must be`@id/versionchecklib_failed_dialog_retry`

- if having the button of **commit/cancel**,the id must be `@id/versionchecklib_failed_dialog_cancel`

```
   builder.setCustomDownloadFailedListener((context, versionBundle) -> {
            BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_failed_dialog);
            return baseDialog;
        });
```
***

###  ProGuard
```
   -keepattributes Annotation
   -keepclassmembers class * {    @org.greenrobot.eventbus.Subscribe ;}
   -keep enum org.greenrobot.eventbus.ThreadMode { *; }
   -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {    (java.lang.Throwable);}
   -keep class com.allenliu.versionchecklib.**{*;}
```


### Last

***

 - download the  [demo](https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/sample/src/main/java/com/allenliu/sample/v2/V2Activity.java) to view  more functions
 
 - thanks all for the support library
 
 - star/issue is welcome
 


### License

***

Apache 2.0

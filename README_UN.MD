## CheckVersionLib[ ![Download](https://api.bintray.com/packages/zkxy/maven/VersionCheckLib/images/download.svg) ](https://bintray.com/zkxy/maven/VersionCheckLib/_latestVersion)
## V2版震撼来袭，功能强大，链式编程，调用简单，集成轻松，扩展性强大

老规矩先看V2效果，这个版本最大的特点就是使用非常简单，相对于1.+版本

### 效果
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/v2.jpg" width=200/><img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/V2.gif" width=200/>
 
### 特点
- [x] 任何地方都可以调用

- [x] **简单简单简单简单**（重要的话我说四遍）

- [x] **扩展性强大**

- [x] 所有具有升级功能的app均可使用，耶稣说的

- [x] **更强大的自定义界面支持**

- [x] 支持强制更新（一行代码）

- [x] 支持静默下载 （一行代码）

- [x] 适配到Android O

### 导入

```
compile 'com.allenliu.versionchecklib:library:2.1.9'
```

### 使用

和1.+版本一样，两种模式

> 只使用下载模式

先来个最简单的调用

```
        AllenVersionChecker
                .getInstance()
                .downloadOnly(
                        UIData.create().setDownloadUrl(downloadUrl)
                )
                .excuteMission(context);
```

`UIData`：UIData是一个Bundle，用于存放用于UI展示的一些数据，后面自定义界面时候可以拿来用



> 请求服务器版本+下载

该模式最简单的使用
```
   AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(requestUrl)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        //拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
                        ...
                        //如果是最新版本直接return null
                        return UIData.create().setDownloadUrl(downloadUrl);
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                })
                .executeMission(context);


```
请求版本一些其他的http参数可以设置，如下

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
                        //拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
                        ...
                        UIData uiData = UIData
                                .create()
                                .setDownloadUrl(downloadUrl)
                                .setTitle(updateTitle)
                                .setContent(updateContent);
                        //放一些其他的UI参数，拿到后面自定义界面使用
                        uiData.getVersionBundle().putString("key", "your value");
                        return uiData;

                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                })
                .executeMission(context);
```

以上就是最基本的使用（库默认会有一套界面），如果还不满足项目需求，下面就可以用这个库来飙车了

### 一些其他的function设置
解释下，下面的builder叫`DownloadBuilder`
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
> 取消任务

 ```
  AllenVersionChecker.getInstance().cancelAllMission(this);

```
> 静默下载
  
  ```
   builder.setSilentDownload(true); 默认false
  ```
> 设置当前服务器最新的版本号，供库判断是否使用缓存
 
  - 缓存策略：如果本地有安装包，首先判断与当前运行的程序的versionCode是否不一致，然后判断是否有传入最新的
    versionCode，如果传入的versionCode大于本地的，重新从服务器下载，否则使用缓存
  
  ```
   builder.setNewestVersionCode(int); 默认null
  ```
> 强制更新

  设置此listener即代表需要强制更新，会在用户想要取消下载的时候回调
  需要你自己关闭所有界面
  ```
  builder.setForceUpdateListener(() -> {
                forceUpdate();
            });
```    
> 下载忽略本地缓存

  如果本地有安装包缓存也会重新下载apk
  
```
 builder.setForceRedownload(true); 默认false
``` 

> 是否显示下载对话框
```
builder.setShowDownloadingDialog(false); 默认true
```
> 是否显示通知栏

```
builder.setShowNotification(false);  默认true
```
> 自定义通知栏
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
> 是否显示失败对话框

```
  builder.setShowDownloadFailDialog(false); 默认true
```
> 自定义下载路径

```
  builder.setDownloadAPKPath(address); 默认：/storage/emulated/0/AllenVersionPath/
```
> 自定义下载文件名
```
  builder.setApkName(apkName); 默认：getPackageName()
```
> 可以设置下载监听

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
> 设置取消监听
```

 builder.setOnCancelListener(() -> {
            Toast.makeText(V2Activity.this,"Cancel Hanlde",Toast.LENGTH_SHORT).show();
        });
```
> 静默下载+直接安装（不会弹出升级对话框）
```
    builder.setDirectDownload(true);
           builder.setShowNotification(false);
           builder.setShowDownloadingDialog(false);
           builder.setShowDownloadFailDialog(false);
```
> 自定义安装回调
```
    setCustomDownloadInstallListener(CustomInstallListener customDownloadInstallListener)
```

### 自定义界面

自定义界面使用回调方式，开发者需要返回自己定义的Dialog（父类android.app）

 - 所有自定义的界面必须使用listener里面的context实例化
 
 - 界面展示的数据通过UIData拿

> **自定义显示更新界面**

   设置`CustomVersionDialogListener`
   

- 定义此界面**必须**有一个确定下载的按钮，按钮id必须为`@id/versionchecklib_version_dialog_commit`

- 如果有取消按钮（没有忽略本条要求），则按钮id必须为`@id/versionchecklib_version_dialog_cancel`

eg.

```
  builder.setCustomVersionDialogListener((context, versionBundle) -> {
            BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_dialog_one_layout);
            //versionBundle 就是UIData，之前开发者传入的，在这里可以拿出UI数据并展示
            TextView textView = baseDialog.findViewById(R.id.tv_msg);
            textView.setText(versionBundle.getContent());
            return baseDialog;
        });

```

> **自定义下载中对话框界面**

设置`CustomDownloadingDialogListener`


- 如果此界面要设计取消操作（没有忽略），请务必将id设置为`@id/versionchecklib_loading_dialog_cancel`


```
    builder.setCustomDownloadingDialogListener(new CustomDownloadingDialogListener() {
            @Override
            public Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
                BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_layout);
                return baseDialog;
            }
//下载中会不断回调updateUI方法
            @Override
            public void updateUI(Dialog dialog, int progress, UIData versionBundle) {
                TextView tvProgress = dialog.findViewById(R.id.tv_progress);
                ProgressBar progressBar = dialog.findViewById(R.id.pb);
                progressBar.setProgress(progress);
                tvProgress.setText(getString(R.string.versionchecklib_progress, progress));
            }
        });
```

> **自定义下载失败对话框**

设置CustomDownloadFailedListener

- 如果有**重试**按钮请将id设置为`@id/versionchecklib_failed_dialog_retry`

- 如果有 **确认/取消**按钮请将id设置为`@id/versionchecklib_failed_dialog_cancel`

```
   builder.setCustomDownloadFailedListener((context, versionBundle) -> {
            BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_failed_dialog);
            return baseDialog;
        });
```
***

###  混淆配置
```
   -keepattributes Annotation
   -keepclassmembers class * {    @org.greenrobot.eventbus.Subscribe ;}
   -keep enum org.greenrobot.eventbus.ThreadMode { *; }
   -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {    (java.lang.Throwable);}
   -keep class com.allenliu.versionchecklib.**{*;}
```


### 最后

***

 - 更全面的使用请看 [demo](https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/sample/src/main/java/com/allenliu/sample/v2/V2Activity.java)
 
 - 感谢各位对本库的支持
 
 - 欢迎star/issue
 


### License

***

Apache 2.0

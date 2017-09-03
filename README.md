# CheckVersionLib[ ![Download](https://api.bintray.com/packages/zkxy/maven/VersionCheckLib/images/download.svg) ](https://bintray.com/zkxy/maven/VersionCheckLib/_latestVersion)


## 特点
- [x] 任何地方都可以检测（可设置定时检测）

- [x] app内任何地方都可以弹出升级对话框

- [x] **自定义性强，手动回调解析，适用于各种版本检测接口**

- [x] 自动处理下载和安装

- [x] 自动请求读写权限

- [x] **支持自定义界面**

- [x] 支持强制更新

- [x] 支持静默下载

- [x] 使用okhttp请求，不与第三方请求框架冲突

## 效果
 
 
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/custom.gif" width=200/> <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style4.png" width=200/> <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style1.png" width=200/>
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style2.png" width=200/>
 
## 使用步骤
### android studio导入
`compile 'com.allenliu.versionchecklib:library:1.6.3'`


### 如何使用
1.自定义service，service继承 `AVersionService `，实现其中的 `onResponses(AVersionService service, String response)`抽象方法.

该方法主要是请求版本接口的回调，由于不同的使用者版本检测接口返回数据类型不一致，所以你需要自己解析数据，判断版本号之后调用升级对话框，如果使用库默认的界面直接调用如下方法: `service.showVersionDialog(downloadUrl,title,updateMsg )`

示例代码:
           
	     
	     if (serverVersion > clientVersion) { 
	      //传入下载地址，以及版本更新消息
	     service.showVersionDialog(downloadUrl,title,updateMsg );
	    // or 
	    service.showVersionDialog(downloadUrl,title,updateMsg,bundle);
	     }
	     
	

              
2.在任意地方开启自定义service，并传入`VersionParam`

      ```
         VersionParams.Builder builder = new VersionParams.Builder()
                      .setRequestUrl("http://www.baidu.com")
                      .setService(DemoService.class);
                      
         AllenChecker.startVersionCheck(this, builder.build());
      ```
	
	
   `VersionParams`属性见下表：
 
   | 属性名        | 是否必须           | 默认值 |解释|
   | ------------- |:-------------|:-------------|:-------------:|
   | requestUrl   | 是 |-|请求版本接口的url|
   | service   | 是 |-|指定你自己的service|
   |downloadAPKPath|否|/storage/emulated/0/AllenVersionPath/|apk下载路径|
   | httpHeaders   | 否 |不传为空|http版本请求header|
   | pauseRequestTime   | 否 |1000*30|版本接口请求失败与下次请求间隔时间（如果为-1表示请求失败不继续请求）|
   | httpHeaders   | 否 |不传为空|http版本请求header|
   | requestMethod   | 否 |GET|http版本请求方式|
   | requestParams   | 否 |不传为空|http版本请求携带的参数|
   | customDownloadActivityClass   | 否 |VersionDialogActivity.class|版本dialog Activity,使用默认界面不指定|
   | isForceRedownload   | 否 |false|如果本地有缓存，是否强制重新下载apk(设置false会如果下载了安装包而用户没有安装则不会再次下载)|
   | isSilentDownload   | 否 |false|静默下载开关|
 
3.开启和关闭log

 `AllenChecker.init(true)`
	  
### **自定义界面** 
   如果想自定义界面，只需创建一个继承自`VersionDialogActivity`的Activity,Activity设置Theme为透明：

 ` android:theme="@style/versionCheckLibvtransparentTheme"`
 
   开启Service的之前，记住将自定义的Activity传入VersionParams
   
   `setCustomDownloadActivityClass(CustomVersionDialogActivity.class)`
   
   - 调用父类`getVersionTitle()` ,`getVersionUpdateMsg()`,`getVersionParamBundle()`方法,这是从service传过来的值，可以在自定义界面使用
   
   - 自定义 `versionDialog`：
     重写 `showVersionDialog()` ,在里面实现自己的逻辑，在确认按钮里调用 `super.dealAPK();`
   
   - 自定义 `downloadingDialog`，重写`showLoadingDialog(int currentProgress)`,在里面实现自己的逻辑
   
   - 自定义 `failDialog` ,重写`showFailDialog`，实现自己的逻辑
   
   - 强制更新。如果使用默认的版本dialog，`dialogDismiss`和`onDownloadSuccess`回调里关闭app，具体用法请看demo
   
   - 除此之外还可以在定义的Activity里面监听一些下载和点击回调 
   
   
                 setApkDownloadListener(this);
                 setCommitClickListener(this);
                 setDialogDimissListener(this);
		 
### 强制更新

主要思路就是监听

   ```
                 setApkDownloadListener(this);
		         setDialogDimissListener(this);
   ```
 
		 
具体查看[ForceUpdate](https://github.com/AlexLiuSheng/CheckVersionLib/tree/master/ForceUpdateDemo)
 
### 下载通知栏图标和文字替换
需要自定义图标只需在mimap文件下建立`ic_launcher`图标，替换标题只需在项目xml定义`app_name`属性,还有其他一些属性替换，如下表:

| 属性名        | 属性值           | 
| ------------- |:-------------:|
| versionchecklib_confirm    | 确认 |
| versionchecklib_cancel   | 取消      |   
|versionchecklib_retry | 重试    |  
|versionchecklib_download_fail_retry| 下载失败是否重试？   |  
|versionchecklib_download_finish | 下载完成，点击安装   |  
|versionchecklib_downloading | 正在下载中...  |  
|versionchecklib_check_new_version |检测到新版本  |  
|versionchecklib_download_fail | 下载失败，点击重试|  
更详细的使用请看demo
`欢迎star和提issue`
## 更新日志

- V1.6.0
   - 优化项目，移除okgo
   - 使用okhttp实现请求和下载
- V1.5.4 [issues#27](https://github.com/AlexLiuSheng/CheckVersionLib/issues/27)
   - 解决了 [issues#27](https://github.com/AlexLiuSheng/CheckVersionLib/issues/27)
- V1.5.3 [issues#26](https://github.com/AlexLiuSheng/CheckVersionLib/issues/26)
   - 解决了 [issues#26](https://github.com/AlexLiuSheng/CheckVersionLib/issues/26)
- V1.5.2
   - 解决了 [issues#25](https://github.com/AlexLiuSheng/CheckVersionLib/issues/25)
- V1.5.1
   - solve [issues#24](https://github.com/AlexLiuSheng/CheckVersionLib/issues/24)
- V1.5
   - 增加getVersionTitle,getVersionParamBundle,getVersionUpdateMsg方法，方便自定义界面使用
   - 修复了对本地缓存apk的判断优化。只有本地安装包与当前app的包名一样并且versioncode不一样才会认为本地有apk
- V1.4
   - 修复了之前自定义界面不能使用从service传过去的title，updateMsg问题
- V1.3 
   - 增加**静默下载**功能
   - 优化库启动方式
- V1.2
   - **强制重新下载开关**功能
- V1.1
   - 添加apk下载缓存，下载完成之后默认不再次下载。安装之后删除安装包
   
## TODO
- [ ] Kotlin版本
## License
        
        Copyright 2017 AllenLiu.

        Licensed to the Apache Software Foundation (ASF) under one or more contributor
        license agreements. See the NOTICE file distributed with this work for
        additional information regarding copyright ownership. The ASF licenses this
        file to you under the Apache License, Version 2.0 (the "License"); you may not
        use this file except in compliance with the License. You may obtain a copy of
        the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
        WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
        License for the specific language governing permissions and limitations under
        the License.
  

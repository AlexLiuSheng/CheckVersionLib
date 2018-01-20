# CheckVersionLib[ ![Download](https://api.bintray.com/packages/zkxy/maven/VersionCheckLib/images/download.svg) ](https://bintray.com/zkxy/maven/VersionCheckLib/_latestVersion)

#  [V2版来袭](https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/README.MD)
[English Doc](https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/README_EN.md)
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

- [x] 适配到Android O

## 效果
 
 
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/custom.gif" width=200/> <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/main.jpg" width=200/> <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style1.png" width=200/>
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style2.png" width=200/>
 
## 使用步骤
### android studio导入
`compile 'com.allenliu.versionchecklib:library:1.8.8'`


### 如何使用
#### 1.请求版本接口 + 下载模块
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
	
#### 2.只使用下载模块


    只使用下载模块不用定义第一步的service，正常传入versiongParams参数，不设置requestUrl和service，只用设置onlyDownload 为true。并且传入downloadUrl和需要显示的信息


 ```
  //如果仅使用下载功能，downloadUrl是必须的
   builder.setOnlyDownload(true)
                .setDownloadUrl("http://down1.uc.cn/down2/zxl107821.uc/miaokun1/UCBrowser_V11.5.8.945_android_pf145_bi800_(Build170627172528).apk")
                .setTitle("检测到新版本")
                .setUpdateMsg(getString(R.string.updatecontent));
                
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
   | onlyDownload  |否|false|是否只使用下载模块|
   |title|否|null|只使用下载模块时，升级对话框的title|
   |updateMsg|否|null|只使用下载模块时，升级对话框内容|
   |downloadUrl|只使用下载模式时必须|-|只使用下载模块时传入的下载apk地址|
   |paramBundle|否|null|额外的一些参数可以放里面，可以在versiongDialogActivity里面使用|
   |isShowDownloadingDialog|否|true|是否显示下载对话框|
   |isShowNotification|否|true|是否显示下载的通知栏|
   |isShowDownloadFailDialog|否|true|是否显示下载失败对话框|
 
#### 3.开启和关闭log

 `AllenChecker.init(true)`
 
#### 4.取消请求

   取消全部请求
   
 `AllenChecker.cancelMission();`
	  
### **自定义界面** 
   如果想自定义界面，只需创建一个继承自`VersionDialogActivity`的Activity,
   Activity设置Theme为透明：

 ` android:theme="@style/versionCheckLibvtransparentTheme"`
   
   设置launchMode为SingleTask
   
   ` android:launchMode="singleTask"`
 
   记住将自定义的Activity传入VersionParams
   
   `setCustomDownloadActivityClass(CustomVersionDialogActivity.class)`
   
   - 调用父类`getVersionTitle()` ,`getVersionUpdateMsg()`,`getVersionParamBundle()`方法,这是从service传过来的值，可以在自定义界面使用
   
   - 自定义 `versionDialog`：
     重写 `showVersionDialog()` ,在里面实现自己的逻辑，在确认按钮里调用 `super.dealAPK();`

     
     example code:
   ```
     versionDialog = new BaseDialog(this, R.style.BaseDialog, R.layout.custom_dialog_two_layout);
         TextView tvTitle = (TextView) versionDialog.findViewById(R.id.tv_title);
         TextView tvMsg = (TextView) versionDialog.findViewById(R.id.tv_msg);
         Button btnUpdate = (Button) versionDialog.findViewById(R.id.btn_update);
 
         versionDialog.show();
         //设置dismiss listener 用于强制更新,dimiss会回调dialogDismiss方法
         versionDialog.setOnDismissListener(this);
         //可以使用之前从service传过来的一些参数比如：title。msg，downloadurl，parambundle
         tvTitle.setText(getVersionTitle());
         tvMsg.setText(getVersionUpdateMsg());
         //可以使用之前service传过来的值
         Bundle bundle = getVersionParamBundle();
         btnUpdate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 versionDialog.dismiss();
                 CustomVersionDialogActivity.super.dealAPK();
 
             }
         });
         versionDialog.show();
  ```
	
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
需要自定义图标只需在mimap文件下建立`ic_launcher`图标，替换标题只需在项目xml定义`app_name`属性,还有其他一些属性替换（[仍然被替换？](https://github.com/AlexLiuSheng/CheckVersionLib/issues/83)），如下表:

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
V 1.8.8

   - fix the oom of static context
   
V 1.8.6

   - fix [issue80](https://github.com/AlexLiuSheng/CheckVersionLib/issues/80)
   
V 1.8.4
   - support android 8
   
V 1.8.3 
   - fix [issue73](https://github.com/AlexLiuSheng/CheckVersionLib/issues/73)
   - fix [issue75](https://github.com/AlexLiuSheng/CheckVersionLib/issues/75)

V 1.8.2
   - fix bug of silent downloading 
  
V 1.8.0
   - fix bugs of force update
   - fix [issue](https://github.com/AlexLiuSheng/CheckVersionLib/issues/68)
- V1.7.7
   - fix [issue](https://github.com/AlexLiuSheng/CheckVersionLib/issues/64)
   - fix re downloading apk when app have cache of apk
   - fix [issue](https://github.com/AlexLiuSheng/CheckVersionLib/issues/63)
- V1.7.6
   - support cancel all the task
- V1.7.5
   - support multiple language mode
- V1.7.4
   - solve [issues#59](https://github.com/AlexLiuSheng/CheckVersionLib/issues/59)
- V1.7.2
   - support https request
- V1.7.1
   - 增加是否显示下载通知栏开关
   - 增加是否显示下载对话框开关
- V1.6.9
   - VersionDialogActivity statusbar transparent
- V1.6.8
   - 增加只使用下载功能用法
- V1.6.6
   - 解决[issues#33](https://github.com/AlexLiuSheng/CheckVersionLib/issues/33)
- V1.6.5
   - 解决了[issues#32](https://github.com/AlexLiuSheng/CheckVersionLib/issues/32)
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
  

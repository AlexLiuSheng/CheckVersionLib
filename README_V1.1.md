# CheckVersionLib[ ![Download](https://api.bintray.com/packages/zkxy/maven/VersionCheckLib/images/download.svg) ](https://bintray.com/zkxy/maven/VersionCheckLib/_latestVersion)
  现在热更新技术挺火的，大公司都出了自己的热更新框架，但是各家热更新都有各自优缺点，终究不能解决所有bug，万不得已还是得进行版本升级，这是一个android上的自动版本检测并更新库。库集成了检测版本、下载版本以及自动安装升级
## 特点
- [x] 任何地方都可以检测（可设置定时检测）

- [x] app内任何地方都可以弹出升级对话框

- [x] **自定义性强，手动回调解析，适用于各种版本检测接口**

- [x] 自动处理下载和安装

- [x] 自动请求读写权限

- [x] **支持自定义界面**

- [x] 支持强制更新

## 效果
 
 
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/custom.gif" width=200/><img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style1.png" width=200/>
 <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style2.png" width=200/>
 
## 使用步骤
### android studio导入
`compile 'com.allenliu.versionchecklib:library:1.1'`


### 如何使用
1.自定义service，service继承 `AVersionService `，实现其中的 `onResponses(AVersionService service, String response)`抽象方法.

该方法主要是请求版本接口的回调，由于不同的使用者版本检测接口返回数据类型不一致，所以你需要自己解析数据，判断版本号之后调用升级对话框，如果使用库默认的界面直接调用如下方法: `service.showVersionDialog(downloadUrl,title,updateMsg )`

示例代码:
           
	     
	     if (serverVersion > clientVersion) { 
	      //传入下载地址，以及版本更新消息
	     service.showVersionDialog(downloadUrl,title,updateMsg );}
	     
	
	      

              
2.在任意地方开启自定义service，并传入`VersionParam`

        versionParams = new VersionParams().setRequestUrl("http://www.baidu.com");
        Intent intent = new Intent(this, DemoService.class);
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        startService(intent);
	
   `VersionParams`有如下方法，除了requestUrl都是可选值
 
   <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/versionparams.png" width=400/>
	  
### **自定义界面** 
   如果想自定义界面，只需创建一个继承自`VersionDialogActivity`的Activity,Activity设置Theme为透明：

 ` android:theme="@style/versionCheckLibvtransparentTheme"`
 
   开启Service的时候，将自定义的Activity传入VersionParams
   
   `setCustomDownloadActivityClass(CustomVersionDialogActivity.class)`
   
   - 自定义 版本dialog,重写 `showVersionDialog()` ,在里面实现自己的逻辑，最后调用`downloadFile(url)`或者`downloadFile(url,filecallback)`注意不要调用父类的方法
   
   - 自定义 下载中dialog，重写`showLoadingDialog(int currentProgress)`,在里面实现自己的逻辑
   
   - 自定义 下载失败dialog ,重写`showFailDialog`，实现自己的逻辑
   
   - 强制更新。如果使用默认的版本dialog，`setCancelClickListner`回调里实现，具体用法请看demo
   
   - 除此之外还可以在定义的Activity里面监听一些下载和点击回调 
  
         setOnDownloadSuccessListener(this);
	 
         setCommitClickListener(this);
	  
         setCancelClickListener(this);
	 
	     setOnDownloadingListener(this);
       
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
  

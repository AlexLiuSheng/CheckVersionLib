
This is a library that can help you update your app intelligently.library contains all kinds of functions,so that you can customize it by your requirements.

## Feature

 - [x] Check  app version everywhere（support polling check）
 
 - [x] Popup update dialog everywhere
 
 - [x] **Powerful customization functions ，Manual callback parse，it is useful for all kinds of verion api**
 
 - [x] Automatic downloading and automatic installing
 
 - [x] Request read and write permissions automatically
 
 - [x] **Support customizing UI**
 
 - [x] Support forcing update
 
 - [x] Support silent downloading
 
 - [x] Using okhttp frame，no conflict with other http request frames
 
 ## Effects
  
  
  <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/custom.gif" width=200/> <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/main.jpg" width=200/> <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style1.png" width=200/>
  <img src="https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/style2.png" width=200/>
 
 
 ### include 
    `compile 'com.allenliu.versionchecklib:library:1.7.5'`
 ### How to use
 
 #### 1.use request version api module and  downloading module
 1.customize service，service need to extend `AVersionService `，override `onResponses(AVersionService service, String response)` method.
 
 This  is a callback method when request your app version api.Because of different applications have different api，you should call the method to show update dialog after verifies version code.
 
 eg:
            
 	     
 	     if (serverVersion > clientVersion) { 
 	      //input apk downloading url and update message
 	     service.showVersionDialog(downloadUrl,title,updateMsg );
 	    // or 
 	    service.showVersionDialog(downloadUrl,title,updateMsg,bundle);
 	     }
 	     
 	
 
               
 2.this step is to set `VersionParams` and set service name into the VersionParam,lastly start to check version.
 
       ```
          VersionParams.Builder builder = new VersionParams.Builder()
                       .setRequestUrl("http://www.baidu.com")
                       .setService(DemoService.class);
                       
          AllenChecker.startVersionCheck(this, builder.build());
       ```
 	
 #### 2.Only using downloading module
 
 
    If you only want to use download module,you should just set `VersiongParams`. `service`  is not necessary.And you should set `onlyDownload=true` and set a `downloadUrl` inside the `VersionParams`.
 
  ```
   //downloadUrl is necessary for downlading module
    builder.setOnlyDownload(true)
                 .setDownloadUrl("http://down1.uc.cn/down2/zxl107821.uc/miaokun1/UCBrowser_V11.5.8.945_android_pf145_bi800_(Build170627172528).apk")
                 .setTitle("New Update")
                 .setUpdateMsg(getString(R.string.updatecontent));
                 
    AllenChecker.startVersionCheck(this, builder.build());
  ```
 	
   `VersionParams` Attribute：
  
  | Attribute Nmae        | Necessary           | default value | explain |
  | ------------- |:-------------|:-------------|:-------------:|
  | requestUrl   | Y/N  |-|the url of  version api |
  | service   | Y/N |-|should appoint your service except for only using download module |
  |downloadAPKPath|N|/storage/emulated/0/AllenVersionPath/|APK download path|
  | httpHeaders   | N |NULL|HTTP HEADER|
  | pauseRequestTime   | N |1000*30|the time between this request and next request|
  | requestMethod   | N |GET|Http request method|
  | requestParams   | N |NULL|http request params|
  | customDownloadActivityClass   | N |VersionDialogActivity.class|Customize dialog's activity,No Need for default|
  | isForceRedownload   | N |false|whether foceing downloading app even if there is cache|
  | isSilentDownload   | N |false|the switch of silent downloading|
  | onlyDownload  |N|false|whether only using donwloading module|
  |title|N|null|the title of update dialog when using downloading module|
  |updateMsg|N|null|the content of update dialog when using downloading module|
  |downloadUrl|Y for downlading module|-|the apk download address when using downloading module |
  |paramBundle|N|null|extra params，can use when custimization UI|
  |isShowDownloadingDialog|N|true|Whether showing downloading dialog|
  |isShowNotification|N|true|Whether showing notification when downloading|
    
  
 3.Start or close log
 
  `AllenChecker.init(true)`
 	  
 ### **Customize UI** 
    if you want to customize dialog ui,you should create an activity that extends `VersionDialogActivity`,and set activty theme：
 
  ` android:theme="@style/versionCheckLibvtransparentTheme"`
  
    And you should deliver activty name into VersionParams
    
    `setCustomDownloadActivityClass(CustomVersionDialogActivity.class)`
    
    - call `getVersionTitle()` ,`getVersionUpdateMsg()`,`getVersionParamBundle()`methods,this params come from service
    
    - customize `versionDialog`：
      override `showVersionDialog()` method ,write your code in it，and call `super.dealAPK();` when positive button is clicked.
 
      
      example code:
   ```
      versionDialog = new BaseDialog(this, R.style.BaseDialog, R.layout.custom_dialog_two_layout);
          TextView tvTitle = (TextView) versionDialog.findViewById(R.id.tv_title);
          TextView tvMsg = (TextView) versionDialog.findViewById(R.id.tv_msg);
          Button btnUpdate = (Button) versionDialog.findViewById(R.id.btn_update);
          versionDialog.show();
          //set dismiss listener for foceing update function ,it will call dialogDismiss method when dissmiss
          versionDialog.setOnDismissListener(this);
          //you can use extra params that is deliverd from service or versionParams：title。msg，downloadurl，parambundle
          tvTitle.setText(getVersionTitle());
          tvMsg.setText(getVersionUpdateMsg());
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
 
 	
  - customize  `downloadingDialog`，override `showLoadingDialog(int currentProgress)`,write your logic code in it
    
  - customize  `failDialog` ,override `showFailDialog`，write your logic code in it
    
  - force update function。Closing application in method of`dialogDismiss` and `onDownloadSuccess`，see demo for detail
    
  - except for above methods,you can also add some listener
    
    
                  setApkDownloadListener(this);
                  setCommitClickListener(this);
                  setDialogDimissListener(this);
 		 
 ### force update
 
 main idea is to listen
 
    ```
                  setApkDownloadListener(this);
 		         setDialogDimissListener(this);
    ```
  
 		 
 see demo for detail[ForceUpdate](https://github.com/AlexLiuSheng/CheckVersionLib/tree/master/ForceUpdateDemo)
  
 ### Donwloading notification icon and text replace
 If you want to relpaced the downloading notification icon,you should copy your icon to override icon named `ic_launcher`，
 replaced the notification name just need  override the attribute of `app_name` int strings.xml,there are some other attributes ，as following table:
 
 | Attribute Name        | Attribute Value           | 
 | ------------- |:-------------:|
 | versionchecklib_confirm    | Commit |
 | versionchecklib_cancel   | Cancel      |   
 |versionchecklib_retry | Retry    |  
 |versionchecklib_download_fail_retry| dowload falied and retry？   |  
 |versionchecklib_download_finish | download successful,click to install  |  
 |versionchecklib_downloading | download successful,clcik to install downloading...  |  
 |versionchecklib_check_new_version |New Update  |  
 |versionchecklib_download_fail | download failed，click to retry|  
 see demo for further understanding
 `star and issue is welcomed`
 
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
   

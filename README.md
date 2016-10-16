# CheckVersionLib
  现在热更新技术挺火的，大公司都出了自己的热更新框架，但个人感觉热更新技术还不是很完善，一般的IT公司还是倾向于传统的下载安装包进行版本升级，这是一个android上的自动版本检测并更新库。库集成了检测版本、下载版本以及自动安装升级
##特点
1.任何地方都可以检测

2.任何地方都可以弹出升级对话框

3.自定义性强，手动回调解析，适用于各种版本检测接口

4.自动处理下载和升级
##效果
 ![](https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/gif/version.gif)
##使用步骤
###android studio导入
`compile 'com.allenliu:checkversionlib:1.0.3'`（新增直接POSTSTRING方式请求接口）
###如何使用
1.自定义service，service必须继承库的 `AVersionService `，实现其中的 `onResponses(AVersionService service, String response)`抽象方法，该方法
主要是请求版本接口的回调，由于不同的使用者版本检测接口返回数据类型不一致，所以你需要自定解析数据，然后判断版本号之后调用 `service.showVersionDialog(downloadUrl,updateMsg )`
方法。示例代码:

             if (serverVersion > clientVersion) {
             //传入下载地址，以及版本更新消息
                  service.showVersionDialog(downloadUrl,updateMsg );
              } else {
              //由于是回调方法，当不进行版本升级时，需要手动关闭service。需要进行版本升级时，由库管理生命周期
                  stopSelf();
              }
              
2.在任意地方开启自定义service，并传入`VersionParam`

              VersionParams versionField = new VersionParams()
                         //是否强制升级,默认false
                        .setIsForceUpdate(false)
                        //接口请求方式,默认get
                        .setRequestMethod(AVersionService.POST)
                        //请求参数,选填
                        .setRequestParams(param)
                        //当版本接口请求失败时，service会根据设定的间隔时间继续请求版本接口，
                          直到手动关闭service或者接口请求成功，不填默认10s
                        .setPauseRequestTime(requestTime)
                         //接口地址，必填
                        .setRequestUrl(url)
                        //自定service包名,必须填写用于开启service
                        .setVersionServiceName("com.allenliu.versionchecklib.DemoService");
                Intent intent = new Intent(VersionDemoActivity.this, DemoService.class);
                intent.putExtra("versionField", versionField);
                startService(intent);
                
3.下载通知栏图标和文字替换，需要自定义图标只需在mimap文件下建立`ic_launcher`图标，替换标题只需在项目xml定义`app_name`属性
  

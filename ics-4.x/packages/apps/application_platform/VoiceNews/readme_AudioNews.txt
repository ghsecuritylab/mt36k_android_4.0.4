1.应用名称(launcher中显示的名字): 有声新闻
2.应用的包名：android.tcl.news
3.主activity名：TabHost.java
4.相关文件说明
  a. libAisound.so  //语音库文件(arm平台)
  b. Resource.irf   //语音资源文件(与语音库配套存在)
  c. tclTts.jar     //语音实现接口文件(已集成到apk中)
  d. sqlitecommon.jar  //开机启动服务(已集成到apk中)

5.依赖的模块说明：
  a.语音库文件(libAisound.so),实现文字转化语音的过程
  b.语音资源文件(Resource.irf),为文字转化为语音提供资源对照
  c.文字转化为语音的接口tclTts.jar，为java应用层提供c/c++层的实现接口 
  d.开机启动服务(sqlitecommom.jar),获取设备认证数据

6.集成方法：
  6.1 将libAisound.so 拷贝到system/lib下
  6.2 将Resource.irf 拷贝到system/etc下
  6.3 安装AudioNews.apk

7.功能简介：
  连接网络，从portal端获取不同分类的最新新闻列表，用户可点击查看新闻内容，  也可开启语音，电视自动播报新闻内容。

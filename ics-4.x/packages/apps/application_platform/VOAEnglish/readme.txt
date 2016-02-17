1.应用名称：英语听力

2.应用的包名：com.tcl.all.voa

3.主activity名：sisVoaEntry

4.相关文件说明(apk,so,jar等)：
				sqlitecommon.jar	//开机启动服务，获取认证信息(源文件已经集成到apk中)

5.依赖的模块说明:
			  开机启动服务(sqlitecommon.jar)，获取设备认证数据。

6.集成的方法:
      将VOAEnglish.apk拷贝到/system/app下。
			注：如果安装apk文件，执行如下命令：
			adb install   /XXXX/XXX/VOAEnglish.apk
			说明 ：/XXXX/XXX代表你存放以上2个文件的目录。
	
7.功能简介:
  本功能是通过欢网提供VOA英语资源，提高用户英语听力水平。
  
  刘艳 liuyan03@tcl.com
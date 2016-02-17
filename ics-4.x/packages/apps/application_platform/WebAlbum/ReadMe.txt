1.应用名称：网络相册
2.应用的包名：com.tcl.androidtv.webalbums2D
3.主activity名：WebAlbumsMain
4.相关文件说明(apk,so,jar等)：
Gesture_netphoto.apk   
手势识别库文件：
haarstage.data   
libgesture.so

以上两个文件放到system/lib下

gesturelib.jar
sqlitecommon.jar

5.依赖的模块说明:
       5.1 gesturelib.jar.
       5.2 本地音乐apk.
       5.3 libgesture.so库.
       5.4  haarstage.data
       5.5 sqlitecommon.jar
       5.6 ImageViewer.apk

6.集成的方法:
      6.1 将  libgesture.so和 haarstage.data拷贝到 /system/lib下。
      6.2 装apk文件，即
	执行 adb install   /XXXX/XXX/Gesture_netphoto .apk
说明 ：/XXXX/XXX代表你本地存放文件的目录。

7.功能简介:
  手势操作网络相册


一：文件清单：
  Gesture_netphoto.apk   haarstage.data   libgesture.so



1.应用名称(launcher中显示的名字)：人脸识别

2.应用的包名：com.tcl.demo.face

3.主activity名：FaceDetectActivity

4.相关文件说明(apk,so,jar等)：
FaceDetect.apk	//应用程序
haarstage.face.data   
libface_detect.so
libfaceDetection.so


6.集成的方法:
      FaceDetect.apk拷贝到/system/app下。//不能通过adb install FaceDetect.apk去安装
      haarstage.face.data 拷贝到/system/lib下。
      libface_detect.so拷贝到/system/lib下。
      libfaceDetection.so拷贝到/system/lib下。

7.功能简介:
  
 人脸识别：当用户将人脸识别模式打开后，超过一定时间摄像头没有检测到人脸信息就会进入省电模式，
当用户回来通过按键或者摄像头自动检测到人脸信息时，退出省电模式。如需关闭，需要进入人脸识别应用，
点击关闭按钮即可。

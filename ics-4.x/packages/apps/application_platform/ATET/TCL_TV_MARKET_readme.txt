*****************************************************************
1.APK名称：         TCL_TV_MARKET.apk
2.APK英文名称：     
3.APK中文名称：     ATET游戏大厅
4.APK包名:              com.sxhl.tcltvmarket
5.APK主入口:            com.atet.tvmarket.control.start.DeviceIdActivity
6.厂商定义版本号：V3.6.1.13
7.安装集成说明：

   7.1 集成方法：
   1）预置此apk到/system/app/下；
   2) 请不要修改签名
 
   7.2 U盘安装方法，如果之前系统已经预置了apk，需到system/app删除原有的apk，以及/data/data目录下的包名，方法如下：
   1).如果是只读文件系统，先remount分区，命令：mount -o remount ,rw /system
   2).删除apk，rm /system/app/TCL_TV_MARKET.apk
   3).删除data数据包， rm -rf /data/data/com.sxhl.tcltvmarket
   4).安装pm install /mnt/usb/XXXX/TCL_TV_MARKET.apk
   5).重启
   备注：如果不是预置到/system/app/下的apk，
   可以直接覆盖安装pm install -r /mnt/usb/XXXX/TCL_TV_MARKET.apk


8 功能简介或说明:
  
*****************************************************************
9.版本更改记录： 
 
 9.1 修改记录
   版本提交日期：2016-1-4

   9.1.1 修改问题时间：20160104
   9.1.2 第三方应用版本名称：
         ATET内部版本号：V3.6.1.13
   9.1.2 修改的问题：
	
         1、应用安装路径是sdcard

     
        
 

package com.tcl.xian.StartandroidService;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SqlCommon  {
    /** Called when the activity is first created. */
//	@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sqlcommon); 
//	}
  /*   private void insertUserRecord(String userName) {
        ContentValues values = new ContentValues();
        values.put(MyUsers.User.USER_NAME, userName);
        values.put(MyUsers.User.USER_SEX, "gril");
        getContentResolver().insert(MyUsers.User.CONTENT_URI, values);
       // getContentResolver().delete(MyUsers.User.CONTENT_URI,"3",null);
      //  ContentValues values1 = new ContentValues();
       // values1.put(MyUsers.User.USER_NAME, "xiaoqiang");
       // getContentResolver().update(MyUsers.User.CONTENT_URI,values1,"6",null);
    } */
 /*   private void insertHuanidRecord(String userName) {
        ContentValues values = new ContentValues();
      
        values.put(MyUsers.huanid.HUAN_ID, userName);
      
        getContentResolver().insert(MyUsers.huanid.CONTENT_URI, values);
       // getContentResolver().delete(MyUsers.User.CONTENT_URI,"3",null);
      //  ContentValues values1 = new ContentValues();
       // values1.put(MyUsers.User.USER_NAME, "xiaoqiang");
       // getContentResolver().update(MyUsers.User.CONTENT_URI,values1,"6",null);
    }*/
	 //保存终端是否激活的标志位在数据库中
    public void updateDeviceActiveFlag(String record, ContentResolver resolver){
      	 try {
      		ContentValues values1 = new ContentValues();
       	   
            values1.put(MyUsers.devicetoken.ACTIVE_FLAG, record);
            
            resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
      	 }catch (Exception e) {
      		Log.e("SqlCommon", "updateDevice", e);
      	 }
    	
      }
    //保存设备ID号在数据库中
    public void updateDeviceidRecord(String record, ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
     	 
        values1.put(MyUsers.devicetoken.DEVICE_ID, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
      }
    //保存设备编号在数据库中
    public void updateDumRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
    	 
        values1.put(MyUsers.devicetoken.DUM, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    	
     }
    //保存设备类型在数据库中
    public void updateDeviceModel(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
    	 
        values1.put(MyUsers.devicetoken.DEVICE_MODEL, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
     }
    //保存设备激活码在数据库中
    public void updateActivekeyRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
   	 
        values1.put(MyUsers.devicetoken.ACTIVE_KEY, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
    }
    
    //保存DIDtoken在数据库中
    public void updateDidTokenRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
      	 
        values1.put(MyUsers.devicetoken.DIDTOKEN, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
   }
    //保存用户登录token在数据库中
    public void updateTokenRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
      	 
        values1.put(MyUsers.devicetoken.TOKEN, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
   }
    //保存设备欢网登录的号在数据库中
    public void updateHuanidRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
     	 
        values1.put(MyUsers.devicetoken.HUAN_ID, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
      }
    //保存设备登录许可证类型在数据库中
    public void updateLicenseTypeRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
     	 
        values1.put(MyUsers.devicetoken.LICENSE_TYPE, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
      }
    //保存设备登录许可证数据在数据库中
    public void updateLicenseDataRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
     	 
        values1.put(MyUsers.devicetoken.LICENSE_DATA, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
      }
    
    //保存设备登录状态在数据库中
    public void updateDeviceLoginFlagRecord(String record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
     	 
        values1.put(MyUsers.devicetoken.DEVICELOGIN_FLAG, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateDevice", e);
  	 }
    
      }

    //保存设备登录状态在数据库中
    public void updateBootTimes(int record,ContentResolver resolver){
   	 try {
   		ContentValues values1 = new ContentValues();
     	 
        values1.put(MyUsers.devicetoken.BOOT_TIMES, record);
        
        resolver.update(MyUsers.devicetoken.CONTENT_URI,values1,null,null);
  	 }catch (Exception e) {
  		Log.e("SqlCommon", "updateBootTimes", e);
  	 }
    
      }
    //获取激活的标志位
    public String getDeviceActiveFlag(ContentResolver resolver) {
        String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA };
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
            Cursor cur =resolver.query(myUri, columns,null, null, null );
            
    		if (cur != null) {
    			if (cur.moveToFirst()) {

    				do {
    					deviceid = cur.getString(cur
    							.getColumnIndex(MyUsers.devicetoken.ACTIVE_FLAG));

    				} while (cur.moveToNext());
    			}
    			cur.close();
    		}
        } catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
        
  
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    
    //获取设备认证成功的标志位
    public String getDeviceLoginFlag(ContentResolver resolver) {
        String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA };
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
        	Cursor cur =resolver.query(myUri, columns,null, null, null );
               
       		if (cur != null) {
       			if (cur.moveToFirst()) {

       				do {
       					deviceid = cur.getString(cur
       							.getColumnIndex(MyUsers.devicetoken.DEVICELOGIN_FLAG));

       				} while (cur.moveToNext());
       			}
       			cur.close();
       		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
     
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
   //获取设备ID号 
    public String getDeviceid(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID ,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA};
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
            Cursor cur =resolver.query(myUri, columns,null, null, null );
            
    		if (cur != null) {
    			if (cur.moveToFirst()) {

    				do {
    					deviceid = cur.getString(cur
    							.getColumnIndex(MyUsers.devicetoken.DEVICE_ID));

    				} while (cur.moveToNext());
    			}
    			cur.close();
    		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    //获取设备编号
    public String getDum(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA };
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
            Cursor cur = resolver.query(myUri, columns,null, null, null );
            
    		if (cur != null) {
    			if (cur.moveToFirst()) {

    				do {
    					deviceid = cur.getString(cur
    							.getColumnIndex(MyUsers.devicetoken.DUM));

    				} while (cur.moveToNext());
    			}
    			cur.close();
    		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    //获取设备类型
    public String getDeviceModel(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA };
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
        	Cursor cur = resolver.query(myUri, columns,null, null, null );
               
       		if (cur != null) {
       			if (cur.moveToFirst()) {

       				do {
       					deviceid = cur.getString(cur
       							.getColumnIndex(MyUsers.devicetoken.DEVICE_MODEL));

       				} while (cur.moveToNext());
       			}
       			cur.close();
       		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
     
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    //获取设备激活码
    public String getActiveKey(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA };
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
            Cursor cur = resolver.query(myUri, columns,null, null, null );
            
    		if (cur != null) {
    			if (cur.moveToFirst()) {

    				do {
    					deviceid = cur.getString(cur
    							.getColumnIndex(MyUsers.devicetoken.ACTIVE_KEY));

    				} while (cur.moveToNext());
    			}
    			cur.close();
    		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    
  //获取DIDTOKEN
    public String getDidtoken(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA };
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
        	Cursor cur = resolver.query(myUri, columns,null, null, null );
               
       		if (cur != null) {
       			if (cur.moveToFirst()) {

       				do {
       					deviceid = cur.getString(cur
       							.getColumnIndex(MyUsers.devicetoken.DIDTOKEN));

       				} while (cur.moveToNext());
       			}
       			cur.close();
       		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    
    //获取登录token
    public String getToken(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA };
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
        	Cursor cur = resolver.query(myUri, columns,null, null, null );
              
      		if (cur != null) {
      			if (cur.moveToFirst()) {

      				do {
      					deviceid = cur.getString(cur
      							.getColumnIndex(MyUsers.devicetoken.TOKEN));

      				} while (cur.moveToNext());
      			}
      			cur.close();
      		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
      
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    
    //获取欢网帐号
    public String getHuanid(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID ,MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA};
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
        	Cursor cur =resolver.query(myUri, columns,null, null, null );
              
      		if (cur != null) {
      			if (cur.moveToFirst()) {

      				do {
      					deviceid = cur.getString(cur
      							.getColumnIndex(MyUsers.devicetoken.HUAN_ID));

      				} while (cur.moveToNext());
      			}
      			cur.close();
      		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
      
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    //获取终端许可证类型
    public String getLicenseType(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID, MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA};
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
        	Cursor cur =resolver.query(myUri, columns,null, null, null );
              
      		if (cur != null) {
      			if (cur.moveToFirst()) {

      				do {
      					deviceid = cur.getString(cur
      							.getColumnIndex(MyUsers.devicetoken.LICENSE_TYPE));

      				} while (cur.moveToNext());
      			}
      			cur.close();
      		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
      
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    
  //获取终端许可证数据
    public String getLicenseData(ContentResolver resolver) {
    	   String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID, MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA};
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        String deviceid = "";
        try {
        	Cursor cur =resolver.query(myUri, columns,null, null, null );
              
      		if (cur != null) {
      			if (cur.moveToFirst()) {

      				do {
      					deviceid = cur.getString(cur
      							.getColumnIndex(MyUsers.devicetoken.LICENSE_DATA));

      				} while (cur.moveToNext());
      			}
      			cur.close();
      		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
      
		if(deviceid==null){
			deviceid="";
		}
        return deviceid;
    } 
    
    //获取开机次数
    public int getBootTimes(ContentResolver resolver) {
    	   //String columns[] = new String[] { MyUsers.devicetoken.ACTIVE_FLAG,MyUsers.devicetoken.DEVICELOGIN_FLAG,MyUsers.devicetoken.DEVICE_ID, MyUsers.devicetoken.DUM,MyUsers.devicetoken.DEVICE_MODEL,MyUsers.devicetoken.ACTIVE_KEY,MyUsers.devicetoken.DIDTOKEN,MyUsers.devicetoken.TOKEN,MyUsers.devicetoken.HUAN_ID, MyUsers.devicetoken.LICENSE_TYPE,MyUsers.devicetoken.LICENSE_DATA,MyUsers.devicetoken.BOOT_TIMES};
        Uri myUri = MyUsers.devicetoken.CONTENT_URI;
        int deviceid = 0;
        try {
        	Cursor cur =resolver.query(myUri, null,null, null, null);
        	if(cur!=null){
        		int mColumn = cur.getColumnCount();
            	if(mColumn<12){
            	  Log.i("SqlCommon", "getBootTimes"+mColumn);
            	  //deleteTable(resolver);
            	  return deviceid;
            	}
        	}
        
      		if (cur != null) {
      			if (cur.moveToFirst()) {

      				do {
      					deviceid = cur.getInt(cur
      							.getColumnIndex(MyUsers.devicetoken.BOOT_TIMES));

      				} while (cur.moveToNext());
      			}
      			cur.close();
      		}
        }catch (Exception e) {
        	Log.e("SqlCommon", "Exception", e);
		}
        return deviceid;
    } 
    //获取开机次数
    public void deleteTable(ContentResolver resolver) {
    	Uri myUri = MyUsers.devicetoken.CONTENT_URI;
    	resolver.delete(myUri, null, null); 
    } 
}
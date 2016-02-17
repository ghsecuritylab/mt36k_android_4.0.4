package com.mediatekk.mmpcm.videoimpl;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
//import android.media.MediaMetadataRetriever;

import com.mediatekk.mmpcm.video.ICapture;


public class Capture implements ICapture {
    private static Bitmap bmp1;
    //private static Bitmap bmp2;
    
    //MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    
	public void captureVideo(String filepath)
	{
	    //retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);
	    //retriever.setDataSource(filepath);
	    //bmp1 = retriever.captureFrame();
	    //retriever.release();
	}
	
	public void saveToPath(String path, String picturename)
	{
       /* FileOutputStream fos = null;   
        try {   
             fos = new FileOutputStream(picturename);   
            if (null != fos&& bmp1 != null)   
             {   
                 bmp1.compress(Bitmap.CompressFormat.JPEG, 100, fos);   
                 fos.flush();   
                 fos.close();   
             }   
         } catch (FileNotFoundException e) {   
             e.printStackTrace();   
         } catch (IOException e) {   
             e.printStackTrace();   
         } 		*/
	}
	
	public void abortCapture()
	{
		
	}
	
	public void setPowerOnLogo(String picturename)
	{
		
	}
	
	public void setBackground(String picturename)
	{
		
	}
}
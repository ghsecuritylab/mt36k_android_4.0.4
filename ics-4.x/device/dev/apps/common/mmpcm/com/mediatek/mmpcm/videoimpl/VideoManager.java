package com.mediatek.mmpcm.videoimpl;


import android.content.Context;
import android.view.SurfaceView;

import com.mediatek.mmpcm.video.ICapture;
import com.mediatek.mmpcm.video.IComset;
import com.mediatek.mmpcm.video.IFileInfo;
import com.mediatek.mmpcm.video.IPlayback;

public class VideoManager {
    private static VideoManager mVideoManager = null;
    
    private static IPlayback playback = null;
    private static IFileInfo fileinfo = null;
    private static IComset comset = null;
    private static ICapture capture = null;  
    
    private VideoManager(){
        
    }
    
    /**
     * 
     * @param context
     * @param surfaceview
     * @param playerMode VideoConst.PLAYER_MODE_MMP or VideoConst.PLAYER_MODE_NET
     * @return
     */
    public static VideoManager getInstance(Context context,SurfaceView surfaceview,int playerMode){
        if(mVideoManager == null){
            synchronized(VideoManager.class){
                if(mVideoManager == null){
                    mVideoManager = new VideoManager(context,surfaceview,playerMode);
                }               
            }
        }
        return mVideoManager;
    }
    
    public static VideoManager getInstance(Context context,int playerMode){
        if(mVideoManager == null){
            synchronized(VideoManager.class){
                if(mVideoManager == null){
                    mVideoManager = new VideoManager(context,playerMode);
                }               
            }
        }
        return mVideoManager;
    }  
    
    public static VideoManager getInstance(){
        return mVideoManager;
    }
    
    private VideoManager(Context context,SurfaceView surfaceview,int playerMode){
        if(playback == null){
            playback = new Playback(surfaceview,playerMode);
        }
        
        if(fileinfo == null){
            fileinfo = FileInfo.getInstance();
        }
        
        if(comset == null){
            comset = new Comset();
        }
        
        if(capture == null){
            capture = new Capture();
        }                               
    }
    
    private VideoManager(Context context,int playerMode){
        if(playback == null){
            playback = new MPlayback(playerMode);
        }
        
        if(fileinfo == null){
            fileinfo = FileInfo.getInstance();
        }
        
        if(comset == null){
            comset = new Comset();
        }
        
        if(capture == null){
            capture = new Capture();
        }                               
    }
    
    public void onRelease()
    {           
        playback.onRelease();
        playback = null;
        fileinfo = null;
        comset = null;
        capture = null;
        mVideoManager = null;
    }    
    
    public IPlayback getPlayback()
    {           
        return playback;
    }
    
    public IFileInfo getFileInfo()
    {
        return fileinfo;
    }       
    
    public IComset getComset()
    {
        return comset;
    }
    
    public ICapture getCapture()
    {
        return capture;
    }
}

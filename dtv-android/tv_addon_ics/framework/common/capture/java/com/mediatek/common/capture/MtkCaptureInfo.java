package com.mediatek.common.capture;

import android.graphics.Rect;

public class MtkCaptureInfo
{
	public final int     format;  
    public final int     e_res_type;    
    public final int     quality;
    public final int     max_size;
    public final Rect    rect;
    public final int     ui4_moveable_width;
    public final int     ui4_moveable_height;
	public final byte    ui1_video_path; /*0 is for Main plane, 1 is for Sub plane*/

	public final int     skBitmap;
	public final int     buffer_width;
	public final int     buffer_height;
	public final int     buffer_pitch;
	public final int     color_mode;
    
//	public final int     ui4_res_width;
//	public final int     ui4_res_height;
//	public final int     h_src_surf;/* For capture photo use*/
//    public final int     h_working_surf; /* For capture photo use*/
    
	public MtkCaptureInfo(int e_format, 
						  int iquality, 
						  int imax_size,
						  int res_type, 
						  Rect t_rect,
						  int moveable_width, 
						  int moveable_height)
	{
		this.format = e_format;
		this.quality = iquality;
		this.max_size = imax_size;
		this.e_res_type = res_type;
		this.rect = t_rect;
		this.ui4_moveable_width =moveable_width;
		this.ui4_moveable_height =moveable_height;
		this.ui1_video_path = 0;
		this.skBitmap = 0;
		this.buffer_width = 0;
		this.buffer_height = 0;
		this.buffer_pitch = 0;
		this.color_mode = 0;
	}


	public MtkCaptureInfo(int e_format, 
						  int iquality, 
						  int imax_size,
						  int res_type, 
						  Rect t_rect,
						  int moveable_width, 
						  int moveable_height,
						  byte video_path)
	{
		this.format = e_format;
		this.quality = iquality;
		this.max_size = imax_size;
		this.e_res_type = res_type;
		this.rect = t_rect;
		this.ui4_moveable_width =moveable_width;
		this.ui4_moveable_height =moveable_height;
		this.ui1_video_path = video_path;
		this.skBitmap = 0;
		this.buffer_width = 0;
		this.buffer_height = 0;
		this.buffer_pitch = 0;
		this.color_mode = 0;
	}

	public MtkCaptureInfo(int e_format, 
						  int iquality, 
						  int imax_size,
						  int res_type, 
						  Rect t_rect,
						  int moveable_width, 
						  int moveable_height,
						  byte video_path,
						  int  mSkBitmap,
						  int     buffer_width,
						  int     buffer_height,
						  int     buffer_pitch,
						  int     color_mode)
	{
		this.format = e_format;
		this.quality = iquality;
		this.max_size = imax_size;
		this.e_res_type = res_type;
		this.rect = t_rect;
		this.ui4_moveable_width =moveable_width;
		this.ui4_moveable_height =moveable_height;
		this.ui1_video_path = video_path;
		this.skBitmap = mSkBitmap;
		this.buffer_width = buffer_width;
		this.buffer_height = buffer_height;
		this.buffer_pitch = buffer_pitch;
		this.color_mode   = color_mode;
	}
	
} 

package com.tcl.mediafile;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.tclwidget.TCLToast;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.android.internal.R;

public class MultiMediaUtils {
	private static final String TAG = "mediabrowser - MediaFileUtils ";

	/**
	 * @param file
	 * @param mainType
	 * @return 
	 */
	public static boolean checkMainType(MediaFile file, String mainType) {
		
		if (file.isDirectory()) {
			return false;
		}
		String mimetype = file.mimeType();
		if (mimetype == null)
			return false;
		String type = MimeType.getType(mimetype);
		if (type == null) {
			return false;
		}
		return type.equals(mainType);
	}
	
	/**
	 * 用于判断一个MediaFile是否为视频文件
	 * */
	public static boolean isVideoFile(MediaFile file) {
		
		if (file.isDirectory()) {
			return false;
		}
		String mimetype = file.mimeType();
		if (mimetype == null)
			return false;
		if (mimetype.equals("application/octet-stream")
				|| checkMainType(file, "video"))
			return true;
		
		return false;
	}
	
	/**
	 * 用于判断一个MediaFile是否为音频文件
	 * */
	public static boolean isAudioFile(MediaFile file) {
		return checkMainType(file, "audio");
	}
	
	/**
	 * 用于判断一个MediaFile是否为图片文件
	 * */
	public static boolean isImageFile(MediaFile file) {
		return checkMainType(file, "image");
	}	
	
	
	/**
	 * 一个通过MimeType去过来video类型的MediaFile的MediaFileFilter。
	 * */
	public static class VideoFileFilter implements MediaFileFilter {
		public boolean accept(MediaFile file) {
			return isVideoFile(file);
		}
	}
	
	/**
	 * 一个通过MimeType去过来audio类型的MediaFile的MediaFileFilter。
	 * */
	public static class AudioFileFilter implements MediaFileFilter {
		public boolean accept(MediaFile file) {
			return isAudioFile(file);
		}
	}
	
	public static class AudioAndDirFileFilter implements MediaFileFilter {
		public boolean accept(MediaFile file) {
			return isAudioFile(file) || file.isDirectory();
		}
	}
	
	public static class VideoAndDirFileFilter implements MediaFileFilter {
		public boolean accept(MediaFile file) {
			return isVideoFile(file) || file.isDirectory();
		}
	}
	
	public static class ImageAndDirFileFilter implements MediaFileFilter {
		public boolean accept(MediaFile file) {
			return isImageFile(file) || file.isDirectory();
		}
	}
	
	/**
	 * 一个通过MimeType去过来audio类型的MediaFile的MediaFileFilter。
	 * */
	public static class ImageFileFilter implements MediaFileFilter {
		public boolean accept(MediaFile file) {
			return isImageFile(file);
		}
	}
	
	/**
	 * 根据filter从当前文件夹创建一个播放列表。构建的播放列表，当前选择的媒体文件位于播放列表的第一个。
	 * */
	static PlayList createPlayList(MediaFile file, MediaFileFilter filter) {
		MediaFile parent = file.getParent();
		//Uri.Builder ub = new Uri.Builder();
		//ArrayList<Uri> uriList = new ArrayList<Uri>();
		PlayList playList = new PlayList();
		int index = -1;
		if (parent == null) {
			return null;
		}
		else {
			List<MediaFile> files = parent.listMediaFiles();
			int size = files.size();
			for (int i = 0; i < size; i++) {
				MediaFile f = files.get(i);
				if (filter.accept(f)) {
					if (f.equals(file)) {
						playList.index = playList.playList.size();
					}
					playList.playList.add(f.uri());
				}
			}
		}
		
		//依次将队首对象移至队尾，实现将第index个对象作为播放列表的队首。
		//这样好，还是在播放列表中增加index好？？？
//		Log.d(TAG, "index: " + index);
//		for (int i = 0; i < index; i++) {
//			Log.d(TAG, "move file: " + uriList.get(0).getPath());
//			uriList.add(uriList.remove(0));
//		}
		return playList;
	}
	

	
	
	
	static PlayList createVideoPlayList(MediaFile file) {
		return createPlayList(file, new VideoFileFilter());
	}
	
	static PlayList createAudioPlayList(MediaFile file) {
		return createPlayList(file, new AudioFileFilter());
	}
	
	static PlayList createImagePlayList(MediaFile file) {
		return createPlayList(file, new ImageFileFilter());
	}

	public static Intent createOpenFileIntent(MediaFile file) {
		Log.v(TAG , "paly: " + file.getName());
		Intent it = new Intent(Intent.ACTION_VIEW);
		if (MultiMediaUtils.isVideoFile(file)) {
			it.setType("application/vnd.tcl.playlist-video");
			Bundle bundle = new Bundle();
			PlayList list = MultiMediaUtils.createVideoPlayList(file);
			if(list!=null && list.size()>0){
				bundle.putParcelableArrayList("playlist", list.playList);
				bundle.putInt("index", list.index);
				it.putExtras(bundle);
			} else {
				Log.i("MediaFileUtils-->>>>>>>>>>>>>>", "VideoFile  playlist is null");
				return null;
			}	
		}
		else if (MultiMediaUtils.isAudioFile(file)){
			it.setType("application/vnd.tcl.playlist-audio");
			Bundle bundle = new Bundle();
			PlayList list = MultiMediaUtils.createAudioPlayList(file);
			if(list!=null && list.size()>0){
				bundle.putParcelableArrayList("playlist", list.playList);
				bundle.putInt("index", list.index);
				it.putExtras(bundle);
			} else {
				Log.i("MediaFileUtils-->>>>>>>>>>>>>>", "AudioFile  playlist is null");
				return null;
			}
		}
		else if (MultiMediaUtils.isImageFile(file)){
			it.setType("application/vnd.tcl.playlist-image");
			Bundle bundle = new Bundle();
			PlayList list = MultiMediaUtils.createImagePlayList(file);
			if(list!=null && list.size()>0){
				bundle.putParcelableArrayList("playlist", list.playList);
				bundle.putInt("index", list.index);
				it.putExtras(bundle);
			} else {
				Log.i("MediaFileUtils-->>>>>>>>>>>>>>", "ImageFile  playlist is null");
				return null;
			}
		}
		else {
			//Uri.Builder ub = new Uri.Builder();
			Uri uri = file.uri();
			it.setDataAndType(uri, file.mimeType());
		}
		
		Log.d(TAG, it.toString());
		return it;
	}
	
	/**
	 * 打开一个多媒体文件，打开多媒体文件时，根据当前目录创建播放列表。
	 * 
	 * @param ctx
	 * @param file
	 */
	public static void openMultiMediaFile(Context ctx, MediaFile file) {
		Log.v(TAG , "paly: " + file.getName());
		try {
//			Intent it = new Intent(Intent.ACTION_VIEW);
//			if (MultiMediaUtils.isVideoFile(file)) {
//				it.setType("application/vnd.tcl.playlist-video");
//				Bundle bundle = new Bundle();
//				PlayList list = MultiMediaUtils.createVideoPlayList(file);
//				if(list!=null && list.size()>0){
//					bundle.putParcelableArrayList("playlist", list.playList);
//					bundle.putInt("index", list.index);
//					it.putExtras(bundle);
//				} else {
//					Log.i("MediaFileUtils-->>>>>>>>>>>>>>", "VideoFile  playlist is null");
//					return;
//				}	
//			}
//			else if (MultiMediaUtils.isAudioFile(file)){
//				it.setType("application/vnd.tcl.playlist-audio");
//				Bundle bundle = new Bundle();
//				PlayList list = MultiMediaUtils.createAudioPlayList(file);
//				if(list!=null && list.size()>0){
//					bundle.putParcelableArrayList("playlist", list.playList);
//					bundle.putInt("index", list.index);
//					it.putExtras(bundle);
//				} else {
//					Log.i("MediaFileUtils-->>>>>>>>>>>>>>", "AudioFile  playlist is null");
//					return;
//				}
//			}
//			else if (MultiMediaUtils.isImageFile(file)){
//				it.setType("application/vnd.tcl.playlist-image");
//				Bundle bundle = new Bundle();
//				PlayList list = MultiMediaUtils.createImagePlayList(file);
//				if(list!=null && list.size()>0){
//					bundle.putParcelableArrayList("playlist", list.playList);
//					bundle.putInt("index", list.index);
//					it.putExtras(bundle);
//				} else {
//					Log.i("MediaFileUtils-->>>>>>>>>>>>>>", "ImageFile  playlist is null");
//					return;
//				}
//			}
//			else {
//				//Uri.Builder ub = new Uri.Builder();
//				Uri uri = file.uri();
//				it.setDataAndType(uri, file.mimeType());
//			}
//			
//			Log.d(TAG, it.toString());
			Intent it = createOpenFileIntent(file);
			ctx.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			TCLToast toast = TCLToast.makePrompt(ctx, String.format(ctx.getString(R.string.mediafile_cannot_open_file), file.getName()), Toast.LENGTH_LONG, TCLToast.INFO_IMAGE);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}
}

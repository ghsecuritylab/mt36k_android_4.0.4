package com.mediatekk.mmpcm.fileimpl;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.media.ExifInterface;
import android.os.AsyncTask;


import com.mediatekk.mmpcm.MmpTool;
import com.mediatekk.mmpcm.mmcimpl.Const;
import com.mediatekk.mmpcm.photoimpl.ProcessPhoto;
/**
 * This class defines Photo file
 * 
  */
public class PhotoFile extends MtkFile {

	private static final String TAG = "PhotoFile";
	private static final long serialVersionUID = 111212L;
	
	
	
	private int SampleSize = 0;
	
	
	/**
	 * Get the photo file with MtkFile
	 * @param f
	 *        MtkFile
	 */
	public PhotoFile(MtkFile f) {
		super(f.getPath());
	}

	/**
	 * Get the photo file with URI
	 * @param uri
	 */
	public PhotoFile(URI uri) {
		super(uri);
	}

	/**
	 * Get the photo file with directory path and name of the file
	 * @param dirPath
	 * @param name
	 */
	public PhotoFile(String dirPath, String name) {
		super(dirPath, name);
	}

	/**
	 * Get the photo file with path of the file
	 * @param path
	 */
	public PhotoFile(String path) {
		super(path);
	}

	/**
	 * Get the photo file with directory path and name of the file
	 * @param dir
	 * @param name
	 */
	public PhotoFile(File dir, String name) {
		super(dir, name);
	}

	/**
	 * Get resolution of the photo
	 */
	public String getResolution() {

		/*if (!isValidPhoto()) {
			return null;
		}*/
		if (width == 0 || height == 0) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			o.inSampleSize = 1;
			BitmapFactory.decodeFile(this.getAbsolutePath(), o);
			width = o.outWidth;
			height = o.outHeight;
		}

		return new StringBuffer().append(width).append("*").append(height)
				.toString();
	}

	private int width;
	private int height;
	
	/**
	 * If photo larger than 20M, unvalid.
	 * @return if true, valide. else unvalid
	 */
/*	private boolean isValidPhoto() {
		long size = this.getFileSize();
		if (size > 20 * 1024 * 1024 || size <= 0) {
			return false;
		}
		return true;
	}
*/
	public Bitmap getThumbnail(int width, int height) {

		Bitmap bmp = null;
		Bitmap smallBmp =null;

		try {

			MmpTool.LOG_INFO("starting--------");
			/*if (!isValidPhoto()) {
				return null;
			}*/
			//add by shuming for fix CR:DTV00399658
			if(width<height){
				width= height;
			}
			bmp = decodeBitmap(this, width);
			MmpTool.LOG_INFO("end-------- bmp = " + bmp);
			
			if(bmp!=null){
				
				if(bmp.getWidth()<bmp.getHeight()){
					smallBmp = Bitmap.createScaledBitmap(bmp, height, height, true);
				}else{
					smallBmp = Bitmap.createScaledBitmap(bmp, width, height, true);
				}
				
				
			}
			//change by shuming for fix CR DTV00402642
//					bmp = ThumbnailUtils.extractThumbnail(bmp, width, height
//							,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);	
						
//			bmp = mextractThumbnail(bmp, width, height
//					,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);	
			if (hisense) {
				smallBmp = scaleBitmap(bmp, width);
			}
			if(bmp!=null&& !bmp.isRecycled()){
				bmp.recycle();
				bmp=null;
				System.gc();
			}
		} catch (OutOfMemoryError e) {
			MmpTool.LOG_ERROR("OutOfMemoryError!!!");
		}
		return smallBmp;
	}

	private static BitmapFactory.Options opt = new BitmapFactory.Options();

	private Bitmap decodeBitmap(File file, int requiredSize) {
		if (null == file) {
			return null;
		}

		opt.mCancel = false;

		Bitmap bmp = null;

		opt.inJustDecodeBounds = true;
		opt.inSampleSize = 1;
		BitmapFactory.decodeFile(this.getAbsolutePath(), opt);
		width = opt.outWidth;
		height = opt.outHeight;

		// add by keke 3.30 6144 * 4096(24mx1024x1024) maximum support
		// for Static gif files
		if (this.getAbsolutePath().toLowerCase().endsWith("gif")) {
			if ((width * height) > (6144 * 4096)) {
				return null;
			}
		}

		// add by shuming for fix CR:DTV00399658
		SampleSize = (int) (height / (float) requiredSize);
		if (width <= 10 || height <= 10) {
			opt.inSampleSize = 128;
		} else if (SampleSize > 0) {
			opt.inSampleSize = SampleSize;
		} else if (height > requiredSize || width > requiredSize) {
			int scale = (int) Math.pow(
					2.0,
					(int) Math.round(Math.log(requiredSize
							/ (double) Math.max(height, width))
							/ Math.log(0.5)));
			opt.inSampleSize = scale;
		} else {
			opt.inSampleSize = 1;
		}
		opt.inJustDecodeBounds = false;
		MmpTool.LOG_INFO("thumbnial_decoding inSampleSize: " + opt.inSampleSize);
		bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
		if (null == bmp) {
			opt.requestCancelDecode();
			return null;
		}

		if (getOrientation() != -1) {

			if (getOrientation() == 1) {
				return bmp;
			} else {
				int orientation = getOrientation();
//				if (null != bmp) {
					bmp = ProcessPhoto.Rotate(bmp,Const.ORIENTATION_ARRAY[orientation]);//rotate(bmp, orientation);
//				}
				
			}
		}

		return bmp;
	}

	// add for Hisense
	private static boolean hisense = false;

	public void hisenseOn() {
		hisense = true;
	}

	/**
	 * Create a 2>>N pixels target bitmap from source bitmap.
	 * 
	 * @param b
	 *            source bitmap
	 * @param size
	 *            eg: 128,256,512
	 * @return target bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap source, int size) {
		if (source == null) {
			return source;
		}
		Matrix matrix = new Matrix();
		int width = source.getWidth();
		int height = source.getHeight();
		float sx = (float) size / height;

		if (width > height) {
			sx = (float) size / width;
		}
		matrix.setScale(sx, sx);
		try {
			Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source
					.getHeight(), matrix, false);

			if (source != bitmap) {				
				source.recycle();
				MmpTool.LOG_INFO("source recycle ? "+source.isRecycled());
			}

			Bitmap bitmap1 = Bitmap.createBitmap((int) size, (int) size,
					Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap1);
			float left = 0;
			float right = 0;
			int width1 = bitmap.getWidth();
			int height1 = bitmap.getHeight();
			if (width1 < size) {
				left = (size - width1) / 2;
			}
			if (height1 < size) {
				right = (size - height1) / 2;
			}
			canvas.drawBitmap(bitmap, left, right, null);
			
			bitmap.recycle();
			MmpTool.LOG_INFO("bitmap recycle ? "+bitmap.isRecycled());
			
			return bitmap1;
		} catch (OutOfMemoryError e) {
			MmpTool.LOG_ERROR("OutOfMemoryError!!!");
		}
		return null;
	}

	public void stopDecode() {
		// MmpTool.LOG_INFO("STOP Entered 000000000000000");
		// if (null != opt) {
		// MmpTool.LOG_INFO("STOP Starting:1111111111111");
		//
		// //add by shuming for fix bug DTV00380813
		//
		// StopTask stopDecodePhoto = new StopTask();
		// stopDecodePhoto.execute();
		//
		// MmpTool.LOG_INFO("STOP Ending:2222222222222");
		//
		// opt.mCancel = false;
		// MmpTool.LOG_INFO("opt.mCancel" + opt.mCancel);
		//
		// }
	}

	/**
	 * Stop to decode thumbnail
	 */
	public void stopThumbnail(){
		//TODO
		stopDecode();
	}

	//private int degree = 0;

	private int getOrientation() {
		//degree = 0;
		ExifInterface exif = null;
		try {

			exif = new ExifInterface(this.getAbsolutePath());

		} catch (IOException ex) {
			MmpTool.LOG_ERROR("cannot read exif"+ex);
			return -1;
		}
		int orientation = -1;
		if (exif != null) {
				orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			/*if (orientation != -1) {
				// We only recognize a subset of orientation tag values.
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}

		}*/
		}

		return orientation;
	}

	private Bitmap rotate(Bitmap bm, int dg) {

		int widthOrig = bm.getWidth();
		int heightOrig = bm.getHeight();

		int newWidth = widthOrig;
		int newHeight = heightOrig;
		float scaleWidth = ((float) newWidth) / widthOrig;
		float scaleHeight = ((float) newHeight) / heightOrig;

		Matrix rotatematrix = new Matrix();
		rotatematrix.postScale(scaleWidth, scaleHeight);

		rotatematrix.setRotate(dg);

		try {
			Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, widthOrig,
					heightOrig, rotatematrix, true);
			if (bm != resizedBitmap) {
				bm.recycle();
				bm = resizedBitmap;

			}
		} catch (OutOfMemoryError e) {
			MmpTool.LOG_ERROR("OutOfMemoryError");
		}

		return bm;
	}

}

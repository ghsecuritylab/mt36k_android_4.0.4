package com.mediatek.mmpcm.photoimpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.mediatek.mmpcm.MmpTool;
import com.mediatek.mmpcm.fileimpl.FileConst;
import com.mediatek.mmpcm.fileimpl.MtkFile;
import com.mediatek.mmpcm.mmcimpl.Const;
import com.mediatek.mmpcm.mmcimpl.PlayList;
import com.mediatek.mmpcm.photo.IImageshow;

import com.mediatek.netcm.dlna.DLNAManager;
import com.mediatek.netcm.samba.SambaManager;

/**
 * 
 * This class represents control photo and play it .
 *
 */
public class Imageshowimpl implements IImageshow {

	private static final String TAG = "Imageshowimpl";

	private int duration = 3;

	private boolean playstatus_play;

	@SuppressWarnings("unused")
	private boolean playstatus_pause;

	private int zoomoutmult;

	private float zoominmult;

	private int rotatedegree = 0;

	private ProcessPhoto processimage;

	public Bitmap OrigiPhoto;

	public String picPath;
	public String netPath;

	private OnPhotoCompletedListener mCompleteListener;
	private OnPhotoDecodeListener mDecodeListener;

	// photo's height and width
	private int bmpH;
	private int bmpW;

	// Display's height and width
	private int dw;
	private int dh;
	private PlayList imagepL;
	/**
	 * Simple constructor
	 */
	public Imageshowimpl() {
		processimage = new ProcessPhoto();
		this.imagepL = PlayList.getPlayList();
	}
	/**
	 * Simple constructor
	 * @param Display gives you access to some information about a particular display
     * connected to the device.
	 */
	public Imageshowimpl(Display curDisplay) {
		processimage = new ProcessPhoto();
		this.curDisplay = curDisplay;
		this.imagepL = PlayList.getPlayList();
		setWindow();
	}
	/**
	 * set photo to frame mode.
	 * @return
	 */
	public String setPhotoFrameImage() {
		return null;
	}

	/**
	 * return current photo
	 */
	public PhotoUtil curShow() {
		resetOrientation();
		PhotoUtil curBmp = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {
			if (LocOrNet == ConstPhoto.LOCAL) {

				curBmp = transfBitmap(curPath);

			} else if (LocOrNet == ConstPhoto.SAMBA
					|| LocOrNet == ConstPhoto.DLNA
					|| LocOrNet == ConstPhoto.URL) {

				setNetPath(curPath);
				curBmp = netBitmap();
			}
		}
		return curBmp;
	}
	/**
	 * pause photo.
	 * @param
	 */
	public void setPause(boolean blean) {
		playstatus_pause = blean;
	}
	/**
	 * play photo.
	 * @param
	 */
	public void setPlay(boolean blean) {
		playstatus_play = blean;
		playstatus_pause = !blean;

	}

	private int zoomOutMult = 0;

	private float zoomInMult = 0;

	/**
	 *1. if you set "inOrOut" to "ConstPhoto.ZOOM_OUT",it will enlarge 1.25mult
	 * each; if you set "inOrOut" to "ConstPhoto.IN",it will shrink 0.8mult. 2.
	 * if you set "inOrOut" except "ConstPhoto.ZOOM_OUT" and "ConstPhoto.IN",it
	 * will zoom according to "size".
	 */
	public void Zoom(ImageView image, int inOrOut, Bitmap bitmap, float size) {

		if (inOrOut == ConstPhoto.ZOOM_OUT
				&& zoomOutMult <= ConstPhoto.ZOOM_MAXMULT) {
			processimage.zoom(image, inOrOut, bitmap, size);
			zoomOutMult++;
			zoomInMult--;
		} else if (inOrOut == ConstPhoto.ZOOM_IN
				&& zoomInMult <= ConstPhoto.ZOOM_MAXMULT) {
			processimage.zoom(image, inOrOut, bitmap, size);
			zoomInMult++;
			zoomOutMult--;
		} else if (inOrOut != ConstPhoto.ZOOM_IN
				&& inOrOut != ConstPhoto.ZOOM_OUT)
			processimage.zoom(image, inOrOut, bitmap, size);
		MmpTool.LOG_INFO(zoomOutMult + ":" + zoomInMult);

	}
	/**
	 * Clean zoom base value.
	 */
	public void cleanZoomMult() {
		zoomInMult = 0;
		zoomOutMult = 0;
	}

	/**
	 * Right rotate specified bitmap
	 * @param Specified bitmap
	 */

	public Bitmap rightRotate(Bitmap bitmap) {
		cleanZoomMult();
			setOrientation();
		OrigiPhoto = processimage.Rotate(bitmap, Const.ORIENTATION_ROTATE);

		return OrigiPhoto;
	}
	/**
	 * Left rotate specified bitmap
	 * @param Specified bitmap
	 */
	public Bitmap leftRotate(Bitmap bitmap) {
		cleanZoomMult();

		int arr[] = {0,0,-90};
		OrigiPhoto = processimage.Rotate(bitmap,arr );
		rotatedegree += -90;

		if (rotatedegree + 360 == 0) {
			rotatedegree = 0;
		}

		return OrigiPhoto;
	}

	private Bitmap rightRotate(Bitmap bitmap, int dg) {
		cleanZoomMult();

		OrigiPhoto = processimage.Rotate(bitmap, Const.ORIENTATION_ARRAY[dg]);

		return OrigiPhoto;
	}
	/**
	 * Get album.
	 * @return 
	 */
	public String getAlbum() {
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {

			MmpTool.LOG_INFO(curPath);
			try {
				curPath = curPath.substring(0, curPath.lastIndexOf("/"));
				curPath = curPath.substring(curPath.lastIndexOf("/") + 1);
				MmpTool.LOG_INFO("-------------------curPath: " + curPath);
			} catch (IndexOutOfBoundsException e) {
				MmpTool.LOG_INFO(e.toString());
				return "";
			}
		}
		if (curPath != null) {
			if (curPath.compareTo("usbdisk") == 0) {
				curPath = "sda1";
			}
		}
		return curPath;
	}


	private int degree;
	/**
	 * Get rorate degree .
	 * @return
	 */
	public int getOrientation() {
		ExifInterface exif = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {

			try {
				exif = new ExifInterface(curPath);
				MmpTool.LOG_INFO("curPath = " + curPath);
			} catch (IOException ex) {
				MmpTool.LOG_ERROR( "cannot read exif"+ ex);
				return -1;
			}

			if (exif != null) {
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, -1);
				if (orientation != -1 && orientation <= 8) {
					
					degree = orientation ;
					MmpTool.LOG_ERROR( "getOrientation degree"+ degree);
				}
			}
		} else {
			return -2;
		}

		MmpTool.LOG_INFO("degree = " + degree);
		return degree;
	}
	
	
	private int getNextOrientation(int cur){
	
		int index = Const.ORIENTATION_NEXT_ARRAY[cur];
		
		
		MmpTool.LOG_INFO("value cur= " + cur+"index="+index);
		if(index <=4){
			index = (index+1)%4>0?(index+1)%4:4 ;
		}else{
			index = 4 + ((index+1)%4>0?(index+1)%4:4 );
		}
		MmpTool.LOG_INFO("value cur= " + cur+"index="+index);
		int i ;
		for(i=1;i<=8;i++){
			if(Const.ORIENTATION_NEXT_ARRAY[i] == index ){
				return i;
			}
		}
		
		return -1;
		
	}

	/**
	 * Set rorate
	 */
	public void setOrientation() {
		int value = 0;
		ExifInterface exif = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {

			try {
				exif = new ExifInterface(curPath);
				value = getOrientation();

				MmpTool.LOG_INFO("getOrientation() = " + getOrientation());

				if ( value > 0 && value <=8) {
//					switch ((d + 90) % 360) {
//					case 0:
//						value = ExifInterface.ORIENTATION_NORMAL;
//						break;
//					case 90:
//						value = ExifInterface.ORIENTATION_ROTATE_90;
//						break;
//					case 180:
//						value = ExifInterface.ORIENTATION_ROTATE_180;
//						break;
//					case 270:
//						value = ExifInterface.ORIENTATION_ROTATE_270;
//						break;
//					default:
//						break;
//					}
					int next=getNextOrientation(value);
					MmpTool.LOG_INFO("value = " + value+"next="+next);
					exif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer
							.toString(next));
				}
			} catch (IOException ex) {
				MmpTool.LOG_ERROR(TAG + "cannot read exif"+ex);
			}
		}

		try {
			String f = getFlash();
			if (f == null || f.compareTo("65535") == 0
					|| f.compareTo("-1") == 0) {
				exif.setAttribute("Flash", Integer.toString(-1));
			}

			String wb = getWhiteBalance();
			if (wb == null || wb.compareTo("65535") == 0
					|| wb.compareTo("-1") == 0) {
				exif.setAttribute("WhiteBalance", Integer.toString(-1));
			}

			exif.saveAttributes();
			MmpTool.LOG_DBG(TAG + "exif.saveAttributes()######");
		} catch (IOException e) {
			MmpTool.LOG_ERROR(TAG+"cannot save exif"+ e);
		}

		MmpTool.LOG_DBG(TAG + "set Orientation = "
				+ exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));

	}
	/**
	 * Set rorate degree to 0. 
	 */
	public void resetOrientation() {
		rotatedegree = 0;
	}

	/**
	 * Returns the value of the specified "Make" or 
	 * null if there is no such tag in the JPEG file.
	 * @return
	 */
	public String getMake() {
		String make = null;
		ExifInterface exif = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {

			try {
				exif = new ExifInterface(curPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			make = exif.getAttribute("Make");
		} else {
			return "curPath is null!";
		}

		return make;
	}

	/**
	 * Returns the value of the specified "Model" or 
	 * null if there is no such tag in the JPEG file.
	 * @return
	 */
	public String getModel() {
		String model = null;
		ExifInterface exif = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {
			try {
				exif = new ExifInterface(curPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model = exif.getAttribute("Model");
		} else {
			return "curPath is null!";
		}

		return model;
	}

	/**
	 * Returns the value of the specified "Flash" or 
	 * null if there is no such tag in the JPEG file.
	 * @return
	 */
	public String getFlash() {
		String flash = null;
		ExifInterface exif = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {
			try {
				exif = new ExifInterface(curPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flash = exif.getAttribute("Flash");
		} else {
			return "curPath is null!";
		}

		if (flash == null || flash.compareTo("65535") == 0 
			|| flash.compareTo("-1") == 0) {
			return null;
		}

		return flash;
	}

	/**
	 * Returns the value of the specified "WhiteBlance" or 
	 * null if there is no such tag in the JPEG file.
	 * @return
	 */
	public String getWhiteBalance() {
		String whiteblance = null;
		ExifInterface exif = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {
			try {
				exif = new ExifInterface(curPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			whiteblance = exif.getAttribute("WhiteBlance");
		} else {
			return "curPath is null!";
		}

		return whiteblance;
	}

	/**
	 * Returns the value of the specified "FocalLength" or 
	 * null if there is no such tag in the JPEG file.
	 * @return
	 */
	public String getFocalLength() {
		String focallength = null;
		ExifInterface exif = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {

			try {
				exif = new ExifInterface(curPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			focallength = exif.getAttribute("FocalLength");
		} else {
			return "curPath is null!";
		}
		return focallength;
	}
	/**
	 * Get bitmap size.
	 * @return
	 */
	public String getSize() {
		double psize = 0;
		if (LocOrNet == ConstPhoto.LOCAL) {
			File flFile;
			String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);
			if (curPath != null) {
				flFile = new File(curPath);
				psize = (double) (flFile.length() / 1024.00);
			}

		} else if (LocOrNet == ConstPhoto.SAMBA || LocOrNet == ConstPhoto.DLNA
				|| LocOrNet == ConstPhoto.URL) {
			psize = (double) ((bmpH * bmpW) / 1024.00);
		}
		DecimalFormat myFormatter = new DecimalFormat("###.00");
		return myFormatter.format(psize) + "KB";
	}
	/**
	 * Get bitmap resolution.
	 * @return
	 */
	public String getResolution() {
		File flFile = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);
		if (curPath != null) {
			flFile = new File(curPath);
		}

		if (bmpW == 0 || bmpH == 0) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(flFile.getAbsolutePath(), o);
			bmpW = o.outWidth;
			bmpH = o.outHeight;
		}

		return new StringBuffer().append(bmpW).append(" X ").append(bmpH)
				.toString();
	}
	/**
	 * Get bitmap height.
	 * @return
	 */
	public int getPheight() {
		return bmpH;
	}
	/**
	 * Get bitmap width.
	 * @return
	 */
	public int getPwidth() {
		return bmpW;
	}
	/**
	 * Get photo file name.
	 * @return
	 */
	public String getName() {
		File flFile;
		String curName = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);
		if (null != curPath) {
			flFile = new File(curPath);
			curName = flFile.getName();
		}

		return curName;
	}
	/**
	 * Get photo file modify date.
	 * @return
	 */
	public String getModifyDate() {
		String date = null;
		String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);

		if (null != curPath) {
			MmpTool.LOG_INFO("curPath = " + curPath);
			File flFile;
			flFile = new File(curPath);
			SimpleDateFormat sdf = new SimpleDateFormat();

			date = sdf.format(new java.util.Date(flFile.lastModified()));
		}
		return date;

	}
	/**
	 * @deprecated
	 * Get play stauts.
	 * @return
	 */
	public int getStatusPlay() {
		if (playstatus_play) {
			return 1;
		} else {
			return 0;
		}
	}
	/**
	 * Get playback duration.
	 * @return
	 */
	public int getDuration() {
		return duration;
	}
	/**
	 * Set playback duration value.
	 * @param
	 */
	public void setDuration(int interval) {
		duration = interval;
	}
	/**
	 * Get zoom out size.
	 * @return
	 */
	public int getZoomOutSize() {
		return zoomoutmult;
	}
	/**
	 * Get zoom in size.
	 * @return
	 */
	public float getZoomInSize() {
		return zoominmult;
	}

	private Display curDisplay;

	private void setWindow() {
		dw = curDisplay.getRawWidth();
		dh = curDisplay.getRawHeight();
	}

	/**
	 * if photo larger than 20M, unvalid file.
	 * @return if true, valide. else unvalid
	 */
	private boolean isValidPhoto(){
		if (LocOrNet == ConstPhoto.LOCAL) {
			MtkFile flFile;
			double psize = 0;
			String curPath = imagepL.getCurrentPath(Const.FILTER_IMAGE);
			if (curPath != null) {
				flFile = new MtkFile(curPath);
				psize = (double) (flFile.length() / 1024.00);
			}
			
			if (psize==0){
				return false;
			}
			
//			if (indx >= 0 && indx < FileConst.photoSuffix.length){
//				if (FileConst.photoSuffix[indx].equals(".gif")){
//					BitmapFactory.Options opt = new BitmapFactory.Options();
//					opt.inJustDecodeBounds = true;
//					opt.inSampleSize = 1;
//					BitmapFactory.decodeFile(curPath, opt);
					
//					if (opt.outWidth > 1920 && opt.outHeight > 1080){
//						return false;
//					}
//				}
//			}
		}

		return true;
	}
	
	private static BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
	private ArrayList<Bitmap> historyBitmap;

	private void recyleBitmap(Bitmap bitmap){
		
		if (null == historyBitmap){
			historyBitmap = new ArrayList<Bitmap>();
		}
		if (historyBitmap.size() < 2){
			historyBitmap.add(bitmap);
		} else {
			Bitmap temp = historyBitmap.get(0);
			historyBitmap.remove(0);
			if (temp != null){
				temp.recycle();
				System.gc();
				MmpTool.LOG_INFO("temp.isRecycled = ------------"+temp.isRecycled());
			}
			historyBitmap.add(bitmap);		
		}
	}
	/**
	 *   
     * Decode a file path into a bitmap. If the specified file name is null,
     * or cannot be decoded into a bitmap, the function returns null.
     *
     * @param transfPath complete path name for the file to be decoded.
     * @return The decoded bitmap, or null if the image data could not be
     *         decoded, or, if opts is non-null, if opts requested only the
     *         size be returned (in opts.outWidth and opts.outHeight)
     *
	 * @return
	 */
	public PhotoUtil transfBitmap(String transfPath) {
        PhotoUtil trBmp = new PhotoUtil();
		MmpTool.LOG_INFO(transfPath);
		
		
		if (!isValidPhoto()){
			if (null != mDecodeListener)
				mDecodeListener
						.onDecodeFailure();
			return null;
		}
		
		bmpFactoryOptions.mCancel = false;

		if (bmpFactoryOptions.inSampleSize != 1) {
			bmpFactoryOptions.inSampleSize = 1;
		}

		bmpFactoryOptions.inJustDecodeBounds = true;

		MmpTool.LOG_INFO("bmpFactoryOptions:mCancel = "
				+ bmpFactoryOptions.mCancel + ";" + "inSampleSize = "
				+ bmpFactoryOptions.inSampleSize);
		try {
			BitmapFactory.decodeFile(transfPath, bmpFactoryOptions);
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		/* get photo's height and width */
		bmpW = bmpFactoryOptions.outWidth;
		bmpH = bmpFactoryOptions.outHeight;

		MmpTool.LOG_INFO("get screen resolution:" + "bmpW = " + bmpW + ";"
				+ "bmpH = " + bmpH);

		bmpFactoryOptions.inSampleSize = 1;
		
		//add for play some special photo
		if(bmpW<10 || bmpH<10){
			
			bmpFactoryOptions.inSampleSize=15;
			
		}else{

		bmpFactoryOptions.inSampleSize = computeSampleSize(
				bmpFactoryOptions, ConstPhoto.UNCONSTRAINED, 
				ConstPhoto.MAX_NUMOF_PIXS);
		}
        
		MmpTool.LOG_INFO("bmpFactoryOptions:mCancel = "
				+ bmpFactoryOptions.mCancel + ";" + "inSampleSize = "
				+ bmpFactoryOptions.inSampleSize);

		bmpFactoryOptions.inJustDecodeBounds = false;

		int i = 0; //bmpFactoryOptions.frameNumber;
		MmpTool.LOG_INFO("framenumber = "+i);
		MmpTool.LOG_INFO("decoding......... start");
		if(i>1){
			int[] delayTimes = {100,100};//bmpFactoryOptions.frameDelayArray;
			trBmp.setmDelayTime(delayTimes);
			//trBmp.setmBitmaps(BitmapFactory.decodeAnimationGifFile(transfPath, bmpFactoryOptions, i));
			return trBmp;
		}
		try {
			trBmp.setmBitmaps(new Bitmap[]{BitmapFactory.decodeFile(transfPath, bmpFactoryOptions)});
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		MmpTool.LOG_INFO("decoding......... end trBmp = "+ trBmp);
		if (null != trBmp) {
			MmpTool.LOG_INFO("trBmp is not null~~~~~~~~");
		}

		if (LocOrNet != ConstPhoto.LOCAL) {
			if (image.exists()) {
				image.delete();
			}
		}

		if (null != trBmp && null != trBmp.getmBitmaps()
				&& null != trBmp.getmBitmaps()[0]) {
			int orientation = getOrientation();
			if (orientation != -1 && orientation != -2) {
				if (orientation == 1) {
					return trBmp;
				} else {
					trBmp.getmBitmaps()[0] = rightRotate(trBmp.getmBitmaps()[0], orientation);
				}
			}
			if (null != mDecodeListener) {
				mDecodeListener.onDecodeSuccess();
			}
		} else {
			if (null != mDecodeListener)
				mDecodeListener
						.onDecodeFailure();
		}

		return trBmp;
	}
	
    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    
    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == ConstPhoto.UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == ConstPhoto.UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == ConstPhoto.UNCONSTRAINED) &&
                (minSideLength == ConstPhoto.UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == ConstPhoto.UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    
	/**
	 * Get pre photo when manual mode.
	 * @return
	 */
	public PhotoUtil getPre() {
		resetOrientation();
		PhotoUtil bitmapPre = null;

		if (imagepL.getRepeatMode(Const.FILTER_IMAGE) == Const.REPEAT_NONE) {
			if (imagepL.getShuffleMode(Const.FILTER_IMAGE) == Const.SHUFFLE_OFF) {
				if (imagepL.getCurrentIndex(Const.FILTER_IMAGE) == 0) {
					if (null != mCompleteListener)
						mCompleteListener.onComplete();
					return null;
				}
			} else {
				if (imagepL.getCurShuffleIndex(Const.FILTER_IMAGE) >= (imagepL
						.getFileNum(Const.FILTER_IMAGE) - 1)) {
					if (null != mCompleteListener)
						mCompleteListener.onComplete();
					return null;
				}
			}
		}

		String path_Pre;
		path_Pre = imagepL.getNext(Const.FILTER_IMAGE, Const.MANUALPRE);

		if (null == path_Pre) {
			MmpTool.LOG_INFO( "previous path is null....");
		}

		if (LocOrNet == ConstPhoto.LOCAL) {
			if (null != path_Pre) {
				bitmapPre = transfBitmap(path_Pre);
			}

		} else if (LocOrNet == ConstPhoto.SAMBA || LocOrNet == ConstPhoto.DLNA
				|| LocOrNet == ConstPhoto.URL) {
			if (null != path_Pre) {
				setNetPath(path_Pre);
			}
			bitmapPre = netBitmap();
		}
		return bitmapPre;
	}

	/** 
	 * Get next photo when manual mode.
	 * */
	public PhotoUtil getNext() {
		resetOrientation();
		PhotoUtil bitmapNext = null;
		String path_Next;

		path_Next = imagepL.getNext(Const.FILTER_IMAGE, Const.MANUALNEXT);

		if (null == path_Next) {
			MmpTool.LOG_INFO("next path is null....");
		}

		if (LocOrNet == ConstPhoto.LOCAL) {
			if (null == path_Next && isEnd()) { // 
				if (null != mCompleteListener)
					mCompleteListener.onComplete();
				return null;
			} else {
				bitmapNext = transfBitmap(path_Next);
			}

		} else if (LocOrNet == ConstPhoto.SAMBA || LocOrNet == ConstPhoto.DLNA
				|| LocOrNet == ConstPhoto.URL) {
			if (null == path_Next && isEnd()) { 
				if (null != mCompleteListener)
					mCompleteListener.onComplete();
				return null;
			} else {
				setNetPath(path_Next);
				bitmapNext = netBitmap();
			}
		}
		return bitmapNext;
	}

	public boolean isEnd() {
		if (imagepL.getRepeatMode(Const.FILTER_IMAGE) == Const.REPEAT_NONE) {
			if (imagepL.getShuffleMode(Const.FILTER_IMAGE) == Const.SHUFFLE_ON) {
				if (imagepL.getCurShuffleIndex(Const.FILTER_IMAGE) >= (imagepL
						.getFileNum(Const.FILTER_IMAGE) - 1)) {
					return true;
				}
			} else {
				if (imagepL.getCurrentIndex(Const.FILTER_IMAGE) >= (imagepL
						.getFileNum(Const.FILTER_IMAGE) - 1)) {
					return true;
				}
			}
		}

		return false;
	}

	/** 
	 * Auto play next photo.
	 * @param
	 */
	public PhotoUtil autoPlayNext() {
		resetOrientation();
		PhotoUtil autoBmp = null;
		String path = null;

		path = imagepL.getNext(Const.FILTER_IMAGE, Const.AUTOPLAY);

		if (LocOrNet == ConstPhoto.LOCAL) {

			if (path != null || !isEnd()) {

				autoBmp = transfBitmap(path);

			} else {
				if (null != mCompleteListener)
					mCompleteListener.onComplete();
				return null;
			}

		} else if (LocOrNet == ConstPhoto.SAMBA || LocOrNet == ConstPhoto.DLNA
				|| LocOrNet == ConstPhoto.URL) {

			if (path != null || !isEnd()) {

				setNetPath(path);
				autoBmp = netBitmap();

			} else {
				if (null != mCompleteListener)
					mCompleteListener.onComplete();
				return null;
			}

		}
		return autoBmp;
	}
	/**
	 * @deprecated
	 * 
	 * @return
	 */
	public Bitmap moveImage() {
		return null;
	}
	/**
	 * @deprecated
	 * 
	 * @return
	 */
	public ImageView moveImageView(ImageView iView, int left, int top,
			int right, int bottom) {

		iView.setPadding(iView.getPaddingLeft() + left, iView.getPaddingTop()
				+ top, iView.getPaddingRight() + right, iView
				.getPaddingBottom()
				+ bottom);

		return iView;
	}

	/* net Photo */
	private int LocOrNet;

	public void setLocOrNet(int i) {
		LocOrNet = i;
	}
	/**
	 * Set path when net play mode.
	 * @param path
	 */
	public void setNetPath(String path) {
		if (null != path) {
			netPath = path;
			MmpTool.LOG_INFO(netPath);
		}
	}

	/* For net ,get local path */
	private File image;
	private String path = ConstPhoto.TempFolderPath + "/image";
	private int bufSize = 64 * 1024;
	private byte[] buffer = null;
	private FileOutputStream output = null;
	/**
	 * Get local play path.
	 * @return
	 * @throws IOException
	 */
	public String getLocalPath() throws IOException {
		InputStream input = getInputStream();

		if (null == input) {
			return "";
		}

		image = new File(path);
		image.deleteOnExit();

		if (null == buffer) {
			buffer = new byte[bufSize];
		}

		output = new FileOutputStream(image);

		int ret = input.read(buffer);

		while (ret > 0) {
			output.write(buffer, 0, ret);
			ret = input.read(buffer);
		}
		output.close();
		input.close();
		return path;
	}

	protected InputStream getInputStream() {
		InputStream input = null;
		if (LocOrNet == ConstPhoto.SAMBA) {
			try {
				input = SambaManager.getInstance().getSambaDataSource(netPath)
						.newInputStream();
			} catch (SmbException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else if (LocOrNet == ConstPhoto.DLNA) {
			input = DLNAManager.getInstance().getDLNADataSource(netPath)
					.newContentInputStream();
		} else if (LocOrNet == ConstPhoto.URL) {

			try {
				URL url = new URL(netPath);
				HttpURLConnection httpURLconnection;
				httpURLconnection = (HttpURLConnection) url.openConnection();
				httpURLconnection.setRequestMethod("GET");
				httpURLconnection.setReadTimeout(10 * 1000);

				String responseCode = url.openConnection().getHeaderField(0);

				if (responseCode.indexOf("200") < 0)
					try {
						throw new Exception(
								"Image file is not exit or path is error,error code"
										+ responseCode);
					} catch (Exception e) {
						e.printStackTrace();
					}

				if (httpURLconnection.getResponseCode() == 200) {
					input = url.openStream();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return input;
	}

	private PhotoUtil netBmp = null;
	/**
	 * Get bitmap when internet mode.
	 * @return
	 */
	public PhotoUtil netBitmap() {

		try {
			String locPath = getLocalPath();
			MmpTool.LOG_INFO(locPath);

			netBmp = transfBitmap(locPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return netBmp;
	}

	/**
	 * Set listener to play complete.
	 */
	public void setCompleteListener(OnPhotoCompletedListener completeListener) {
		this.mCompleteListener = completeListener;
	}
	/**
	 * Set listener to decode error or success.
	 */
	public void setDecodeListener(
			OnPhotoDecodeListener decodeListener) {
		this.mDecodeListener = decodeListener;
	}

	public interface OnPhotoCompletedListener {
		public void onComplete();
	}

	public interface OnPhotoDecodeListener {
		public void onDecodeFailure(/*int message*/);
		public void onDecodeSuccess();
	}

	/**
	 * This can be called from another thread while this options object 
	 * is inside a decode... call. Calling this will notify 
	 * the decoder that it should cancel its operation.
	 *  This is not guaranteed to cancel the decode, 
	 *  but if it does, the decoder... operation will return null, 
	 *  or if inJustDecodeBounds is true, will set outWidth/outHeight to -1
	 */
	public void stopDecode() {

		if (null != bmpFactoryOptions) {
			MmpTool.LOG_INFO("stopdecode starting!!!!!!");
			bmpFactoryOptions.requestCancelDecode();
			MmpTool.LOG_INFO("stopdecode ending!!!!!!");
			bmpFactoryOptions.mCancel = false;
		}
	}

}

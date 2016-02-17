package com.tcl.mediafile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * MediaFile为虚基类。提够类似File的接口，并增加了诸如getMimeType()等结构，方便操作。
 * 一般情况下，可以通过Device的getRootFile()方法来获取一个MediaFile。
 * */
public abstract class MediaFile {
	private MediaFile mParent;
	private String mName;
	private String mPath;
	private Device mDevice = null;
	
	/**
	 * 根据MediaFile的path来构建MediaFile。
	 * */
	public MediaFile(String path) {
		mPath = path;
	}
	
	/**
	 * 构造一个MediaFile
	 * */
	public MediaFile(){}
	
	/**
	 * 设置该MediaFile的父结点。
	 * 
	 * @param p 负结点，当没有父结点时，p设置为Null
	 * */
	public void setParent(MediaFile p) {
		mParent = p;
		if (p != null) {
			mDevice = p.getDevice();
		}
	}
	
	/**
	 * 获取父对象
	 * 
	 * @return 父对象，或null；
	 * */
	public MediaFile getParent() {
		return mParent;
	}
	
	/**
	 * 设置改MediaFile归属的设备。同时也可以通过{@link #getDevice() getDevice()}方法来获得一个设备对象。
	 * 
	 * @param device 所归属的设备。
	 * */
	public void setDevice(Device device) {
		mDevice = device;
	}
	
	/**
	 * 获得设备
	 * 
	 * @return 所属的设备。
	 * */
	public Device getDevice() {
		return mDevice;
	}
	
	
	/**
	 * 创建一个新的MediaFile。
	 * 
	 * @return 创建文件是否成功。
	 * @throws IOException
	 * */
	public boolean createMediaFile() throws IOException{
		return false;
	}
	
	/**
	 * 获取MediaFile名
	 * 
	 * @return MediaFile名
	 * */
	public String getName() {
		return mName;
	}
	
	/**
	 * 设置MediaFile名
	 * 
	 * @param MediaFile名
	 * */
	public void setName(String name) {
		mName = name;
	}
	
	/**
	 * 获取文件路径
	 * 
	 * @return 文件路径
	 * */
	public String getPath(){
		return mPath;
	}
	
	//如果是本地文件，则uri为：file:///xxx
	public Uri uri() {
		return Uri.parse("file://" + getPath());
	}
	
	/**
	 * 获取文件长度
	 * 
	 * @return 返回文件长度
	 * */
	abstract public long length();
	
	
//	/**
//	 * 获取文件图标
//	 * 
//	 * @return 返回文件图标
//	 * */
//	public Bitmap getFileIcon(Context ctx) {
//		if (isDirectory())
//			return IconCreator.createFolderIcon(ctx);
//		else
//			return IconCreator.createIconWithMime(ctx, mimeType());
//	}
	
	/**
	 * 设置文件图标
	 * 
	 * @param bm 文件图标的Bitmap
	 * @see #getFileIcon(Context)
	 * */
	public void setFileIcon(Bitmap bm) {
		//mThumbBitmap = bm;
	}
	
	/**
	 * 返回MediaFile的MimeType。
	 * @return MimeType，形如“video/mp4”
	 * */
	public String mimeType() {
		String ext = MimeType.getFileExtensionFromUrl(getName());
		String mimeType = MimeType.getSingleton().getMimeTypeFromExtension(ext);
		return mimeType;
	}
	
	/**
	 * MediaFile比较器，由字母序来确定文件的排序先后。当进行MediaFile列表排序时，将会使用该方法。
	 * */
	public static class MediaFileNameComparator implements Comparator<MediaFile> {
		
		/**
		 * 按字母序比较object1和object2的大小，比较的依据是文件名
		 * @return
		 * 		如果返回负值，则表示这个实例小于另一个；如果返回为正值，表示大于另一个；如果为0，则两个相等。
		 * @throws NullPointerException 如果MediaFile为null，则抛出异常
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(MediaFile file1, MediaFile file2) {
			if (file1 == null || file2 == null) {
				throw new NullPointerException();
			}
			
			if (file1.isDirectory() && !file2.isDirectory())
				return -1;
			else if (!file1.isDirectory() && file2.isDirectory()) {
				return 1;
			}
			
			if (file1.getName() == null || file2.getName() == null)
				return 0;
			return compareName(file1.getName(),file2.getName());//file1.getName().compareTo(file2.getName());
			//compareName(file1.getName(),file2.getName());
		}
		
		//按字母和数字混个排序
		private int compareName(String str1,String str2){
			int flag = 0;
			String digitStr1 ="";
			String digitStr2 ="";
			int len = str1.length()<str2.length()?str1.length():str2.length();
			for(int i=0;i<len;i++){
				if(digitStr1.length()>5){
					if(digitStr1.compareTo(digitStr2)!=0){
						return digitStr1.compareTo(digitStr2);
					}
					digitStr1 = "";
					digitStr2 = "";
				}
				
				if(Character.isDigit(str1.charAt(i)) && Character.isDigit(str2.charAt(i))){
					digitStr1+=str1.charAt(i);
					digitStr2+=str2.charAt(i);
				} else {
					if(digitStr1.trim().length()>0){
						if(Character.isDigit(str1.charAt(i))){
							int j = i;
							while(j<str1.length() && Character.isDigit(str1.charAt(j))){
								digitStr1 += str1.charAt(j);
								j++;
							}

						} else if(Character.isDigit(str2.charAt(i))){
							int j = i;
							while(j<str2.length() && Character.isDigit(str2.charAt(j))){
								digitStr2 += str2.charAt(j);
								j++;
							}
						 
						}      
						if(digitStr1.length()>5 || digitStr2.length()>5){
							if(str1.charAt(i)>str2.charAt(i)){
								flag = 1;
								break;
							}else if(str1.charAt(i)<str2.charAt(i)){
								flag = -1;
								break;
							}
						}else{
							if(Integer.parseInt(digitStr1)>Integer.parseInt(digitStr2)){
								flag = 1;
								break;
							} else if(Integer.parseInt(digitStr1)<Integer.parseInt(digitStr2)){
								flag = -1;
								break;
							}
						}
						
						digitStr1 = "";
						digitStr2 = "";
					} else if(str1.charAt(i)>str2.charAt(i)){
						flag = 1;
						break;
					} else if(str1.charAt(i)<str2.charAt(i)){
						flag = -1;
						break;
					}
				}
				
			}
			
			return flag;
		}
		
	}
	
	/** 
	 * 当两个MediaFile的路径相同时，我们就认为这两个MediaFile是相同的。
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @return true表示相同，反之，不相同
	 */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o.getClass().equals(getClass())))
			return false;
		return getPath().equals(((MediaFile)o).getPath());
	}

	/**
	 * 返回MediaFile的hashCode,我们利用了文件路径String的hashCode作为替代
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/**
	 * 获取MediaFile的InputStream，可以用来读取文件。
	 * @return InputStream
	 * */
	public InputStream getInputStream() {
		return null;
	}
	
	/**
	 * 获取MediaFile的OutputStream，可以用来写文件。
	 * @return OutputStream
	 * */
	public OutputStream getOutputStream() {
		return null;
	}
	
	/**
	 * 删除文件
	 * @return 删除操作是否成功
	 * */
	abstract public boolean delete();
	
	abstract public boolean exist();
	
	/**
	 * 创建文件夹
	 * @return 创建是否成功
	 * */
	abstract public boolean mkdir();
	
	/**
	 * 返回该MediaFile是否可写
	 * @return 是否可写
	 * */
	abstract public boolean canWrite();
	
	/**
	 * 获取改文件夹的文件列表，如果改文件不是文件夹，则返回null
	 * @return 文件列表或null
	 * */
	public List<MediaFile> listMediaFiles() {
		return listMediaFiles(null);
	}
	
	
	/**
	 * @param 过滤器 
	 * @return 文件列表或null
	 * @see MediaFileFilter
	 */
	public abstract List<MediaFile> listMediaFiles(MediaFileFilter filter);

	/**
	 * 指示是否为文件夹
	 * @return true是文件夹，否则不是
	 * */
	public abstract boolean isDirectory();

	@Override
	public String toString() {
		return getPath();
	}
	
}

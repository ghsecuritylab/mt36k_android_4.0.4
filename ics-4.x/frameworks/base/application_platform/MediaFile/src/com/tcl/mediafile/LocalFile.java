package com.tcl.mediafile;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/**
 * @author Hu Jianbin (hujb@tcl.com)
 * 该类继承自MediaFile，仅仅是对File类的一层包装，使其具有MediaFile接口。
 */
public class LocalFile extends MediaFile {
	private File mFile;
	private static FileFilter mFilter = new LocalFileFilter();
	
	public LocalFile(File f) {
		mFile = f;
		setName(f.getName());
	}
	
	public LocalFile(String fpath) {
		mFile = new File(fpath);
	}
	
	@Override
	public boolean createMediaFile() throws IOException {
		return mFile.createNewFile();
	}

	public boolean delete() {
		return mFile.delete();
	}
	
	public boolean mkdir() {
		return mFile.mkdir();
	}
	
	@Override
	public InputStream getInputStream() {
		InputStream os = null;
		try {
			os = new FileInputStream(mFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return os;
	}

	@Override
	public OutputStream getOutputStream() {
		OutputStream os = null;
		try {
			os = new FileOutputStream(mFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return os;
	}

	@Override
	public String getName() {
		return mFile.getName();
	}

	@Override
	public String getPath() {
		return mFile.getPath();
	}

	@Override
	public long length() {
		return mFile.length();
	}

	@Override
	public boolean isDirectory() {
		return mFile.isDirectory();
	}
	
	/**
	 * 列出改目录下的所有文件，但增加了Filter过滤，屏蔽系统文件及隐藏文件 (non-Javadoc)
	 * @see com.tcl.fileexplorer.mediafile.MediaFile#listMediaFiles()
	 * @see FileFilter
	 */
	@Override
	public List<MediaFile> listMediaFiles(MediaFileFilter filter) {
		List<MediaFile> list = new ArrayList<MediaFile>();
		File[] files;
		Log.d("", "---------------listFiles-----------------------" + mFile.getPath());
		files = mFile.listFiles();
		Log.d("", "---------------listFiles-----------------------");
		if (files == null)
			return null;
		for (File f: files) {
			if (mFilter != null && !mFilter.accept(f))
				continue;

			LocalFile lf = new LocalFile(f);
			if (filter != null && !filter.accept(lf))
				continue;
			lf.setParent(this);
			list.add(lf);
		}
		
		/*按字母序排序文件列表*/
		MediaFileNameComparator comparator = new MediaFileNameComparator();
		try {
		Collections.sort(list, comparator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public boolean canWrite() {
		return mFile.canWrite();
	}

	@Override
	public boolean exist() {
		return mFile.exists();
	}
}

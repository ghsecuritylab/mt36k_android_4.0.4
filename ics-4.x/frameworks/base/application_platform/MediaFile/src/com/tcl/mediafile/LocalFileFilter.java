package com.tcl.mediafile;

import java.io.File;
import java.io.FileFilter;

public class LocalFileFilter implements FileFilter {

	public boolean accept(File file) {
		if (file.getName().startsWith(".") || file.isHidden())
			return false;
		return true;
	}
}

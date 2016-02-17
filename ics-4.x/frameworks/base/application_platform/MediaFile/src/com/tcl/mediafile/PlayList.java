package com.tcl.mediafile;

import java.util.ArrayList;

import android.net.Uri;

public class PlayList {
	public int index = 0;
	public ArrayList<Uri> playList = new ArrayList<Uri>();
	
	public int size() {
		return playList.size();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PlayList: { ");
		sb.append("index: ").append(index).append(" size: ").append(playList.size());
		sb.append(" {");
		for (Uri uri: playList) {
			sb.append("\n-----   ").append(uri.toString());
		}
		sb.append(" }");
		return sb.toString();
	}
}

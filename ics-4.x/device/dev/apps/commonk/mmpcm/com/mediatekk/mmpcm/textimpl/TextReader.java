package com.mediatekk.mmpcm.textimpl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mediatekk.mmpcm.MmpTool;
import com.mediatekk.mmpcm.mmcimpl.Const;
import com.mediatekk.mmpcm.mmcimpl.PlayList;
import com.mediatekk.mmpcm.text.ITextEventListener;
import com.mediatekk.mmpcm.text.ITextReader;
import com.mediatekk.netcm.samba.SambaManager;

public class TextReader implements ITextReader {
	private final String localPath = "/tmp/temp.txt";
	private static int SCROLL_STEP = 3;
	private static ITextReader tr = null;
	private static boolean isAutoScrolling = false;
	private TextView tv;
	private ArrayList<SearchInfo> marks = null;
	private int searchCount = 0;
	private StringBuilder sb = null;
	private SpannableString ss = null;
	private int curPos = 0;
	private int mScreenHeight = 0;
	private long curtotalSrollY=0;
	private float curtotalScale=1;
	private ScrollView mScrollView = null;
	private ITextEventListener mTEListener = null;
	private long mTempCurScrollY =0;
	
	private long mTempTotalScrollY =0;

	private int mPlayMode = TextConst.PLAYER_MODE_LOCAL;

	/*
	 * readerStatus: 0 Normal 1 Abnormal 2 Busy loading the text
	 */
	private int readerStatus = TextConst.STATUS_NORMAL;

	private TextReader() {
	}

	public static ITextReader getTextReader() {
		if (tr == null) {
			MmpTool.LOG_DBG("First Create the TextReader Object");
			tr = new TextReader();
		}
		MmpTool.LOG_DBG("Get the existed TextReader Object");
		return tr;
	}

	public void setTv(TextView tv) {
		MmpTool.LOG_DBG("Set the TextView for the TextReader");
		this.tv = tv;
	}

	/**
	 * set play mode
	 */
	public void setPlayMode(int playMode) {
		mPlayMode = playMode;
	}

	static class SearchInfo {
		public SearchInfo(int index, int position) {
			super();
			this.index = index;
			this.position = position;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		private int index;
		private int position;
	}

	private Handler autoScrollHandle = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TextConst.BEGIN_SCROLL:
				MmpTool.LOG_DBG("Continue auto scroll");
				View view = (mScrollView != null ? mScrollView : tv);
				if (view.getScrollY() >= tv.getLineCount() * tv.getLineHeight()
						- mScreenHeight) {
					view.scrollTo(0, tv.getLineCount() * tv.getLineHeight()
							- mScreenHeight);
					MmpTool
							.LOG_DBG("Have scrolled to the end, stop auto scroll");
					autoScrollHandle.sendEmptyMessage(TextConst.END_SCROLL);
				} else {
					view.scrollTo(0, view.getScrollY() + SCROLL_STEP);
					autoScrollHandle.sendEmptyMessageDelayed(
							TextConst.BEGIN_SCROLL, 300);
				}

				break;
			case TextConst.END_SCROLL:
				autoScrollHandle.removeMessages(TextConst.STOP_SCROLL);
				autoScrollHandle.removeMessages(TextConst.BEGIN_SCROLL);
				break;
			case TextConst.STOP_SCROLL:
				MmpTool.LOG_DBG("Stop auto scroll");
				autoScrollHandle.removeMessages(TextConst.END_SCROLL);
				autoScrollHandle.removeMessages(TextConst.BEGIN_SCROLL);
				break;
			case TextConst.SETFONTSIZE:
				long cur=getTotalSrollY();
				if(cur==curtotalSrollY){
					autoScrollHandle.sendEmptyMessageDelayed(TextConst.SETFONTSIZE,30);
				}else{
					if(curtotalSrollY==0){
						mScrollView.scrollTo(0, (int)getCurScrollY());
					}else{
						//int curScaleScrollY=(int) (cur * curtotalScale-1);
						int curTempScrollY =(int) (mTempCurScrollY*cur/mTempTotalScrollY);
						if(mTempCurScrollY*cur%mTempTotalScrollY!=0)
						{
							if(cur>mTempTotalScrollY)
							{
								curTempScrollY-=1;
							}else if(cur<mTempTotalScrollY)
							{
								curTempScrollY+=1;
							}
						}
					   mScrollView.scrollTo(0, curTempScrollY);
					}
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * auto scroll(auto play)
	 * @param scrollSpeed   auto play speed
	 */
	public void autoScroll(int scrollSpeed) {
		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (scrollSpeed <= 0 && scrollSpeed > 30) {
			MmpTool
					.LOG_WARN("Not specify the correct speed,  1 - 30 is avaliable");
			return;
		}
		SCROLL_STEP = scrollSpeed;
		isAutoScrolling = !isAutoScrolling;
		if (isAutoScrolling) {
			autoScrollHandle.sendEmptyMessage(TextConst.BEGIN_SCROLL);

		} else {
			autoScrollHandle.sendEmptyMessage(TextConst.STOP_SCROLL);
		}
	}

	/**
	 * gain current page number
	 */
	public int getCurPagenum() {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return 0;
		}
		View view = (mScrollView != null ? mScrollView : tv);

		if (mScreenHeight == 0) {
			MmpTool
					.LOG_WARN("Need to refresh the textview for getting the correct pagenum");
			return 0;
		} else {
			if (view.getScrollY() + mScreenHeight >= (tv.getLineCount())
					* tv.getLineHeight())
				return getTotalPage();
			else
				return (view.getScrollY()+tv.getLineHeight()/2)/ mScreenHeight + 1;
		}
	}
	public long getCurScrollY() {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return 0;
		}
		View view = (mScrollView != null ? mScrollView : tv);

		if (mScreenHeight == 0) {
			MmpTool
					.LOG_WARN("Need to refresh the textview for getting the correct pagenum");
			return 0;
		} else {
			if (view.getScrollY() + mScreenHeight >= (tv.getLineCount())
					* tv.getLineHeight())
				return getTotalSrollY();
			else
				return view.getScrollY();
		}
	}

	/**
	 * gain current play position
	 */
	public int getCurPos() {
		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return 0;
		}
		View view = (mScrollView != null ? mScrollView : tv);

		Layout l = tv.getLayout();
		int line = l.getLineForVertical(view.getScrollY());
		int off = l.getOffsetForHorizontal(line, 0);
		MmpTool.LOG_DBG("Get the current Position:" + off);
		return off;
	}

	/**
	 * Get text of the total number of pages
	 */
	public int getTotalPage() {
		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return 0;
		}
		if (mScreenHeight == 0) {
			MmpTool
					.LOG_WARN("Need to refresh the textview for getting the correct pagenum");
			return 0;
		} else {
			if ((tv.getLineCount() * tv.getLineHeight()) % mScreenHeight != 0) {
				return (tv.getLineCount() * tv.getLineHeight()) / mScreenHeight
						+ 1;
			} else {
				return (tv.getLineCount() * tv.getLineHeight()) / mScreenHeight;
			}
		}
	}
	public long getTotalSrollY() {
		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return 0;
		}
		if (mScreenHeight == 0) {
			MmpTool
					.LOG_WARN("Need to refresh the textview for getting the correct pagenum");
			return 0;
		} else {
			if ((tv.getLineCount() * tv.getLineHeight()) % mScreenHeight != 0) {
				return (tv.getLineCount() * tv.getLineHeight());
			} else {
				return (tv.getLineCount() * tv.getLineHeight());
			}
		}
	}

	/**
	 * load need play position
	 * @param pos
	 */
	public void loadPos(int pos) {
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}

		View view = (mScrollView != null ? mScrollView : tv);

		Layout l = tv.getLayout();
		int line = l.getLineForOffset(pos);
		float sy = l.getLineBottom(line);
		MmpTool.LOG_DBG("Load the position:" + pos);
		view.scrollTo(0, (int) sy);

	}

	/**
	 * Jump to the next page
	 * 
	 */
	public void pageDown() {
		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}

		View view = (mScrollView != null ? mScrollView : tv);

		if ( tv.getLineCount() == 0 ) {
			MmpTool.LOG_WARN(view.getScrollY() +"   "+(tv.getLineCount() * tv.getLineHeight()
					- mScreenHeight * 2));
			view.scrollTo(0, view.getScrollY());
			
			return;
		}
		
		if (view.getScrollY() >= tv.getLineCount() * tv.getLineHeight()
				- mScreenHeight * 2) {
			
			MmpTool.LOG_INFO("Reach the last page");
			MmpTool.LOG_WARN(view.getScrollY() +"   "+(tv.getLineCount() * tv.getLineHeight()
					- mScreenHeight * 2));
			
			view.scrollTo(0, tv.getLineCount() * tv.getLineHeight()
					- mScreenHeight);
			
			if (mTEListener != null){
				mTEListener.onComplete();
			}
			
		} else {
			MmpTool.LOG_INFO("Page Down");
			MmpTool.LOG_WARN(view.getScrollY() +"   "+(tv.getLineCount() * tv.getLineHeight()
					- mScreenHeight * 2));
			view.scrollTo(0, view.getScrollY() + mScreenHeight);		
		}
	}
	
	/**
	 * Jump to the previous page
	 */
	public void pageUp() {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}

		View view = (mScrollView != null ? mScrollView : tv);

		if (view.getScrollY() <= mScreenHeight) {
			MmpTool.LOG_DBG("Reach the first page");
			view.scrollTo(0, 0);
		} else {
			MmpTool.LOG_DBG("Page up");
			if (getCurPagenum() == getTotalPage()) {
				view.scrollTo(0, (getTotalPage() - 2) * mScreenHeight);

			} else
				view.scrollTo(0, view.getScrollY() - mScreenHeight);
		}

	}

	private String getCharset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			bis.mark(1);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1){
				bis.close();
				return charset;
				}
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {
				int loc = 0;
				while ((read = bis.read()) != -1) {
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF)
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF)
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return charset;
	}

	private int play(TextView tv, View sv, String fileuri) {

		if (tv == null || fileuri == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}

		sv.scrollTo(0, 0);
		File file = new File(fileuri);

		if ((((float) (file.length())) / 1024 / 1024) > 1.5f) {
			if (mTEListener != null)
				{
					mTEListener.fileNotSupport();
				}
			tv.setText(null);
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}
		sb = new StringBuilder();
		try {
			FileInputStream fileIS = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fileIS, getCharset(file)));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			fileIS.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tv.setText(sb);

		if (mTEListener != null && file.length() == 0) {
			mTEListener.fileNotSupport();
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		} else if (mTEListener != null){
			mTEListener.onPrepare();
		}
		readerStatus = TextConst.STATUS_NORMAL;
		return readerStatus;

	}

	/**
	 * Playing the next text
	 */
	public int playNext() {

		if (mPlayMode == TextConst.PLAYER_MODE_DLNA
				|| mPlayMode == TextConst.PLAYER_MODE_HTTP) {
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}

		initReader();
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}
		PlayList pl = PlayList.getPlayList();
		String retFile = pl.getNext(Const.FILTER_TEXT, Const.AUTOPLAY);
		String fileuri = retFile;

		View view = (mScrollView != null ? mScrollView : tv);

		int status;
		
		if (mTEListener != null && fileuri == null) {
			
			mTEListener.onExit();			
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}
		
		if (mPlayMode == TextConst.PLAYER_MODE_LOCAL) {
			status = play(tv, view, fileuri);
		} else if (mPlayMode == TextConst.PLAYER_MODE_SAMBA) {
			String path = null;
			try {
				path = getLocalPath(SambaManager.getInstance()
						.getSambaDataSource(fileuri).newInputStream());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				readerStatus = TextConst.STATUS_ABNORMAL;
				return readerStatus;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				readerStatus = TextConst.STATUS_ABNORMAL;
				return readerStatus;
			}

			status = play(tv, view, path);
		} else {
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}

		autoScrollHandle.sendEmptyMessage(TextConst.STOP_SCROLL);
		return status;
		/*
		 * if (isAutoScrolling == true) isAutoScrolling = !isAutoScrolling;
		 */
	}

	/**
	 * Playing the previous text
	 */
	public int playPrev() {

		if (mPlayMode == TextConst.PLAYER_MODE_DLNA
				|| mPlayMode == TextConst.PLAYER_MODE_HTTP) {
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}

		initReader();
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}
		PlayList pl = PlayList.getPlayList();
		String retFile = pl.getNext(Const.FILTER_TEXT, Const.MANUALPRE);
		String fileuri = retFile;

		if (mTEListener != null && fileuri == null) {
			
			mTEListener.onExit();
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}
		
		View view = (mScrollView != null ? mScrollView : tv);

		int status;
		if (mPlayMode == TextConst.PLAYER_MODE_LOCAL) {
			status = play(tv, view, fileuri);
		} else if (mPlayMode == TextConst.PLAYER_MODE_SAMBA) {
			String path = null;
			try {
				path = getLocalPath(SambaManager.getInstance()
						.getSambaDataSource(fileuri).newInputStream());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				readerStatus = TextConst.STATUS_ABNORMAL;
				return readerStatus;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				readerStatus = TextConst.STATUS_ABNORMAL;
				return readerStatus;
			}

			status = play(tv, view, path);
		} else {
			status = TextConst.STATUS_ABNORMAL;
		}

		autoScrollHandle.sendEmptyMessage(TextConst.STOP_SCROLL);
		return status;
		/*
		 * if (isAutoScrolling == true) isAutoScrolling = !isAutoScrolling;
		 */
	}

	public void scrollLnDown() {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		View view = (mScrollView != null ? mScrollView : tv);

		if (view.getScrollY() > tv.getLineCount() * tv.getLineHeight()
				- mScreenHeight - tv.getLineHeight()) {
			MmpTool.LOG_DBG("Reach the last line");
			if (mTEListener != null){
				mTEListener.onComplete();
			}
		} else {
			MmpTool.LOG_DBG("Scroll line down");
			view.scrollTo(0, view.getScrollY() + tv.getLineHeight());
			
		}
	}

	public void scrollLnUp() {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}

		View view = (mScrollView != null ? mScrollView : tv);

		if (view.getScrollY() <= tv.getLineHeight()) {
			MmpTool.LOG_DBG("Reach the first line");
			view.scrollTo(0, 0);
		} else {
			MmpTool.LOG_DBG("Scroll line up");
			view.scrollTo(0, view.getScrollY() - tv.getLineHeight());
		}

	}

	/**
	 * set font color
	 * @param color 
	 * the string color name  such as"red"
	 */
	public void setFontColor(String color) {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (color.equalsIgnoreCase("red"))
			tv.setTextColor(Color.RED);
		else if (color.equalsIgnoreCase("green"))
			tv.setTextColor(Color.GREEN);
		else if (color.equalsIgnoreCase("black"))
			tv.setTextColor(Color.BLACK);
		else if (color.equalsIgnoreCase("yellow"))
			tv.setTextColor(Color.YELLOW);
		else if (color.equalsIgnoreCase("cyan"))
			tv.setTextColor(Color.CYAN);
		else if (color.equalsIgnoreCase("blue"))
			tv.setTextColor(Color.BLUE);
		else if (color.equalsIgnoreCase("gray"))
			tv.setTextColor(Color.GRAY);
		else if (color.equalsIgnoreCase("white"))
			tv.setTextColor(Color.WHITE);
		else {
			MmpTool.LOG_WARN("Not specify the correct color");
		}
	}

	/**
	 * set font size
	 * @param size
	 * the  less than 50 greater than 1 type floating point number
	 */
	public void setFontSize(float size) {

		if (tv == null  || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (size < 1f && size > 50f) {
			MmpTool
					.LOG_WARN("Not specify the correct size,  1f - 50f is avaliable");
		} else {
			curtotalSrollY=getTotalSrollY();
			if(curtotalSrollY!=0){
			    curtotalScale=((float)getCurScrollY())/((float)curtotalSrollY);
			    mTempCurScrollY=getCurScrollY();
			    mTempTotalScrollY=curtotalSrollY;
			}
			tv.setTextSize(size);
			autoScrollHandle.sendEmptyMessage(TextConst.SETFONTSIZE);
		}

	}

	/**
	 * set font style
	 * @param typeface   
     *Change the font type of the surface of the tv
	 * @param style   change the font style 
	 */
	public void setFontStyle(String typeface, String style) {

		if (tv == null  || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		int i_style;
		if (style.equalsIgnoreCase("BOLD"))
			i_style = Typeface.BOLD;
		else if (style.equalsIgnoreCase("ITALIC"))
			i_style = Typeface.ITALIC;
		else if (style.equalsIgnoreCase("Normal"))
			i_style = Typeface.NORMAL;
		else if (style.equalsIgnoreCase("BOLD_ITALIC"))
			i_style = Typeface.BOLD_ITALIC;
		else {
			MmpTool.LOG_WARN("Not specify the correct style");
			return;
		}

		if (typeface.equalsIgnoreCase("default"))
			tv.setTypeface(Typeface.DEFAULT, i_style);
		else if (typeface.equalsIgnoreCase("monospace"))
			tv.setTypeface(Typeface.MONOSPACE, i_style);
		else if (typeface.equalsIgnoreCase("serif"))
			tv.setTypeface(Typeface.SERIF, i_style);
		else if (typeface.equalsIgnoreCase("sans_serif"))
			tv.setTypeface(Typeface.SANS_SERIF, i_style);
		else {
			MmpTool.LOG_WARN("Not specify the correct typeface");
			return;
		}

		if (i_style == Typeface.BOLD || i_style == Typeface.BOLD_ITALIC) {
			TextPaint tp = tv.getPaint();
			tp.setFakeBoldText(true);
		}

	}

	/**
	 * skip to page you want
	 * @param pageNum
	 */
	public void skipToPage(int pageNum) {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}

		View view = (mScrollView != null ? mScrollView : tv);

		if (pageNum <= 0 || pageNum > getTotalPage()) {
			MmpTool.LOG_WARN("Not specify the correct pageNum");
		} else {
			view.scrollTo(0, (pageNum - 1) * mScreenHeight);
		}

	}

	/**
	 * gain current text
	 */
	public String getCurText() {

		if (tv == null || readerStatus != TextConst.STATUS_NORMAL) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return null;
		}

		View view = (mScrollView != null ? mScrollView : tv);

		int line = tv.getLayout().getLineForVertical(
				+view.getScrollY() - tv.getTotalPaddingTop());
		int st = tv.getLayout().getOffsetForHorizontal(line,
				view.getScrollX() - tv.getTotalPaddingLeft());
		String content = tv.getText().toString().substring(st);
		return content;
	}

	public int playFirst() {

		if (mPlayMode == TextConst.PLAYER_MODE_DLNA
				|| mPlayMode == TextConst.PLAYER_MODE_HTTP) {
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}

		initReader();
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}
		String retFile;
		PlayList pl = PlayList.getPlayList();
		//pl.setRepeatMode(Const.FILTER_TEXT, Const.REPEAT_ALL);
		retFile = pl.getCurrentPath(Const.FILTER_TEXT);
		String path = retFile;
		View view = (mScrollView != null ? mScrollView : tv);

		if (mPlayMode == TextConst.PLAYER_MODE_LOCAL) {
			return play(tv, view, path);
		} else if (mPlayMode == TextConst.PLAYER_MODE_SAMBA) {

			try {
				path = getLocalPath(SambaManager.getInstance()
						.getSambaDataSource(path).newInputStream());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				readerStatus = TextConst.STATUS_ABNORMAL;
				return readerStatus;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				readerStatus = TextConst.STATUS_ABNORMAL;
				return readerStatus;
			}

			return play(tv, view, path);
		} else {
			readerStatus = TextConst.STATUS_ABNORMAL;
			return readerStatus;
		}
	}

	private String getLocalPath(InputStream input) throws IOException {
		String path = localPath;
		int bufSize = 1024;
		File file = new File(path);

		file.deleteOnExit();

		byte[] buffer = new byte[bufSize];
		FileOutputStream output = new FileOutputStream(file);

		while (true) {
			int ret = input.read(buffer);
			if (ret == -1) {
				break;
			}

			output.write(buffer, 0, ret);
		}

		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * gain preview buffer
	 * @param fileuri path
	 */
	public String getPreviewBuf(String fileuri) {

		File file = new File(fileuri);
		final StringBuilder sb = new StringBuilder();
		try {
			FileInputStream fileIS = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fileIS, getCharset(file)));
			String line;
			for (int i = 0; i <= 20; i++) {
				if ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				} else
					break;
			}
			fileIS.close();
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	private void initReader() {

		MmpTool.LOG_DBG("Initialize the textReader");
		isAutoScrolling = false;
		marks = new ArrayList<SearchInfo>();
		searchCount = 0;
		ss = null;
		sb = null;
		curPos = 0;
		readerStatus = TextConst.STATUS_NORMAL;
	}

	/**
	 * search next text
	 */
	public void searchNext() {
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (ss == null) {
			MmpTool.LOG_WARN("No SpannableString");
			return;
		}
		searchCount++;
		if (searchCount > marks.size() || searchCount == 0) {
			MmpTool.LOG_DBG("Return to the head of the text");
			searchCount = 0;
			tv.setText(ss);
		} else {
			MmpTool.LOG_DBG("Find the next keyword");
			int i = (marks.get(searchCount - 1).position >= 10) ? (marks
					.get(searchCount - 1).position - 10) : (marks
					.get(searchCount - 1).position);
			tv.setText(ss.subSequence(i, ss.length()));
		}

	}

	/**
	 * search preview text
	 */
	public void searchPrev() {
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (ss == null) {
			MmpTool.LOG_WARN("No SpannableString");
			return;
		}
		searchCount--;
		if (searchCount <= 0) {
			MmpTool.LOG_DBG("Return to the end of the text");
			searchCount = marks.size();
			tv.setText(ss.subSequence(marks.get(searchCount - 1).position, ss
					.length()));
		} else {

			MmpTool.LOG_DBG("Find the prev keyword");
			int i = (marks.get(searchCount - 1).position >= 10) ? (marks
					.get(searchCount - 1).position - 10) : (marks
					.get(searchCount - 1).position);
			tv.setText(ss.subSequence(i, ss.length()));
		}
		// searchCount--;
		MmpTool.LOG_INFO("--Count Values" + searchCount + "");
	}

	/**
	 * search text you want and set color
	 * @param color,str
	 */
	public void searchText(String str, String color) {
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (sb == null) {
			MmpTool.LOG_WARN("No Stringbuilder");
			return;
		}
		curPos = getCurPos();
		int colorValue;
		if (color.equalsIgnoreCase("red"))
			colorValue = Color.RED;
		else if (color.equalsIgnoreCase("green"))
			colorValue = Color.GREEN;
		else if (color.equalsIgnoreCase("black"))
			colorValue = Color.BLACK;
		else if (color.equalsIgnoreCase("yellow"))
			colorValue = Color.YELLOW;
		else if (color.equalsIgnoreCase("cyan"))
			colorValue = Color.CYAN;
		else if (color.equalsIgnoreCase("blue"))
			colorValue = Color.BLUE;
		else if (color.equalsIgnoreCase("gray"))
			colorValue = Color.GRAY;
		else if (color.equalsIgnoreCase("white"))
			colorValue = Color.WHITE;
		else {

			return;
		}
		ss = new SpannableString(sb);
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(ss);
		int i = 0;
		MmpTool.LOG_DBG("Begin to generate the searchinfo");
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			SearchInfo mInfo = new SearchInfo(i, start);
			marks.add(mInfo);
			i++;
			ss.setSpan(new ForegroundColorSpan(colorValue), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		MmpTool.LOG_DBG("Show the text with the keyword " + str
				+ "on the specified color");
		tv.setText(ss);
	}

	/**
	 * set background color 
	 * @param color
	 */
	public void setBackgroundColor(String color) {

		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (color == null) {
			MmpTool.LOG_WARN("Not specify the correct color");
			return;
		}
		if (color.equalsIgnoreCase("red"))
			tv.setBackgroundColor(Color.RED);
		else if (color.equalsIgnoreCase("green"))
			tv.setBackgroundColor(Color.GREEN);
		else if (color.equalsIgnoreCase("black"))
			tv.setBackgroundColor(Color.BLACK);
		else if (color.equalsIgnoreCase("yellow"))
			tv.setBackgroundColor(Color.YELLOW);
		else if (color.equalsIgnoreCase("cyan"))
			tv.setBackgroundColor(Color.CYAN);
		else if (color.equalsIgnoreCase("blue"))
			tv.setBackgroundColor(Color.BLUE);
		else if (color.equalsIgnoreCase("gray"))
			tv.setBackgroundColor(Color.GRAY);
		else if (color.equalsIgnoreCase("white"))
			tv.setBackgroundColor(Color.WHITE);
		else {
			MmpTool.LOG_WARN("Not specify the correct color");
			return;
		}
	}

	/**
	 * exit search
	 */
	public void exitSearch() {
		if (tv == null) {
			MmpTool.LOG_WARN("No Textview specified, Need to set a Textview");
			return;
		}
		if (sb == null) {
			MmpTool.LOG_WARN("No Stringbuilder");
			return;
		}
		tv.setText(sb);
		MmpTool
				.LOG_DBG("Exit the search mode and return to the position before");
		loadPos(curPos);
	}

	public void setScreenHeight(int screenHeight) {
		mScreenHeight = screenHeight;
	}

	public void setScrollView(ScrollView sv) {
		mScrollView = sv;
	}

	/**
	 * set error listener
	 * @param ITextEventListener tEListener
	 */
	public void setErrorListener(ITextEventListener tEListener) {
		mTEListener = tEListener;
	}
}

package com.mediatekk.mmpcm.text;

import android.widget.ScrollView;
import android.widget.TextView;

public interface ITextReader {

	/**
	 * Set the related TextView Object
	 * 
	 * @param TextView tv
	 *            a Textview object related to TextReader
     *
	 * @return 
	 */
	public void setTv(TextView tv);

	/**
	 * Set the play mode
	 * 
	 * @param int playMode
	 *            0:PLAYER_MODE_LOCAL
	 *            1:PLAYER_MODE_SAMBA
	 *            2:PLAYER_MODE_DLNA
	 *            3:PLAYER_MODE_HTTP
     *
	 * @return 
	 */
	public void setPlayMode(int playMode);
	
	/**
	 * Be called while playing a text file for the first time
	 * 
	 * @param
	 *       
     *
	 * @return 
	 */
	public int playFirst();

	/**
	 * Switch to next text file during playing status
	 * 
	 * @param
	 *       
     *
	 * @return 
	 */
	public int playNext();

	/**
	 * Switch to prev text file during playing status
	 * 
	 * @param
	 *       
     *
	 * @return 
	 */
	public int playPrev();

	/**
	 * Get the current playing position(can be used to save a bookmark)
	 * 
	 * @param
	 *       
     *
	 * @return current reading position (int)
	 */
	public int getCurPos();

	/**
	 * Load reading position
	 * 
	 * @param int
	 *           reading position
     *
	 * @return 
	 */
	public void loadPos(int pos);

	/**
	 * Scroll down a line
	 * 
	 * @param
	 *       
     *
	 * @return 
	 */
	public void scrollLnDown();

	/**
	 * scroll up a line 
	 * 
	 * @param
	 *       
     *
	 * @return 
	 */
	public void scrollLnUp();

	/**
	 * Page down
	 * 
	 * @param
	 *       
     *
	 * @return 
	 */
	public void pageDown();
	

	/**
	 * Page up
	 * 
	 * @param
	 *       
     *
	 * @return 
	 */
	public void pageUp();

	/**
	 * Get the total page number (can be affected by the font and the textview size)
	 * 
	 * @param
	 *       
     *
	 * @return return the total page number for current text (int)
	 */
	public int getTotalPage();

	/**
	 * Get the current playing page NO.
	 * 
	 * @param
	 *       
     *
	 * @return return the current page NO. (int)
	 */
	public int getCurPagenum();

	/**
	 * Switch to a specified page NO.
	 * 
	 * @param
	 *       int pageNum   specified page NO.
     *
	 * @return 
	 */
	public void skipToPage(int pageNum);

	/**
	 * Set font color
	 * 
	 * @param
	 *       String color  :font color red,green,black,yellow,cyan,blue,green,gray,white
     *
	 * @return 
	 */
	public void setFontColor(String color);

	/**
	 * Set font size
	 * 
	 * @param
	 *       float size :font size between 1.0f and 50.0f float
	 *
	 * @return 
	 */
	public void setFontSize(float size);

	/**
	 * Set font style
	 * 
	 * @param
	 *       String typeface default,monospace,serif,san_serif 
	 *     
	 * @param
	 *       String style bold,italic,normal
	 * @return 
	 */
	public void setFontStyle(String typeface, String style);

	/**
	 * Switch on/off the autoscroll function
	 * 
	 * @param
	 *       int scrollSpeed 1-30 1--slowest 30--fastest
     *
	 * @return 
	 */
	public void autoScroll(int scrollSpeed);

	/**
	 * Get the text content from current postion to the end of the text
	 * 
	 * @param
	 *       
	 *
	 * @return 
	 */
	public String getCurText();
	
	/**
	 * Get 20 lines from the beginning for text's preview 
	 * 
	 * @param
	 *       
	 *
	 * @return 
	 */
	public String getPreviewBuf(String fileuri);
	/**
	 * Search the string in the text and set the keywords' color
	 * 
	 * @param
	 *       
	 *
	 * @return 
	 */
	public void searchText(String str, String color);
	/**
	 * Entering the search mode
	 * Search the next string and scroll to the position
	 * 
	 * @param
	 *       
	 *
	 * @return 
	 */
	public void searchNext();
	/**
	 * Search the prev string and scroll to the position
	 * 
	 * @param
	 *       
	 *
	 * @return 
	 */
	public void searchPrev();
	/**
	 * Set the background color
	 * 
	 * @param
	 *       
	 *
	 * @return 
	 */ 
	public void setBackgroundColor(String color);
	/**
	 * Exit the search mode and return to the position before entering the search mode
	 * @param
	 *       
	 *
	 * @return 
	 */
	public void exitSearch();	
	
	
	/**
	 * set current screen screenHeight
	 * @param screenHeight
	 */
	public void setScreenHeight(int screenHeight);
		
	/**
	 * Set scroll View
	 * @param sv
	 */
	public void setScrollView(ScrollView sv);
	/**
	 * reigset error call back function
	 * @param tEListener
	 */
	public void setErrorListener(ITextEventListener tEListener);

}

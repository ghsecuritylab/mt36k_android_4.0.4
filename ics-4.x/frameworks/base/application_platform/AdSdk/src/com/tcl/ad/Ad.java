package com.tcl.ad;

import android.widget.LinearLayout;

/**
 * ----------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ----------------------------------------------------------
 * 
 * @author Dang Jie/dangj@tcl.com/2011.10.12
 * 
 * @JDK version: 1.5
 * @brief: An interface for all types of ads.   
 * @version: v1.0
 *
 */
public interface Ad{
	public boolean isReady();
	public void loadAd(AdRequest adRequest,  LinearLayout parenet);
	public void setAdListener(AdListener adListener);
}

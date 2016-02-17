package com.tcl.ad;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


/**
 * @author haozhanfeng
 * @version dd
 * @date 2012-06-11
 * 
 */
public class AdPictureDialog2 extends Dialog{

	private Context cc;
	private WebView webView;
	private int width;
	private int height;
	//private Handler mhandler = new Handler();
	private String pictureUrl ;
	private int pHeight;
	private int pWidth;
	/**
	 * @param mWidth The absolute width of the display in pixels.
	 * @param mHeight The absolute height of the display in pixels.
	 * @param Pw The absolute width of the picture in pixels.
	 * @param Ph The absolute height of the picture in pixels.
	 * @param mPictureUrl The URL of the resource to load.
	 */
	public AdPictureDialog2(Context context, int mWidth, int mHeight,String mPictureUrl,int Pw,int Ph) {
		super(context);
		// TODO Auto-generated constructor stub
		cc=context;
		
		Log.v("AdPictureDialog", mWidth + " " + mHeight + " " + Pw + " " + Ph);
		
		if(mWidth<=Pw){
			this.width = mWidth;
			this.pWidth = mWidth - 40;
		}else{
			this.width = Pw + 15;
			this.pWidth  = Pw;
		}
		if(mHeight<=Ph){
			this.height = mHeight;
			this.pHeight = mHeight - 40 ;
		}else{
			this.height = Ph + 15;
			this.pHeight = Ph;
		}
//		this.width = mWidth;
//		this.height = mHeight;
		this.pictureUrl = mPictureUrl;
//		if(mWidth<Ph)
//		this.pHeight = Ph;
//		this.pWidth = Pw;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		this.setContentView(R.layout.dailog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	        this.setCancelable(true);
	        
	      
	        //Initialize the WebView object with data from the 'webviewdialog.xml' file 
//	        webView = (WebView) this.findViewById(R.id.webview);
	        webView = new WebView(cc);
	        this.setContentView(webView);
	        Log.i("haozhanfeng", ""+webView);
	        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) webView.getLayoutParams();
//	        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) webView.getLayoutParams(); 
	        
	        linearParams.width = width ;
	        linearParams.height = height ;
	        webView.setLayoutParams(linearParams);
	        
//	        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) webView.getLayoutParams(); 
//	        linearParams.width = 800 + RIGHTWIDTH;
//	        webView.setLayoutParams(linearParams);
	        webView.getSettings().setJavaScriptEnabled(true);
	        webView.getSettings().setUserAgentString("AndroidWebView");
	        webView.clearCache(true);
	        webView.addJavascriptInterface(new Object(){
	        	public void onclickonRoid(){
	        		webView.loadUrl("javascript:wave('"+pictureUrl+"','"+pWidth+"','"+pHeight+"')");
//	        		mhandler.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							 
//							webView.loadUrl("javascript:wave('"+pictureUrl+"','"+pWidth+"','"+pHeight+"')");
//						}
//					});
	        	}
	        }, "demo");
	        webView.loadUrl("file:///android_asset/index.html");
	     
	}

}
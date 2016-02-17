package android.tclwidget;	

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class TCLDLabel extends Dialog
{
	private static TCLDLabel tcl_label;
	private static String text;
	private static TextView textview;
	  
    private static ImageView imgDance;
    
    private static AnimationDrawable animDance;
	public TCLDLabel(Context context)
	{
		super(context,com.android.internal.R.style.TCLDLabelDialog);
	}
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.tcl_dlabel);
//		imgDance = (ImageView) super.findViewById(R.id.progress);
//		 
//        animDance = (AnimationDrawable) imgDance.getBackground();
//		textView = (TextView)findViewById(R.id.text);
//		if(text != null){
//			textView.setText(text);
//		}
//	}
	public TCLDLabel setMessage(String text)
	{
		textview.setText(text);
		return  this;
	}
	public static TCLDLabel makeTCLDLabel(Context context){
		
		tcl_label = new TCLDLabel(context);
		LayoutInflater inflate = (LayoutInflater)
		         context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 
		        View v = inflate.inflate(com.android.internal.R.layout.tcl_dlabel, null);
		        imgDance = (ImageView) v.findViewById(com.android.internal.R.id.progress);
				 
		        animDance = (AnimationDrawable) imgDance.getBackground();
		        
		        textview = (TextView)v.findViewById(com.android.internal.R.id.text);           
		        tcl_label.setContentView(v);
		        animDance.start();
				return tcl_label;		
		
	}
	public static TCLDLabel makeTCLDLabel(Context context,String text){
        
        tcl_label = new TCLDLabel(context);
        LayoutInflater inflate = (LayoutInflater)
                 context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                         
                View v = inflate.inflate(com.android.internal.R.layout.tcl_dlabel, null);
                imgDance = (ImageView) v.findViewById(com.android.internal.R.id.progress);
                         
                animDance = (AnimationDrawable) imgDance.getBackground();
                
                TextView tv = (TextView)v.findViewById(com.android.internal.R.id.text);     
                tv.setText(text);      
                tcl_label.setContentView(v);
                animDance.start();
                        return tcl_label;                
        
}
	private static void setBackgroundWindow(){
		Window window = tcl_label.getWindow(); 
		//window.setFlags (WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
		//WindowManager.LayoutParams.FLAG_BLUR_BEHIND );
		window.setFlags (WindowManager.LayoutParams.FLAG_DIM_BEHIND, 
		WindowManager.LayoutParams.FLAG_DIM_BEHIND );
		WindowManager.LayoutParams lp = window.getAttributes(); 
		lp.dimAmount=0.7f;    
		window.setAttributes(lp);     
	 }
	public void setAnimationMode(int mode){

	}
//	public void show(){
//		animDance.start();
//	}
}
	
	

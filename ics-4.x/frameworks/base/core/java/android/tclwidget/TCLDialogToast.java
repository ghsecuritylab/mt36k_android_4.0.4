package android.tclwidget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class TCLDialogToast extends Dialog{
	/**
	* The TCL_Info image,This is the default
	*/
	public static final int INFO_IMAGE = 0;

	/**
	* The TCL_Warning image
	*/
	public static final int WARNING_IMAGE = 1;
	/**
	* The TCL_Question image
	*/
	public static final int QUESTION_IMAGE = 2;

	static TCLDialogToast mDialog;

	public TCLDialogToast(Context context) {
		this(context, com.android.internal.R.style.TCLDialogToast);
	}

	public TCLDialogToast(Context context, int theme) {
		super(context,
				 (theme==0) ? com.android.internal.R.style.TCLDialogToast : theme);	
		// TODO Auto-generated constructor stub
	}
	
public static TCLDialogToast makeText(Context context, CharSequence text){
		
		mDialog = new TCLDialogToast(context);	
		//setBackgroundWindow();
		LayoutInflater inflate = (LayoutInflater)
         context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
   
        View v = inflate.inflate(com.android.internal.R.layout.tcl_toast, null);
        
        TextView tv = (TextView)v.findViewById(com.android.internal.R.id.message);     
      
       
        tv.setText(text);      
        mDialog.setContentView(v);
      
		return mDialog;		
	}
public static TCLDialogToast makeText(Context context, int resid){
	
	mDialog = new TCLDialogToast(context);	
	//setBackgroundWindow();
	LayoutInflater inflate = (LayoutInflater)
     context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 

    View v = inflate.inflate(com.android.internal.R.layout.tcl_toast, null);
    
    TextView tv = (TextView)v.findViewById(com.android.internal.R.id.message);     
  
   
    tv.setText(resid);      
    mDialog.setContentView(v);
  
	return mDialog;		
}
	
	public static TCLDialogToast makePrompt(Context context, CharSequence text,int imagesrc){
		mDialog = new TCLDialogToast(context);	
		//setBackgroundWindow();
		LayoutInflater inflate = (LayoutInflater)
         context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		View view=inflate.inflate(com.android.internal.R.layout.tcl_toast_img,null);

		ImageView iv= (ImageView)view.findViewById(com.android.internal.R.id.pic);
		if(imagesrc==1){
			iv.setImageResource(com.android.internal.R.drawable.tcl_warning);			
		}
		if(imagesrc==2){
			iv.setImageResource(com.android.internal.R.drawable.tcl_question);			
		}
		if((imagesrc!=0)&&(imagesrc!=1)&&(imagesrc!=2)){
			iv.setImageResource(imagesrc);	
		}
		TextView tv=(TextView)view.findViewById(com.android.internal.R.id.message2);	
		tv.setText(text);		
		mDialog.setContentView(view);		
		return mDialog;
	}
   
	public static TCLDialogToast makePrompt(Context context, int resid,int imagesrc){
		mDialog = new TCLDialogToast(context);	
		//setBackgroundWindow();

		LayoutInflater inflate = (LayoutInflater)
         context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		View view=inflate.inflate(com.android.internal.R.layout.tcl_toast_img,null);

		ImageView iv= (ImageView)view.findViewById(com.android.internal.R.id.pic);
		if(imagesrc==1){
			iv.setImageResource(com.android.internal.R.drawable.tcl_warning);			
		}
		if(imagesrc==2){
			iv.setImageResource(com.android.internal.R.drawable.tcl_question);			
		}
		if((imagesrc!=0)&&(imagesrc!=1)&&(imagesrc!=2)){
			iv.setImageResource(imagesrc);	
		}
		TextView tv=(TextView)view.findViewById(com.android.internal.R.id.message2);	
		tv.setText(resid);		
		mDialog.setContentView(view);		
		return mDialog;
	}
	public static class TCLTimer {
		  private final Timer timer = new Timer();
		    private final int seconds;
		    public TCLTimer(int seconds) { 
		       this.seconds = seconds;
		    }
		    public void start(final TCLDialogToast dialog) { 
		       timer.schedule(new TimerTask() { 
		           public void run() { 
		               closeDialog(dialog); 
		               timer.cancel();
		            } 
		           private void closeDialog(TCLDialogToast dialog) { 
		              dialog.dismiss();
		            } 
		       }, seconds * 1000);
		    } 
	}
   
	public void dismissToast(int seconds){
		TCLTimer tclTimer = new TCLTimer(seconds);
		tclTimer.start(mDialog);
	}
	
	private static void setBackgroundWindow(){
		Window window = mDialog.getWindow(); 
		//window.setFlags (WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
		//		WindowManager.LayoutParams.FLAG_BLUR_BEHIND );
		window.setFlags (WindowManager.LayoutParams.FLAG_DIM_BEHIND, 
				WindowManager.LayoutParams.FLAG_DIM_BEHIND );
	    WindowManager.LayoutParams lp = window.getAttributes(); 
	    lp.dimAmount=0.7f;    
	    window.setAttributes(lp);     
	}
	public void setAnimationMode(int mode){

	}
	


}

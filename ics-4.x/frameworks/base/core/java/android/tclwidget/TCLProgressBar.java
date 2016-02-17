package android.tclwidget;

import com.android.internal.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.content.res.TypedArray;


public class TCLProgressBar extends ProgressBar{
	public TCLProgressBar(Context context) {
		super(context);
	}
	public TCLProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		drawableOfStyleCircle(context, attrs); //add by liuyan 20120524
	}

	public TCLProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
		drawableOfStyleCircle(context, attrs); //add by liuyan 20120524
	}  
    
    /**
    *style?atclprogressBarStyleCircle,∩車D?℅?那那車|米??那足aDT??
    *add by liuyan 20120524
    */
	public void drawableOfStyleCircle(Context context, AttributeSet attrs){
		TypedArray a =
			context.obtainStyledAttributes(attrs, R.styleable.ProgressBar, com.android.internal.R.attr.progressBarStyle, 0);
		
		boolean mOnlyIndeterminate=false;
		Drawable drawable = a.getDrawable(R.styleable.ProgressBar_progressDrawable);    
		mOnlyIndeterminate = a.getBoolean(
			R.styleable.ProgressBar_indeterminateOnly, mOnlyIndeterminate);
		
		if(drawable == null && mOnlyIndeterminate){
			drawable = a.getDrawable(R.styleable.ProgressBar_indeterminateDrawable);
			if(drawable != null){
				setIndeterminateDrawable(drawable);
			}  
		} 

		a.recycle();
	}

}


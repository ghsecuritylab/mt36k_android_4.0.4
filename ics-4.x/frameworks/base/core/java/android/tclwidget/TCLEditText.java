package android.tclwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class TCLEditText extends EditText{
	public TCLEditText(Context context) {
		//super(context);
 		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public TCLEditText(Context context, AttributeSet attrs) {
		this(context, attrs, com.android.internal.R.attr.tcleditTextStyle);
		//super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public TCLEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	
	
}

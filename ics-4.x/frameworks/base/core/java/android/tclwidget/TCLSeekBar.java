package android.tclwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class TCLSeekBar extends SeekBar{

	public TCLSeekBar(Context context) {
		//super(context);
	
		this(context, null);
	}

	public TCLSeekBar(Context context, AttributeSet attrs) {
		//super(context, attrs);
		this(context, attrs, com.android.internal.R.attr.tclseekBarStyle);
	}

	public TCLSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}

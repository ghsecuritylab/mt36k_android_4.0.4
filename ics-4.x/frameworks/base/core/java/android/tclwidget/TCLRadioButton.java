package android.tclwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class TCLRadioButton extends RadioButton {
    
    public TCLRadioButton(Context context) {
        this(context, null);
    }
    
    public TCLRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.tclradioButtonStyle);
    }

    public TCLRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}

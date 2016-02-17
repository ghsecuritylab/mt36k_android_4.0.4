package com.tcl.gesture;

import com.tcl.gesture.EyeSightEngController.ActionHandleStrategy;

import android.view.View;

abstract public class ViewsActionHandleStrategy implements ActionHandleStrategy{
    View[] mViews;
    int currentPosition;
    
    public ViewsActionHandleStrategy(View[] views){
        mViews = views;
    }
    
    
    public int getCurrentPosition() {
        return currentPosition;
    }

    
    public boolean handleLeft() {
        currentPosition--;
        if(currentPosition < 0){
            currentPosition = 0;
        }
        mViews[currentPosition].requestFocus();
        return true;
    }

    
    public boolean handleRight() {
        currentPosition++;
        if(currentPosition >= mViews.length){
            currentPosition = mViews.length - 1;
        }
        mViews[currentPosition].requestFocus();
        return true;
    }

}

package com.mediatek.tv.model;

public interface InputListener {
    public enum InputListenerSignalSatus{
    	SignalStatusUnknown,
        SignalSatusLoss,
        SignalSatusLocked
    }
    
    public enum InputListenerEventNotify{
    	InputEventUnknown,
        InputEventNoSignal,
        InputEventWithSignal,
        InputEventVideoUpdate
    }
    
    public void notifyInputGotSignal(String input );
    
    public void notifyOutputOperatorDone(String output );
    
    public void notifyOutputSignalSatus(String output, InputListenerSignalSatus signalSatus );
    
    public void notifyOutputEvent(String output, InputListenerEventNotify eventNotify );
}


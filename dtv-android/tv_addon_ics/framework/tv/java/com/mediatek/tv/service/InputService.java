package com.mediatek.tv.service;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.os.RemoteException;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.CIPath;
import com.mediatek.tv.model.InputExchange;
import com.mediatek.tv.model.InputExchangeOutputMute;
import com.mediatek.tv.model.InputListener;
import com.mediatek.tv.model.InputListener.InputListenerEventNotify;
import com.mediatek.tv.model.InputListener.InputListenerSignalSatus;
import com.mediatek.tv.model.InputRecord;
import com.mediatek.tv.model.InputRegion;
import com.mediatek.tv.service.InputService.InputServiceListener.InputEventNotify;
import com.mediatek.tv.service.InputService.InputServiceListener.InputSignalStatus;

@SuppressWarnings("unused")
/**
 * This class provides Input service
 * <ul>
 * 
 * </ul>
 */
public class InputService implements IService {
    /*---------------------------------------public static -------------------------------------*/
    public static String InputServiceName = "InputService";

    public static final int INPUT_RET_OK = 0;
    public static final int INPUT_RET_FAIL = -1;

    public static final String INPUT_TYPE_TV = "tv";
    public static final String INPUT_TYPE_AV = "av"; /* COMBI */
    public static final String INPUT_TYPE_VGA = "vga";
    public static final String INPUT_TYPE_SVIDEO = "svideo";
    public static final String INPUT_TYPE_COMPONENT = "component";/* YPBPR */
    public static final String INPUT_TYPE_COMPOSITE = "composite";/* CVBS */
    public static final String INPUT_TYPE_HDMI = "hdmi";

    public static final String INPUT_OUTPUT_MAIN = "main";
    public static final String INPUT_OUTPUT_SUB = "sub";
    
    public static final String INPUT_VIDEO_HD = "HD";
    public static final String INPUT_VIDEO_SD = "SD";
    
    public enum InputState{
        INPUT_STATE_UNKNOWN,
        INPUT_STATE_NORMAL,
        INPUT_STATE_PIP,
        INPUT_STATE_POP
    }

    /*---------------------------------------private static -------------------------------------*/
    private static final String TAG = "[J]InputService";
    private static final String INPUT_TYPE_RESERVED = "reserved";

    private static final int INPUT_SIGNAL_UNKOWN = 0;
    private static final int INPUT_SIGNAL_LOSS = INPUT_SIGNAL_UNKOWN + 1;
    private static final int INPUT_SIGNAL_LOCKED = INPUT_SIGNAL_LOSS + 1;
    private static final int INPUT_VIDEO_UPDATE  = INPUT_SIGNAL_LOCKED + 1;
    
    private static final int INPUT_COLOR_SYS_UNKNOWN = 0;
    private static final int INPUT_COLOR_SYS_NTSC = INPUT_COLOR_SYS_UNKNOWN + 1;
    private static final int INPUT_COLOR_SYS_PAL = INPUT_COLOR_SYS_NTSC + 1;
    private static final int INPUT_COLOR_SYS_SECAM = INPUT_COLOR_SYS_PAL + 1;
    private static final int INPUT_COLOR_SYS_NTSC_443 = INPUT_COLOR_SYS_SECAM + 1;
    private static final int INPUT_COLOR_SYS_PAL_M = INPUT_COLOR_SYS_NTSC_443 + 1;
    private static final int INPUT_COLOR_SYS_PAL_N = INPUT_COLOR_SYS_PAL_M + 1;
    private static final int INPUT_COLOR_SYS_PAL_60 = INPUT_COLOR_SYS_PAL_N + 1;

    /* EXCHANG: get/set type ------------- start */
    private static final int INPUT_EXCHANGE_HEADER_OUTPUT_IDX = 0;
    private static final int INPUT_EXCHANGE_HEADER_TYPE_IDX = INPUT_EXCHANGE_HEADER_OUTPUT_IDX + 1;
    private static final int INPUT_EXCHANGE_HEADER_LEN = INPUT_EXCHANGE_HEADER_TYPE_IDX + 1;
    
    private static final int INPUT_GET_TYPE_UNKNOWN = 0;
    private static final int INPUT_GET_TYPE_OUTPUT_REGION_CAPABILITY = INPUT_GET_TYPE_UNKNOWN + 1;
    private static final int INPUT_GET_TYPE_VIDEO_REGION_CAPABILITY = INPUT_GET_TYPE_OUTPUT_REGION_CAPABILITY + 1;
    private static final int INPUT_GET_TYPE_VIDEO_RESOLUTION = INPUT_GET_TYPE_VIDEO_REGION_CAPABILITY + 1;
    private static final int INPUT_GET_TYPE_COLOR_SYS = INPUT_GET_TYPE_VIDEO_RESOLUTION + 1;
    private static final int INPUT_GET_TYPE_INPUT_SIGNAL_STATUS = INPUT_GET_TYPE_COLOR_SYS + 1;
    private static final int INPUT_GET_TYPE_INPUT_PLANE_ORDER = INPUT_GET_TYPE_INPUT_SIGNAL_STATUS + 1;
    private static final int INPUT_GET_TYPE_PLANE_ARRAY = INPUT_GET_TYPE_INPUT_PLANE_ORDER + 1;
    private static final int INPUT_GET_TYPE_ASPECT_RATIO_ENABLE = INPUT_GET_TYPE_PLANE_ARRAY + 1;
    
    private static final int INPUT_SET_TYPE_UNKNOWN = 300;
    private static final int INPUT_SET_TYPE_PLANE_ORDER = INPUT_SET_TYPE_UNKNOWN + 1;
    private static final int INPUT_SET_TYPE_PLANE_ORDER_SWAP = INPUT_SET_TYPE_PLANE_ORDER + 1;
    private static final int INPUT_SET_AUTO_ADJUST = INPUT_SET_TYPE_PLANE_ORDER_SWAP + 1;
    private static final int INPUT_SET_ENTER_POP_AND_RETURN_FOCUS = INPUT_SET_AUTO_ADJUST + 1;
    private static final int INPUT_SET_ENTER_PIP_AND_RETURN_FOCUS = INPUT_SET_ENTER_POP_AND_RETURN_FOCUS + 1;
    private static final int INPUT_SET_ENTER_NORMAL = INPUT_SET_ENTER_PIP_AND_RETURN_FOCUS + 1;
    private static final int INPUT_SET_FOCUS_CHANGE_TO = INPUT_SET_ENTER_NORMAL + 1;
    private static final int INPUT_SET_STOP_OUTPUT = INPUT_SET_FOCUS_CHANGE_TO + 1;
    private static final int INPUT_SET_TYPE_PLANE_ARRAY = INPUT_SET_STOP_OUTPUT + 1;
    private static final int INPUT_SET_TYPE_START_VIDEO_STREAM = INPUT_SET_TYPE_PLANE_ARRAY + 1;
    private static final int INPUT_SET_TYPE_STOP_VIDEO_STREAM = INPUT_SET_TYPE_START_VIDEO_STREAM + 1;   
    private static final int INPUT_SET_TYPE_START_AUDIO_STREAM = INPUT_SET_TYPE_STOP_VIDEO_STREAM + 1;
    private static final int INPUT_SET_TYPE_STOP_AUDIO_STREAM = INPUT_SET_TYPE_START_AUDIO_STREAM + 1;
    private static final int INPUT_SET_TYPE_MJC_BYPASS_WINDOW = INPUT_SET_TYPE_STOP_AUDIO_STREAM + 1; 
    private static final int INPUT_GET_TYPE_OUTPUT_SIGNAL_STATUS = INPUT_SET_TYPE_MJC_BYPASS_WINDOW + 1;
    
    private static final int INPUT_PLANE_NAME_GLPMX_UNKNOWN = 0;
    private static final int INPUT_PLANE_NAME_GLPMX_MAIN = INPUT_PLANE_NAME_GLPMX_UNKNOWN + 1;
    private static final int INPUT_PLANE_NAME_GLPMX_PIP = INPUT_PLANE_NAME_GLPMX_MAIN + 1;
    private static final int INPUT_PLANE_NAME_GLPMX_OSD1 = INPUT_PLANE_NAME_GLPMX_PIP + 1;
    private static final int INPUT_PLANE_NAME_GLPMX_OSD2 = INPUT_PLANE_NAME_GLPMX_OSD1 + 1;
    
    private static final int INPUT_GET_SIGNAL_STATUS_UNKNOWN = 0;
    private static final int INPUT_GET_SIGNAL_STATUS_LOSS = INPUT_GET_SIGNAL_STATUS_UNKNOWN + 1;
    private static final int INPUT_GET_SIGNAL_STATUS_LOCKED = INPUT_GET_SIGNAL_STATUS_LOSS + 1;
    
    private static final int INPUT_SET_PLANE_ORDER_TYPE_UNKNOWN = 0;
    private static final int INPUT_SET_PLANE_ORDER_TYPE_TOP = INPUT_SET_PLANE_ORDER_TYPE_UNKNOWN + 1;
    private static final int INPUT_SET_PLANE_ORDER_TYPE_UP = INPUT_SET_PLANE_ORDER_TYPE_TOP + 1;
    private static final int INPUT_SET_PLANE_ORDER_TYPE_DOWN = INPUT_SET_PLANE_ORDER_TYPE_UP + 1;
    private static final int INPUT_SET_PLANE_ORDER_TYPE_BOTTOM = INPUT_SET_PLANE_ORDER_TYPE_DOWN + 1;
    
    private static final int INPUT_SET_AUTO_ADJUST_TYPE_UNKNOWN = 0;
    private static final int INPUT_SET_AUTO_ADJUST_TYPE_VGA_ADJUST = INPUT_SET_AUTO_ADJUST_TYPE_UNKNOWN + 1;
    private static final int INPUT_SET_AUTO_ADJUST_TYPE_PHASE = INPUT_SET_AUTO_ADJUST_TYPE_VGA_ADJUST + 1;
    private static final int INPUT_SET_AUTO_ADJUST_TYPE_COLOR = INPUT_SET_AUTO_ADJUST_TYPE_PHASE + 1;
    /* EXCHANG: get/set type ------------- end */
    
    private static InputSourceListener staticListener;

    protected static void onOperationDone(int output, boolean isSignalLoss) {
        if (null != InputService.staticListener) {
            String outputName;

            if (OutputDevice.OUTPUT_MAIN == output) {
                outputName = InputService.INPUT_OUTPUT_MAIN;
            } else {
                outputName = InputService.INPUT_OUTPUT_SUB;
            }

            if (null != InputService.staticListener) {
                InputService.staticListener.notifyOutputOperatorDone(outputName);
            }
        }
    }

    protected static void onSourceDetected(int inputId, int signalStatus) {
        if (null != InputService.staticListener) {
            InputSignalStatus inputSignalStatus = InputSignalStatus.SignalStatusLoss;
            
            Logger.i(TAG, " onSourceDetected: " + inputId + " onOutputSignalStatus: " + signalStatus);
            
            switch (signalStatus) {
            case InputService.INPUT_SIGNAL_LOSS:
                inputSignalStatus = InputSignalStatus.SignalStatusLoss;
                break;
            case InputService.INPUT_SIGNAL_LOCKED:
                inputSignalStatus = InputSignalStatus.SignalStatusLocked;
                break;
            default:
                return;
            }
            
            if (null != InputService.staticListener) {
                InputService.staticListener.notifyInputGotSignal(inputId, inputSignalStatus);
            }
        }
    }

    protected static void onOutputSignalStatus(int output, int signalStatus) {
        if (null != InputService.staticListener) {
            String outputName;
            InputSignalStatus inputSignalStatus = InputSignalStatus.SignalStatusUnknown;
            InputEventNotify  inputEventNotify  = InputEventNotify.InputEventUnknown;
            
            Logger.i(TAG, output + "onOutputSignalStatus: " + signalStatus);

            switch (signalStatus) {
            case InputService.INPUT_SIGNAL_LOSS:
                inputSignalStatus = InputSignalStatus.SignalStatusLoss;
                break;
            case InputService.INPUT_SIGNAL_LOCKED:
                inputSignalStatus = InputSignalStatus.SignalStatusLocked;
                break;
            case InputService.INPUT_VIDEO_UPDATE:
            	inputEventNotify = InputEventNotify.InputEventVideoUpdate;
                break;
            default:
                return;
            }

            if (OutputDevice.OUTPUT_MAIN == output) {
                outputName = InputService.INPUT_OUTPUT_MAIN;
            } else {
                outputName = InputService.INPUT_OUTPUT_SUB;
            }
            
            if( InputSignalStatus.SignalStatusUnknown != inputSignalStatus ) {
            	InputService.staticListener.notifyOutputSignalStatus(outputName, inputSignalStatus);
            }
            
            if( InputEventNotify.InputEventUnknown != inputEventNotify ) {
            	InputService.staticListener.notifyOutputEvent(outputName, inputEventNotify);
            }
        }
    }

    /*---------------------------------------private class -------------------------------------*/
    public interface InputServiceListener extends InputListener{
 
    	public enum InputSignalStatus{
            SignalStatusUnknown,
            SignalStatusLoss,
            SignalStatusLocked
        }
     
        public enum InputEventNotify{
            InputEventUnknown,
            InputEventNoSignal,
            InputEventWithSignal,
            InputEventVideoUpdate
        }
     
        public void notifyInputGotSignal(String input );
        
        public void notifyOutputOperatorDone(String output );
        
        public void notifyOutputSignalStatus(String output, InputSignalStatus signalStatus );
        
        public void notifyInputSignalStatus(String input, InputSignalStatus signalStatus );
        
        //public void notifyOutputEvent(String output, InputEventNotify eventNotify );
    }
    
    // this class is used to handle notification
    private class InputSourceListener {
        private InputService parentInputService;
        private InputListener agentInputListener;

        protected InputSourceListener(InputService parentInputService) {
            this.parentInputService = parentInputService;
            this.agentInputListener = null;
        }

        protected int setAppInputListener(InputListener appInputListener) {
            if (null != appInputListener) {
                this.agentInputListener = appInputListener;
                return InputService.INPUT_RET_OK;
            }

            return InputService.INPUT_RET_FAIL;
        }

        public void notifyInputGotSignal(int inputId, InputSignalStatus inputListenerSignalStatus) {
            String inputString = this.parentInputService.getInputStringById(inputId);

            Logger.i(TAG, "InputService notifyInputGotSignal: " + inputString);

            if (null != this.agentInputListener && null != inputString) {
                if (agentInputListener instanceof InputServiceListener) {
                    ((InputServiceListener) this.agentInputListener).notifyInputSignalStatus(inputString, inputListenerSignalStatus);
                }
                else{
                    if (InputSignalStatus.SignalStatusLocked == inputListenerSignalStatus){
                        this.agentInputListener.notifyInputGotSignal(inputString);
                    }
                }
            }
        }

        public void notifyOutputOperatorDone(String output) {
            Logger.i(TAG, "InputService notifyOutputOperatorDone: " + output);

            if (null != this.agentInputListener) {
                if (agentInputListener instanceof InputServiceListener) {
                    ((InputServiceListener) this.agentInputListener).notifyOutputOperatorDone(output);
                } else {
                    this.agentInputListener.notifyOutputOperatorDone(output);
                }
            }
        }

        public void notifyOutputSignalStatus(String output, InputSignalStatus signalStatus) {
            OutputDevice outputDevice;
            Logger.i(TAG, output + " notifyOutputSignalStatus: " + signalStatus);

            outputDevice = parentInputService.findOutput(output);
            if (null != outputDevice) {
                if (null != this.agentInputListener) {
                    if (agentInputListener instanceof InputServiceListener) {
                        ((InputServiceListener) this.agentInputListener).notifyOutputSignalStatus(output, signalStatus);
                    } else {
                        if (InputSignalStatus.SignalStatusLoss == signalStatus){
                            this.agentInputListener.notifyOutputSignalSatus(output, InputListenerSignalSatus.SignalSatusLoss);
                        }
                        else{
                            this.agentInputListener.notifyOutputSignalSatus(output, InputListenerSignalSatus.SignalSatusLocked);
                        }
                    }
                }
            }
        }
        
        public void notifyOutputEvent(String output, InputEventNotify eventNotify) {
            OutputDevice outputDevice;
            Logger.i(TAG, output + " notifyOutputEvent: " + eventNotify);

            outputDevice = parentInputService.findOutput(output);
            if (null != outputDevice) {
                if (null != this.agentInputListener) {
                    if (InputEventNotify.InputEventVideoUpdate == eventNotify){
                        this.agentInputListener.notifyOutputEvent(output, InputListenerEventNotify.InputEventVideoUpdate);
                    }
                }
            }
        }
    }

    // this class is used to store input source record
    private class InputSourceRecord {
        private int internalIdx; /*
                                  * come from t_isl_rec, the index among same
                                  * type
                                  */
        private int id; /*
                         * come from t_isl_rec, the only id in all of input
                         * source
                         */
        private String recordName;

        public InputSourceRecord(int internalIdx, int id, String recordName) {
            super();
            this.internalIdx = internalIdx;
            this.id = id;
            this.recordName = recordName;
        }

        public int getInternalIdx() {
            return internalIdx;
        }

        public int getId() {
            return id;
        }

        public String getRecordName() {
            return recordName;
        }
        
        public void clone(InputSourceRecord another){
            another.id = this.id;
            another.internalIdx = this.internalIdx;
            another.recordName = new String(this.recordName);
        }
    }

    // this class is used to store input source type information
    private class InputSourceType {
        private static final int INPS_TYPE_NUMERICAL_TV = 0; // numerical
        private static final int INPS_TYPE_NUMERICAL_AV = INPS_TYPE_NUMERICAL_TV + 1;
        private static final int INPS_TYPE_NUMERICAL_VGA = INPS_TYPE_NUMERICAL_AV + 1;
        private static final int INPS_TYPE_NUMERICAL_SVIDEO = INPS_TYPE_NUMERICAL_VGA + 1;
        private static final int INPS_TYPE_NUMERICAL_COMPONENT = INPS_TYPE_NUMERICAL_SVIDEO + 1;
        private static final int INPS_TYPE_NUMERICAL_COMPOSITE = INPS_TYPE_NUMERICAL_COMPONENT + 1;
        private static final int INPS_TYPE_NUMERICAL_HDMI = INPS_TYPE_NUMERICAL_COMPOSITE + 1;
        private static final int INPS_TYPE_NUMERICAL_RESERVED = INPS_TYPE_NUMERICAL_HDMI + 1;
        private static final int INPS_TYPE_NUMERICAL_MAX_NUM = INPS_TYPE_NUMERICAL_RESERVED + 1; /*
                                                                                                  * this
                                                                                                  * definition
                                                                                                  * should
                                                                                                  * be
                                                                                                  * the
                                                                                                  * last
                                                                                                  * one
                                                                                                  * of
                                                                                                  * INPS_
                                                                                                  */

        private int inputType; /*
                                * the value is from INPS_TYPE_TV to
                                * INPS_TYPE_HDMI
                                */
        private String name;
        private int gourp[]; /* come from t_isl_rec */
        private int gourpSize;
        private InputSourceRecord inputSourceRecords[];

        public InputSourceType() {
            super();
            this.gourp = null;
            this.gourpSize = 0;
            this.inputSourceRecords = null;
        }

        public InputSourceType(int inputType) {
            super();
            this.inputType = inputType;
            this.gourp = null;
            this.gourpSize = 0;
            this.inputSourceRecords = new InputSourceRecord[0];

            this.name = new String(this.conventTypeToString(inputType));
        }

        public void setGourp(int[] gourp, int gourpSize) {
            if (null == this.gourp) {
                this.gourpSize = gourpSize;
                this.gourp = new int[this.gourpSize];

                for (int i = 0; i < this.gourpSize; i++) {
                    this.gourp[i] = gourp[i];
                }
            }
        }

        public void addRecord(int internalIdx, int id) {
            int i;
            InputSourceRecord newRecords[] = new InputSourceRecord[this.inputSourceRecords.length + 1];

            for (i = 0; i < this.inputSourceRecords.length; i++) {
                newRecords[i] = this.inputSourceRecords[i];
            }
            newRecords[i] = new InputSourceRecord(internalIdx, id, new String(this.name + internalIdx));

            this.inputSourceRecords = newRecords;
        }
        
        public int getInputType(){
            return this.inputType;
        }

        public boolean isSameType(int inputType) {
            return (inputType == this.inputType);
        }

        public boolean isSameType(String inputType) {
            return this.name.equals(inputType);
        }

        public boolean isSameGroup(InputSourceType otherType) {
            for (int i = 0; i < this.gourpSize && i < otherType.gourpSize; i++) {
                if (this.gourp[i] > 0 && otherType.gourp[i] > 0) {
                    return true;
                }
            }

            return false;
        }

        public InputSourceRecord getInputSourceRecord(String inputSourceString) {
            if (inputSourceString.startsWith(this.name)) {
                for (int i = 0; i < this.inputSourceRecords.length; i++) {
                    if (inputSourceString.equals(this.inputSourceRecords[i].getRecordName())) {
                        return this.inputSourceRecords[i];
                    }
                }
            }

            return null;
        }

        public InputSourceRecord getInputSourceRecord(int inputId) {
            for (int i = 0; i < this.inputSourceRecords.length; i++) {
                if (inputId == this.inputSourceRecords[i].getId()) {
                    return this.inputSourceRecords[i];
                }
            }

            return null;
        }

        public String getTypeName() {
            return this.name;
        }

        public int getRecordNum() {
            return this.inputSourceRecords.length;
        }

        public String[] getAllRecordName() {
            int RecordNum = this.getRecordNum();
            String allRecordString[] = new String[RecordNum];

            for (int i = 0; i < RecordNum; i++) {
                allRecordString[i] = new String(this.inputSourceRecords[i].getRecordName());
            }

            return allRecordString;
        }

        private String conventTypeToString(int inputSourceType) {
            String inputSourceTypeString = null;

            switch (inputSourceType) {
            case InputSourceType.INPS_TYPE_NUMERICAL_TV:
                inputSourceTypeString = InputService.INPUT_TYPE_TV;
                break;
            case InputSourceType.INPS_TYPE_NUMERICAL_AV:
                inputSourceTypeString = InputService.INPUT_TYPE_AV;
                break;
            case InputSourceType.INPS_TYPE_NUMERICAL_VGA:
                inputSourceTypeString = InputService.INPUT_TYPE_VGA;
                break;
            case InputSourceType.INPS_TYPE_NUMERICAL_SVIDEO:
                inputSourceTypeString = InputService.INPUT_TYPE_SVIDEO;
                break;
            case InputSourceType.INPS_TYPE_NUMERICAL_COMPONENT:
                inputSourceTypeString = InputService.INPUT_TYPE_COMPONENT;
                break;
            case InputSourceType.INPS_TYPE_NUMERICAL_COMPOSITE:
                inputSourceTypeString = InputService.INPUT_TYPE_COMPOSITE;
                break;
            case InputSourceType.INPS_TYPE_NUMERICAL_HDMI:
                inputSourceTypeString = InputService.INPUT_TYPE_HDMI;
                break;
            case InputSourceType.INPS_TYPE_NUMERICAL_RESERVED:
                inputSourceTypeString = InputService.INPUT_TYPE_RESERVED;
                break;
            default:
                break;
            }

            return inputSourceTypeString;
        }
    }

    // private class Rect{
    // int left;
    // int right;
    // int top;
    // int bottom;
    // }

    // this class is used to store output device
    private class OutputDevice {
        private static final int OUTPUT_UNKNOWN = -1;
        private static final int OUTPUT_MAIN = 0;
        private static final int OUTPUT_SUB = 1;
        private static final int OUTPUT_MAX_NUM = 2;/*
                                                     * this definition should be
                                                     * the last one of OUTPUT_
                                                     */

        private int output;
        private String name;
        private InputSourceRecord lastInput;
        private Rect rect;

        public OutputDevice(int output) {
            super();
            this.output = output;
            this.name = null;
            this.lastInput = null;
            this.rect = new Rect();

            if (OutputDevice.OUTPUT_MAIN == this.output) {
                this.name = InputService.INPUT_OUTPUT_MAIN;
            }

            if (OutputDevice.OUTPUT_SUB == this.output) {
                this.name = InputService.INPUT_OUTPUT_SUB;
            }
        }

        public int getOutput() {
            return output;
        }

        public String getName() {
            return name;
        }

        public InputSourceRecord getLastInput() {
            return lastInput;
        }

        public Rect getRect() {
            return rect;
        }

        public void setLastInput(InputSourceRecord lastInput) {
            this.lastInput = lastInput;
        }
        
        public void exchangeLastInput( OutputDevice anotherOutput ){
            InputSourceRecord tmpLastInput = this.lastInput;
            this.lastInput = anotherOutput.lastInput;
            anotherOutput.lastInput = tmpLastInput;
        }

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public boolean isSameOutput(int output) {
            return (this.output == output);
        }

        public boolean isSameOutput(String outputString) {
            return this.name.equals(outputString);
        }
    }

    private class InterStateMechine{
        private static final int OPERATION_UNKNOWN = 100;
        private static final int OPERATION_ENTER_POP = OPERATION_UNKNOWN + 1;
        private static final int OPERATION_ENTER_PIP = OPERATION_ENTER_POP + 1;
        private static final int OPERATION_ENTER_NORMAL = OPERATION_ENTER_PIP + 1;
        private static final int OPERATION_BIND_OUT_FOR_IN = OPERATION_ENTER_NORMAL + 1;
        private static final int OPERATION_SWAP = OPERATION_BIND_OUT_FOR_IN + 1;
        private static final int OPERATION_CHANGE_FOCUS = OPERATION_SWAP + 1;
        
        private InputService ownerService;
        private InputService.InputState currentState;
        
        public InterStateMechine( InputService ownerService ){
            this.ownerService = ownerService;
            this.currentState = InputService.InputState.INPUT_STATE_NORMAL;
        }
        
        private boolean gotoState( InputService.InputState dstSate ){
            if ((InputService.InputState.INPUT_STATE_PIP != dstSate) && (InputService.InputState.INPUT_STATE_POP != dstSate) && (InputService.InputState.INPUT_STATE_NORMAL != dstSate)){
                return false;
            }
            
            if (dstSate != this.currentState){
                this.currentState = dstSate;                
                return true;
            }
            
            return false;
        }
        
        public InputService.InputState getCurrentState(){
            return this.currentState;
        }
        
        public boolean doOperation( int operatorName, int p1, int p2){
            switch(operatorName){
            case OPERATION_ENTER_POP:
                Logger.i(TAG, "statemechine check OPERATION_ENTER_POP");
                return this.gotoState(InputService.InputState.INPUT_STATE_POP);
                
            case OPERATION_ENTER_PIP:
                Logger.i(TAG, "statemechine check OPERATION_ENTER_PIP");
                return this.gotoState(InputService.InputState.INPUT_STATE_PIP);
                
            case OPERATION_ENTER_NORMAL:
                Logger.i(TAG, "statemechine check OPERATION_ENTER_NORMAL");
                return this.gotoState(InputService.InputState.INPUT_STATE_NORMAL);
                
            case OPERATION_BIND_OUT_FOR_IN:
                int output = p1;
                int input = p2;
                
                Logger.i(TAG, "statemechine check OPERATION_BIND_OUT_FOR_IN " + "current_state==" + this.currentState);
                /* when both MAIN and Sub exist, input service will do something check */
                if (InputService.InputState.INPUT_STATE_NORMAL != this.currentState ){
                    OutputDevice currentOutput = this.ownerService.findOutput(output);
                    InputSourceRecord currentInput = currentOutput.getLastInput();
                    OutputDevice anotherOutput = null;
                    InputSourceRecord anotherInput = null;
                    
                    if (currentOutput.getName().equals(INPUT_OUTPUT_MAIN)){
                        anotherOutput = this.ownerService.findOutput(INPUT_OUTPUT_SUB);
                    }
                    else {
                        if (currentOutput.getName().equals(INPUT_OUTPUT_SUB)){
                            anotherOutput = this.ownerService.findOutput(INPUT_OUTPUT_MAIN);
                        }
                        else{
                            return false;
                        }
                    }
                    
                    if (null == currentOutput || null == anotherOutput){
                        return false;
                    }
                    
                    anotherInput = anotherOutput.getLastInput();
                    if ((null != anotherInput) && (null != currentInput)){
                        String anotherInputType = this.ownerService.getTypeFromInputString(anotherInput.getRecordName());
                        String currentInputType = this.ownerService.getTypeFromInputString(currentInput.getRecordName());
                        String[] currentInputConflicts = this.ownerService.getConflictType(currentInputType);
                        
                        for (int i = 0; i < currentInputConflicts.length; i++){
                            if (currentInputConflicts[i].equals(anotherInputType)){
                                return false;
                            }
                        }
                    }
                }
                
                if (InputService.InputState.INPUT_STATE_NORMAL == this.currentState ){
                	if (this.ownerService.findOutput(INPUT_OUTPUT_SUB).getOutput() == output){
                		return false;
                	}
                }
                return true;
                
            case OPERATION_SWAP:
                Logger.i(TAG, "statemechine check OPERATION_SWAP");
                if (InputService.InputState.INPUT_STATE_NORMAL == this.currentState){
                    return false;
                }
                
                return true;
                
            case OPERATION_CHANGE_FOCUS:
                Logger.i(TAG, "statemechine check OPERATION_CHANGE_FOCUS");
                if (InputService.InputState.INPUT_STATE_PIP != this.currentState &&
                    InputService.InputState.INPUT_STATE_POP != this.currentState){
                    return false;
                }
                
                return true;
                
            case OPERATION_UNKNOWN:
            default:
                Logger.i(TAG, "statemechine check OPERATION_UNKNOWN");
                break;
            }
            
            return false;
        }
        
        public boolean doOperation( int operatorName ){
            return doOperation(operatorName, 0, 0);
        }
    }

    private class PipPopFocus{
        private OutputDevice focusedOutput;
        private BroadcastService broadcastService;
        
        public PipPopFocus( OutputDevice initFocusOutput ){
            System.out.println("Enter PipPopFocus()!");
            this.focusedOutput = initFocusOutput;
        }
        
        public void setBroadcastService(BroadcastService broadcastService){
        	this.broadcastService = broadcastService;
        }
        
        public OutputDevice getFocusedOutput(){
            return this.focusedOutput;
        }
        
        public void changeFocus( OutputDevice newFocusOutput){
            this.focusedOutput = newFocusOutput;
            
            System.out.println("Enter changeFocus()!");
            /*
            if (OutputDevice.OUTPUT_MAIN == newFocusOutput.getOutput()){
            	System.out.println("Ready updateFocusWindow(main)!");
            	try {
			this.broadcastService.updateFocusWindow(BroadcastService.SVCTX_FOCUS_WIN_MAIN);
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        }
            }
            else if (OutputDevice.OUTPUT_SUB == newFocusOutput.getOutput()){
            	System.out.println("Ready updateFocusWindow(sub)!");
        		try {
				this.broadcastService.updateFocusWindow(BroadcastService.SVCTX_FOCUS_WIN_SUB);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
            */
        }
    }

    /*---------------------------------------private variable -------------------------------------*/
    private InputSourceListener inputSourceListener;

    private int inputSourceTypesNum; // input source type: HDMI, VGA ...
    private InputSourceType inputSourceTypes[];

    private int outputDevicesNum; // output device: main sub
    private OutputDevice outputDevices[];

    private PipPopFocus pipPopFocus;

    private InterStateMechine stateMechine;
    
    private BroadcastService broadcastService;
    private CIService ciService;

    /*---------------------------------------protected/private function -------------------------------------*/
    protected InputService() {
        int i = 0;
        int currentOutputDevicesNum = OutputDevice.OUTPUT_MAX_NUM; 
        int ret = 0;

        Logger.i(TAG, "InputService instance creator BEGIN");

        this.inputSourceListener = new InputSourceListener(this);
        InputService.staticListener = this.inputSourceListener;

        this.inputSourceTypesNum = InputSourceType.INPS_TYPE_NUMERICAL_MAX_NUM;
        this.inputSourceTypes = new InputSourceType[this.inputSourceTypesNum];
        {
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_TV] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_TV);
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_AV] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_AV);
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_VGA] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_VGA);
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_SVIDEO] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_SVIDEO);
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_COMPONENT] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_COMPONENT);
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_COMPOSITE] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_COMPOSITE);
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_HDMI] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_HDMI);
            this.inputSourceTypes[InputSourceType.INPS_TYPE_NUMERICAL_RESERVED] = new InputSourceType(
                    InputSourceType.INPS_TYPE_NUMERICAL_RESERVED);
        }

        this.outputDevicesNum = currentOutputDevicesNum;
        this.outputDevices = new OutputDevice[this.outputDevicesNum];
        {
            i = 0;
            this.outputDevices[i++] = new OutputDevice(OutputDevice.OUTPUT_MAIN);
            this.outputDevices[i++] = new OutputDevice(OutputDevice.OUTPUT_SUB);
        }

        this.pipPopFocus = new PipPopFocus(this.findOutput(OutputDevice.OUTPUT_MAIN));

        this.stateMechine = new InterStateMechine(this);

        Logger.i(TAG, "InputService instance creator END");
    }

    private InputSourceType getInputSourceType(int inputSourceType) {
        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            if (this.inputSourceTypes[i].isSameType(inputSourceType)) {
                return this.inputSourceTypes[i];
            }
        }

        return new InputSourceType();
    }

    private InputSourceType getInputSourceType(String inputSourceType) {
        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            if (this.inputSourceTypes[i].isSameType(inputSourceType)) {
                return this.inputSourceTypes[i];
            }
        }

        return new InputSourceType();
    }

    private int addInputSourceRecord(int inputType, int gourp[], int gourpSize, int id, int internalIdx) {
        InputSourceType inputSourceType = this.getInputSourceType(inputType);

        String groupLog = new String();
        for (int i = 0; i < gourpSize; i++) {
            groupLog = groupLog + gourp[i];
        }

        Logger.i(TAG, inputSourceType.getTypeName() + " id: " + id + " internalIdx: " + internalIdx + " gourp:"
                + groupLog);

        inputSourceType.addRecord(internalIdx, id);
        inputSourceType.setGourp(gourp, gourpSize);

        return InputService.INPUT_RET_OK;
    }

    protected int getInputIdByString(String inputSourceString) throws TVMException {
        int inputId = 0;
        InputSourceRecord inputSourceRecord = null;

        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            inputSourceRecord = this.inputSourceTypes[i].getInputSourceRecord(inputSourceString);
            if (null == inputSourceRecord) {
                continue;
            }

            inputId = inputSourceRecord.getId();
            break;
        }

        if (null == inputSourceRecord) {
            throw new TVMException(InputService.INPUT_RET_FAIL, "the input parameter is wrong.");
        }

        return inputId;
    }

    protected String getInputStringById(int inputId) {
        String inputString = null;
        InputSourceRecord inputSourceRecord = null;

        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            inputSourceRecord = this.inputSourceTypes[i].getInputSourceRecord(inputId);
            if (null == inputSourceRecord) {
                continue;
            }

            inputString = inputSourceRecord.getRecordName();
            break;
        }

        return inputString;
    }

    protected OutputDevice findOutput(int output) {
        for (int i = 0; i < this.outputDevicesNum; i++) {
            if (this.outputDevices[i].isSameOutput(output)) {
                return this.outputDevices[i];
            }
        }

        return null;
    }

    protected OutputDevice findOutput(String output) {
        for (int i = 0; i < this.outputDevicesNum; i++) {
            if (this.outputDevices[i].isSameOutput(output)) {
                return this.outputDevices[i];
            }
        }

        return null;
    }

    /*---------------------------------------public function -------------------------------------*/

    /**
     * Register a listener to input service. Application will be notified when
     * some input source got signal or action of bind(), swap() operator done
     * 
     * <pre>
     * ------------------------simple code for InputService------------------------
     * public class AppInputListener implements InputListener {
     *     public void notifyInputGotSignal(String input) {}
     *     public void notifyOutputOperatorDone(String output) {}
     * }
     * 
     * AppInputListener appInputListener = new AppInputListener();
     * inputService.setInputListener(appInputListener);
     * </pre>
     * 
     * @param appInputListener
     *            notification function table. It is just an interface.
     *            Application should implement the actual class.
     * @return int 
     * 			  Success or fail.
     */
    public int setInputListener(InputListener appInputListener) {
        int ret = InputService.INPUT_RET_OK;

        ret = this.inputSourceListener.setAppInputListener(appInputListener);
        if (ret < InputService.INPUT_RET_OK) {
            ret = InputService.INPUT_RET_FAIL;
            Logger.e(TAG, "appInputListener should not be null.");
        }

        return ret;
    }
    
    public int setInputListener(InputServiceListener appInputListener) {
        int ret = InputService.INPUT_RET_OK;

        ret = this.inputSourceListener.setAppInputListener(appInputListener);
        if (ret < InputService.INPUT_RET_OK) {
            ret = InputService.INPUT_RET_FAIL;
            Logger.e(TAG, "appInputListener should not be null.");
        }

        return ret;
    }

    /**
     * Get all active input number of special type
     * 
     * @param inputType
     *            The special type. 
     *            All input type have been defined in input service static element, 
     *            such as InputService.INPUT_TYPE_TV .
     * @return int 
     * 			  The input number of designated input type.
     */
    public int getDesignatedTypeInputsNum(String inputType) {
        InputSourceType inputSourceType = this.getInputSourceType(inputType);
        return inputSourceType.getRecordNum();
    }

    /**
     * Get all active input strings of special type. Application can all inputs
     * by getDesignatedTypeInputsNum and this API
     * 
     * <pre>
     * ------------------------simple code ------------------------
     * int hdmiNum = inputService.getDesignatedTypeInputsNum( InputService.INPUT_TYPE_HDMI );
     * String hdmis[] = inputService.getDesignatedTypeInputsString(InputService.INPUT_TYPE_HDMI);
     * 
     * int hdmiIdx = 2;
     * if (0 == hdmiNum) { 
     *     return; 
     * }
     * else {
     *     if (hdmiIdx >= hdmiNum) {
     *         hdmiIdx = hdmiNum - 1;
     *     }
     * }
     * </pre>
     * 
     * @param inputType
     *            The special type. 
     *            All input type have been defined in input service static element,
     *            such as InputService.INPUT_TYPE_TV .
     * @return String[] 
     * 		   	  The all input string of designated input type.
     */
    public String[] getDesignatedTypeInputsString(String inputType) {
        InputSourceType inputSourceType = this.getInputSourceType(inputType);
        return inputSourceType.getAllRecordName();
    }

    /**
     * Get conflict input number of special type.
     * 
     * @param inputType
     *            The special type. 
     *            All input type have been defined in input service static element,
     *            such as InputService.INPUT_TYPE_TV .
     * @return int
     * 			  The input number conflict with designated input type.
     */
    public int getConflictTypeNum(String inputType) {
        InputSourceType inputSourceType = this.getInputSourceType(inputType);
        int conflictTypeNum = 0;

        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            if (this.inputSourceTypes[i].isSameGroup(inputSourceType)) {
                conflictTypeNum++;
            }
        }

        return conflictTypeNum;
    }

    /**
     * Get conflict input strings of special type. If two input need same
     * resource and the resource is only one in the system, system can not play
     * both of them. Then one is Conflict to another.
     * 
     * @param inputType
     *            The special type. 
     *            All input type have been defined in input service static element,
     *            such as InputService.INPUT_TYPE_TV .
     *            
     * @return String[] 
     * 			  The all input strings conflict with designated input type.
     */
    public String[] getConflictType(String inputType) {
        InputSourceType inputSourceType = null;
        int i;
        int conflictTypeNum = 0;
        String conflictTypes[] = new String[this.getConflictTypeNum(inputType)];

        for (i = 0; i < this.inputSourceTypesNum; i++) {
            if (this.inputSourceTypes[i].isSameType(inputType)) {
                inputSourceType = this.inputSourceTypes[i];
                break;
            }
        }

        if (null != inputSourceType) {
            conflictTypeNum = 0;
            for (i = 0; i < this.inputSourceTypesNum; i++) {
                if (this.inputSourceTypes[i].isSameGroup(inputSourceType)) {
                    conflictTypes[conflictTypeNum] = new String(this.inputSourceTypes[i].getTypeName());
                    conflictTypeNum++;
                }
            }
        }

        return conflictTypes;
    }

    /**
     * Get input type by input string. It is useful when Application want to
     * other inputs with same type The input string should be return of
     * getDesignatedTypeInputsString or getConflictType.
     * 
     * @param inputString
     *            The special type. 
     *            All input type have been defined in input service static element.
     * @return String 
     * 			  the input type string.
     */
    public String getTypeFromInputString(String inputSourceString) {
        InputSourceType inputSourceType = null;

        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            if (null == this.inputSourceTypes[i].getInputSourceRecord(inputSourceString)) {
                continue;
            }
            
            inputSourceType = this.inputSourceTypes[i];

            break;
        }

        if (null == inputSourceType) {
            return null;
        }

        return inputSourceType.getTypeName();
    }

    /**
     * Get screen outputs number. Usually, only "main" and "sub" are screen
     * outputs.
     * 
     * @return int
     * 			  The number of screen outputs.
     */
    public int getScreenOutputsNum() {
        return this.outputDevicesNum;
    }

    /**
     * Get screen outputs string. Usually, there are only "main" and "sub".
     * 
     * @return String[] 
     * 			  The all screen outputs string.
     */
    public String[] getScreenOutputs() {
        String screenOutputs[] = new String[this.outputDevicesNum];

        for (int i = 0; i < this.outputDevicesNum; i++) {
            screenOutputs[i] = new String(this.outputDevices[i].getName());
        }

        return screenOutputs;
    }

    /**
     * Get current input on the screen output.
     * 
     * @param output
     *            screen output string.
     * @return String 
     * 			  The last input string of designated screen output.
     */
    public String getLastInputOfOutput(String output) {
        InputSourceRecord   inputSourceRecord = null;
        
        for (int i = 0; i < this.outputDevicesNum; i++) {
            if (this.outputDevices[i].isSameOutput(output)) {
                inputSourceRecord = this.outputDevices[i].getLastInput();
                
                if (null == inputSourceRecord){
                    return null;
                }
                
                return new String(inputSourceRecord.getRecordName());
            }
        }

        return null;
    }

    /**
     * Bind designated input to designated screen output.
     * 
     * <pre>
     * ------------------------simple code ------------------------
     * int outputNum = inputService.getScreenOutputsNum();                                 
     * String outputs[] = inputService.getScreenOutputs();                                 
     *                                                                                     
     * inputService.setScreenOutputRect(outputs[0], rect0);                                
     * inputService.setScreenOutputRect(outputs[1], rect1);                                
     *                                                                                     
     * inputService.bind(outputs[0], hdmis[hdmiIdx]);                                      
     *                                                                                     
     * int hdmiConflictNum = inputService.getConflictTypeNum(InputService.INPUT_TYPE_HDMI);
     * String hdmiConflict[] = inputService.getConflictType(InputService.INPUT_TYPE_HDMI); 
     * 
     *  boolean notConflict = true;
     *  for (int i = 0; i < hdmiConflictNum; i++)
     *  {
     *      if (InputService.INPUT_TYPE_VGA.equals(hdmiConflict[i]))
     *      {
     *          notConflict = false;
     *      }
     *  }                                                                               
     *                                                                                     
     * if (notConflict)                                                                    
     * {                                                                                   
     *     inputService.bind(outputs[1], vgas[vgaIdx]);                                    
     *     inputService.swap(outputs[0], outputs[1]);                                      
     * }
     * </pre>
     * 
     * @param output
     *            screen output string.
     * @param input
     *            input string.
     * @return int
     * 			  Success or fail.
     */
    public int bind(String output, String input) {
        OutputDevice outputDevice = this.findOutput(output);
        //InputSourceType inputSourceType = this.getInputSourceType(input);
        InputSourceRecord inputSourceRecord = null;
        InputSourceType inputSourceType = null;

        /* find input */
        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            inputSourceRecord = this.inputSourceTypes[i].getInputSourceRecord(input);
            if (null == inputSourceRecord) {
                continue;
            }
            
            inputSourceType = this.inputSourceTypes[i];
            
            break;
        }

        if (null == inputSourceRecord || null == outputDevice) {
            return InputService.INPUT_RET_FAIL;
        }
        
        Logger.i(TAG, "bind" + " output:" + outputDevice.getOutput() + "  input:" + inputSourceRecord.getId());
        
        if (!this.stateMechine.doOperation(
                InterStateMechine.OPERATION_BIND_OUT_FOR_IN,
                outputDevice.getOutput(), inputSourceRecord.getId())) {
            Logger.i(TAG, "statemechine check bind FAIL");
            return InputService.INPUT_RET_FAIL;
        }

		if (this.stateMechine.getCurrentState() == InputService.InputState.INPUT_STATE_PIP ||
		   this.stateMechine.getCurrentState() == InputService.InputState.INPUT_STATE_POP){
		   	Logger.i(TAG, "pip/pop input type" + inputSourceType.getInputType());
		     if (InputSourceType.INPS_TYPE_NUMERICAL_TV != inputSourceType.getInputType()){
			 	Logger.i(TAG, "pip/pop output type" + outputDevice.getOutput());
				if (outputDevice.getOutput() == OutputDevice.OUTPUT_MAIN){
					Logger.i(TAG, "pip/pop getLastInputOfOutput sub" + this.getLastInputOfOutput(InputService.INPUT_OUTPUT_SUB));
					if(this.getLastInputOfOutput(InputService.INPUT_OUTPUT_SUB)!= null){	
						if (this.getLastInputOfOutput(InputService.INPUT_OUTPUT_SUB).contains(InputService.INPUT_TYPE_TV)){
							CIPath ciPath = this.ciService.getCITSPath();
	            			ciPath.switchPath(true);   /* Only when DTV, it is true */
						}else {
							CIPath ciPath = this.ciService.getCITSPath();
	            			ciPath.switchPath(false);   /* Only when DTV, it is true */
						}
					}
				}else {
					Logger.i(TAG, "pip/pop getLastInputOfOutput main" + this.getLastInputOfOutput(InputService.INPUT_OUTPUT_MAIN));
					if(this.getLastInputOfOutput(InputService.INPUT_OUTPUT_MAIN)!= null){
						if (this.getLastInputOfOutput(InputService.INPUT_OUTPUT_MAIN).contains(InputService.INPUT_TYPE_TV)){
							CIPath ciPath = this.ciService.getCITSPath();
	            			ciPath.switchPath(true);   /* Only when DTV, it is true */
						}else {
							CIPath ciPath = this.ciService.getCITSPath();
	            			ciPath.switchPath(false);   /* Only when DTV, it is true */
						}
					}
				}
			 }else {
			 	CIPath ciPath = this.ciService.getCITSPath();
	            ciPath.switchPath(true);   /* Only when DTV, it is true */
			 }
		}else{
	        /* switch CI path, when it is input source: BEGIN */
	        if (InputSourceType.INPS_TYPE_NUMERICAL_TV != inputSourceType.getInputType()){
	            CIPath ciPath = this.ciService.getCITSPath();
	            ciPath.switchPath(false);   /* Only when DTV, it is true */
	            /* when the source is TV, it will be handled by broadcast service */
	        }else {
	        	CIPath ciPath = this.ciService.getCITSPath();
	            ciPath.switchPath(true);   /* Only when DTV, it is true */
	        }
        }
        /* switch CI path, when it is input source: END */

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.inputServiceBind_proxy(outputDevice.getOutput(), inputSourceRecord.getId());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        if( InputSourceType.INPS_TYPE_NUMERICAL_TV == inputSourceType.inputType )
        {
            try {
    			this.broadcastService.updateTVWindowRegion(outputDevice.getOutput());
    		} catch (TVMException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        
        }
        
        outputDevice.setLastInput(inputSourceRecord);

        return InputService.INPUT_RET_OK;
    }

    /**
     * Exchange screen output1 and screen output2
     * 
     * @param output1
     *            screen output1 string.
     * @param output2
     *            screen output1 string.
     * @return int
     * 			  Success or fail.
     */
    public int swap(String output1, String output2) {        
        OutputDevice output1Device = this.findOutput(output1);
        OutputDevice output2Device = this.findOutput(output2);
        
        if (null == output1Device || null == output2Device){
            return InputService.INPUT_RET_FAIL;
        }
        
        if (!this.stateMechine.doOperation(InterStateMechine.OPERATION_SWAP)) {
            return InputService.INPUT_RET_FAIL;
        }
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            service.inputServiceSwap_proxy(output1Device.getOutput(), output2Device.getOutput());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        output1Device.exchangeLastInput(output2Device);
        
        return InputService.INPUT_RET_OK;
    }

    /**
     * Set display region on designated screen output.
     * 
     * @param output
     *            screen output string.
     * @param r
     *            The size and position of display region.
     * @return int
     * 			  Success or fail.
     */
    public int setScreenOutputRect(String output, Rect r) {
        OutputDevice outputDevice = this.findOutput(output);
        InputSourceType inputSourceType = null;
        
        if (null == outputDevice) {
            return InputService.INPUT_RET_FAIL;
        }
        
        if (null != outputDevice.lastInput)
        {
        	inputSourceType = this.getInputSourceType(outputDevice.lastInput.recordName);
        }

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.setScreenOutputRect_proxy(outputDevice.getOutput(), r.left, r.right, r.top, r.bottom);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if( null != inputSourceType && InputSourceType.INPS_TYPE_NUMERICAL_TV == inputSourceType.inputType )
        {
            try {
    			this.broadcastService.updateTVWindowRegion(outputDevice.getOutput());
    		} catch (TVMException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        
        }

       return InputService.INPUT_RET_OK;
    }

    /**
     * Set video region on designated screen output.
     * 
     * @param output
     *            screen output string.
     * @param r
     *            The size and position of video region.
     * @return int
     * 			  Success or fail.
     */
    public int setScreenOutputVideoRect(String output, Rect r) {
        OutputDevice outputDevice = this.findOutput(output);

        if (null == outputDevice) {
            return InputService.INPUT_RET_FAIL;
        }

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.setScreenOutputVideoRect_proxy(outputDevice.getOutput(), r.left, r.right, r.top, r.bottom);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return InputService.INPUT_RET_OK;
    }

    /**
     * Get display region on designated screen output.
     * 
     * @param output
     *            screen output string.
     * @return Rect
     *            The size and position of display region.
     */
    public Rect getScreenOutputRect(String output) {
        OutputDevice outputDevice = this.findOutput(output);
        Rect resultRect = new Rect();
        InputRegion inputRegion = new InputRegion();

        if (null == outputDevice) {
            return null;
        }

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.getScreenOutputRect_proxy(outputDevice.getOutput(), inputRegion);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        resultRect.left = inputRegion.getLeft();
        resultRect.right = inputRegion.getRight();
        resultRect.top = inputRegion.getTop();
        resultRect.bottom = inputRegion.getBottom();

        return resultRect;
    }

    /**
     * Get video region on designated screen output.
     * 
     * @param output
     *            screen output string.
     * @return Rect
     *            The size and position of video region.
     */
    public Rect getScreenOutputVideoRect(String output) {
        OutputDevice outputDevice = this.findOutput(output);
        Rect resultRect = new Rect();
        InputRegion inputRegion = new InputRegion();

        if (null == outputDevice) {
            return null;
        }

        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                service.getScreenOutputVideoRect_proxy(outputDevice.getOutput(), inputRegion);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        resultRect.left = inputRegion.getLeft();
        resultRect.right = inputRegion.getRight();
        resultRect.top = inputRegion.getTop();
        resultRect.bottom = inputRegion.getBottom();

        return resultRect;
    }
    
    /**
     * for increase model class number, inputSourceData will use inputSourceData to
     * transfer data.
	 * And the inputSourceData meaning will be explained by caller and JNI 
	 */
    private int exchangeInputSourceData(int output, int exchangeType, int[] inputSourceData) {
        int ret = InputService.INPUT_RET_FAIL;
        int totalLen;
        int[] exchangeData;
        
        if (null == inputSourceData){
            inputSourceData = new int[1];
        }
        
        totalLen = InputService.INPUT_EXCHANGE_HEADER_LEN + inputSourceData.length;
        exchangeData = new int[totalLen];

        exchangeData[InputService.INPUT_EXCHANGE_HEADER_OUTPUT_IDX] = output;
        exchangeData[InputService.INPUT_EXCHANGE_HEADER_TYPE_IDX] = exchangeType;        
        for (int i = 0, j = InputService.INPUT_EXCHANGE_HEADER_LEN; j < totalLen; i++, j++) {
            exchangeData[j] = inputSourceData[i];
        }
        
        try {
            ITVRemoteService service = TVManager.getRemoteTvService();
            if (service != null) {
                ret = service.inputSourceExchangeData_proxy(exchangeData);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        for (int i = 0, j = InputService.INPUT_EXCHANGE_HEADER_LEN; j < totalLen; i++, j++) {
            inputSourceData[i] = exchangeData[j];
        }

        return ret;
    }

    public class OutputRegionCapability {
        public boolean isEnable;
        public int x_min;
        public int x_max;
        public int y_min;
        public int y_max;
        public int width_min;
        public int width_max;
        public int height_min;
        public int height_max;

        public OutputRegionCapability() {
            this.isEnable = false;
            this.x_min = this.x_max = this.y_min = this.y_max = this.width_min = this.width_max = this.height_min = this.height_max = 0;
        }
    }
  
    /**
     * Get display region capability on designated screen output.
     * 
     * @param output
     *            screen output string.
     * @return OutputRegionCapability
     *            The min and max value of x, y, width and height.
     */
    public OutputRegionCapability getScreenOutputCapability(String output) {
        int ret = 0;
        OutputDevice outputDevice = this.findOutput(output);
        OutputRegionCapability resultCapability = new OutputRegionCapability();
        int index = 0;
        int isEnable = 0;
        int inputSourceData[] = { isEnable, resultCapability.x_min,
                resultCapability.x_max, resultCapability.y_min,
                resultCapability.y_max, resultCapability.width_min,
                resultCapability.width_max, resultCapability.height_min,
                resultCapability.height_max };

        if (null == outputDevice) {
            return null;
        }

        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_GET_TYPE_OUTPUT_REGION_CAPABILITY,
                inputSourceData);

        if (ret >= 0) {
            index = 0;
            if (inputSourceData[index++] > 0) {
                resultCapability.isEnable = true;
            } else {
                resultCapability.isEnable = false;
            }

            resultCapability.x_min = inputSourceData[index++];
            resultCapability.x_max = inputSourceData[index++];
            resultCapability.y_min = inputSourceData[index++];
            resultCapability.y_max = inputSourceData[index++];
            resultCapability.width_min = inputSourceData[index++];
            resultCapability.width_max = inputSourceData[index++];
            resultCapability.height_min = inputSourceData[index++];
            resultCapability.height_max = inputSourceData[index++];

            return resultCapability;
        }

        return null;
    }

    /**
     * Get video region capability on designated screen output.
     * 
     * @param output
     *            screen output string.
     * @return OutputRegionCapability
     *            The min and max value of x, y, width and height.
     */
    public OutputRegionCapability getScreenOutputVideoCapability(String output) {
        int ret = 0;
        OutputDevice outputDevice = this.findOutput(output);
        OutputRegionCapability resultCapability = new OutputRegionCapability();
        int index = 0;
        int isEnable = 0;
        int inputSourceData[] = { isEnable, resultCapability.x_min,
                resultCapability.x_max, resultCapability.y_min,
                resultCapability.y_max, resultCapability.width_min,
                resultCapability.width_max, resultCapability.height_min,
                resultCapability.height_max };

        if (null == outputDevice) {
            return null;
        }

        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_GET_TYPE_VIDEO_REGION_CAPABILITY,
                inputSourceData);

        if (ret >= 0) {
            index = 0;
            if (inputSourceData[index++] > 0) {
                resultCapability.isEnable = true;
            } else {
                resultCapability.isEnable = false;
            }

            resultCapability.x_min = inputSourceData[index++];
            resultCapability.x_max = inputSourceData[index++];
            resultCapability.y_min = inputSourceData[index++];
            resultCapability.y_max = inputSourceData[index++];
            resultCapability.width_min = inputSourceData[index++];
            resultCapability.width_max = inputSourceData[index++];
            resultCapability.height_min = inputSourceData[index++];
            resultCapability.height_max = inputSourceData[index++];
            
            Logger.i(TAG, 
                    " x_min " + resultCapability.x_min + 
                    " x_max " + resultCapability.x_max + 
                    " y_min " + resultCapability.y_min + 
                    " y_max " + resultCapability.y_max + 
                    " width_min " + resultCapability.width_min + 
                    " width_max " + resultCapability.width_max + 
                    " height_min " + resultCapability.height_min + 
                    " height_max " + resultCapability.height_max  );

            return resultCapability;
        }

        return null;
    }
 
    /**
     * Get aspect ratio enable before set aspect ratio, region or screen mode
     * on designated screen output.
     * 
     * @param output
     *            screen output string.
     * @return boolean
     *            aspect ratio enable flag, 
     *            if return true, you can set aspect ratio,
     *            or you can't set aspect ratio.
     */
    public boolean getAspectRatioEnable(String output) {
        int ret = 0;
        OutputDevice outputDevice = this.findOutput(output);
        int isEnable = 1;
        int inputSourceData[] = { isEnable };

        if (null == outputDevice) {
            return false;
        }

        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_GET_TYPE_ASPECT_RATIO_ENABLE,
                inputSourceData);

        if (ret >= 0) {
        	isEnable = inputSourceData[0];
                Logger.i(TAG, " getAspectRatioEnable " + isEnable );
        	if( 1 == isEnable ){
        		return true;
        	}
        	else {
        		return false;
        	}
        }
        return false;
    }
    
    public enum InputTimingType{
        TimingUnknown,
    	TimingVideo,
        TimingGraphic,
        TimingNotSupport
    }
    
    public enum AspectRatio{
    	AspectRatio_Unknown,
        AspectRatio_4_3,
        AspectRatio_16_9,
        AspectRatio_2_21_1,
        AspectRatio_10_11,
        AspectRatio_40_33,
        AspectRatio_16_11,
        AspectRatio_12_11,
        AspectRatio_3_2,
        AspectRatio_1_1,
        AspectRatio_USER       
    }

    public enum Tag3DType{
    	TAG3D_2D,
    	TAG3D_MVC,      /* MVC = Multi-View Codec */
    	TAG3D_FP,       /* FP = Frame Packing */
    	TAG3D_FS,       /* FS = Frame Sequential */
    	TAG3D_TB,       /* TB = Top-and-Bottom */
    	TAG3D_SBS,      /* SBS = Side-by-Side */
    	TAG3D_REALD,
    	TAG3D_SENSIO,
    	TAG3D_LA,       /* LA = Line Alternative */
    	TAG3D_TTDO,     /* TTD only */
    	TAG3D_NOT_SUPPORT
    }
    
    public class VideoResolution {
        public int width;
        public int height;
        public boolean isProgressive;
        public int frameRate;
        public String sdHdString;
        public InputTimingType timingType;
        public AspectRatio aspectRatio;
        public Tag3DType tag3dType; 
         
        public VideoResolution() {
            this.width = this.height = this.frameRate = 0;
            this.isProgressive = false;
            this.sdHdString = null;
            this.timingType = InputTimingType.TimingUnknown;
            this.aspectRatio = AspectRatio.AspectRatio_Unknown;
            this.tag3dType = Tag3DType.TAG3D_2D;
        }
    }
 
    /**
     * Get video resolution of designated screen output.
     * 
     * @param output
     *            screen output string.
     * @return VideoResolution
     *            video resolution information.
     */
    public VideoResolution getVideoResolution(String output) throws TVMException {
        OutputDevice outputDevice = this.findOutput(output);
        int ret = 0;
        VideoResolution videoResolution = new VideoResolution();
        int isProgressive = 0;
        int isHd = 0;
        int timingType = 0;
        int aspectRatio = 0;
        int tag3dType = 0;
        int inputSourceData[] = { 
                videoResolution.width, 
                videoResolution.height,
                isProgressive,
                videoResolution.frameRate,
                isHd,
                timingType,
                aspectRatio,
                tag3dType
                };
        
        if (null == outputDevice) {
            throw new TVMException("getVideoResolution FAIL.");
        }
        
        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_GET_TYPE_VIDEO_RESOLUTION,
                inputSourceData);
        
        if (ret >= 0) {
            videoResolution.width = inputSourceData[0];
            videoResolution.height = inputSourceData[1];            
            isProgressive = inputSourceData[2];
            videoResolution.isProgressive = (0!=isProgressive);
	        videoResolution.frameRate = inputSourceData[3];
            isHd = inputSourceData[4];
            videoResolution.sdHdString = (isHd == 1? InputService.INPUT_VIDEO_HD: InputService.INPUT_VIDEO_SD);
            timingType = inputSourceData[5];
            switch( timingType )
            {
            case 1:
            	videoResolution.timingType = InputTimingType.TimingVideo;
                break;
            case 2:
            	videoResolution.timingType = InputTimingType.TimingGraphic;
                break;            	
            case 3:
            	videoResolution.timingType = InputTimingType.TimingNotSupport;
                break;           	
            default:
            	videoResolution.timingType = InputTimingType.TimingUnknown;
                break;           	
            }
            aspectRatio = inputSourceData[6];
            switch( aspectRatio )
            {
            case 1:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_4_3;
                break;
            case 2:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_16_9;
                break;            	
            case 3:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_2_21_1;
                break;           	
            case 4:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_10_11;
                break;           	
            case 5:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_40_33;
                break;           	
            case 6:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_16_11;
                break;           	
            case 7:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_12_11;
                break;           	
            case 8:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_3_2;
                break;           	                
            case 9:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_1_1;
                break;           	               
            case 10:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_USER;
                break;           	                
            default:
            	videoResolution.aspectRatio = AspectRatio.AspectRatio_Unknown;
                break;               
            }
 
            tag3dType = inputSourceData[7];
            switch( tag3dType )
            {
            case 1:
            	videoResolution.tag3dType = Tag3DType.TAG3D_MVC;
                break;
            case 2:
            	videoResolution.tag3dType = Tag3DType.TAG3D_FP;
                break;            	
            case 3:
            	videoResolution.tag3dType = Tag3DType.TAG3D_FS;
                break;           	
            case 4:
            	videoResolution.tag3dType = Tag3DType.TAG3D_TB;
                break;           	
            case 5:
            	videoResolution.tag3dType = Tag3DType.TAG3D_SBS;
                break;           	
            case 6:
            	videoResolution.tag3dType = Tag3DType.TAG3D_REALD;
                break;           	
            case 7:
            	videoResolution.tag3dType = Tag3DType.TAG3D_SENSIO;
                break;           	
            case 8:
            	videoResolution.tag3dType = Tag3DType.TAG3D_LA;
                break;           	                
            case 9:
            	videoResolution.tag3dType = Tag3DType.TAG3D_TTDO;
                break;           	               
            case 10:
            	videoResolution.tag3dType = Tag3DType.TAG3D_NOT_SUPPORT;
                break;           	                
            default:
            	videoResolution.tag3dType = Tag3DType.TAG3D_2D;
                break;               
            }
        }
        
        return videoResolution;
    }
 
    /**
     * Get color system of designated screen output.
     * 
     * @param output
     *            screen output string.
     * @return int
     *            color system which define in ChannelCommon,
     *            such as ChannelCommon.COLOR_SYS_NTSC .
     */    
    public int getColorSystem(String output) throws TVMException {
        OutputDevice outputDevice = this.findOutput(output);
        int ret = 0;
        int colorSystem = ChannelCommon.COLOR_SYS_UNKNOWN;
        int inputSourceData[] = { colorSystem };
        
        if (null == outputDevice) {
            throw new TVMException("getColorSystem FAIL.");
        }
        
        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_GET_TYPE_COLOR_SYS,
                inputSourceData);
        
        if (ret >= 0) {
            colorSystem = inputSourceData[0];

            switch (colorSystem){
                case InputService.INPUT_COLOR_SYS_NTSC:     colorSystem = ChannelCommon.COLOR_SYS_NTSC;     break;
                case InputService.INPUT_COLOR_SYS_PAL:      colorSystem = ChannelCommon.COLOR_SYS_PAL;      break;
                case InputService.INPUT_COLOR_SYS_SECAM:    colorSystem = ChannelCommon.COLOR_SYS_SECAM;    break;
                case InputService.INPUT_COLOR_SYS_NTSC_443: colorSystem = ChannelCommon.COLOR_SYS_NTSC_443; break;
                case InputService.INPUT_COLOR_SYS_PAL_M:    colorSystem = ChannelCommon.COLOR_SYS_PAL_M;    break;
                case InputService.INPUT_COLOR_SYS_PAL_N:    colorSystem = ChannelCommon.COLOR_SYS_PAL_N;    break;
                case InputService.INPUT_COLOR_SYS_PAL_60:   colorSystem = ChannelCommon.COLOR_SYS_PAL_60;   break;
                case InputService.INPUT_COLOR_SYS_UNKNOWN:
                default:
                    colorSystem = ChannelCommon.COLOR_SYS_UNKNOWN;
                    break;
            }
        }
        
        return colorSystem;
    }

    public enum PlaneOrderCommandType{
        PLANE_ORDER_CTRL_UNKNOWN,
        PLANE_ORDER_CTRL_TOP,
//        PLANE_ORDER_CTRL_UP,  /* At 1st step, we only export TOP&BOTTOM API */
//        PLANE_ORDER_CTRL_DOWN,
        PLANE_ORDER_CTRL_BOTTOM
    }
    
    public class PlaneOrderCommand {
        public PlaneOrderCommandType type;
        private int numLayers;

        public PlaneOrderCommand() {
            this.type = PlaneOrderCommandType.PLANE_ORDER_CTRL_UNKNOWN;
            this.numLayers = 0;
        }
    }
        
    public int setPlaneOrder(String output, PlaneOrderCommand command ) throws TVMException {
    	/* Because HW limitation, the API has no effect, and you can use setPlaneArray() to set z-order */
    	/*    	
 		OutputDevice outputDevice = this.findOutput(output);
        int ret = 0;
        int planeOrderType = InputService.INPUT_SET_PLANE_ORDER_TYPE_UNKNOWN;
        int inputSourceData[] = { planeOrderType, command.numLayers };
        
        if (null == outputDevice) {
            throw new TVMException("setPlaneOrder FAIL.");
        }
        
        Logger.i(TAG, " setPlaneOrder " + "type:" + command.type + " numLayers:" + command.numLayers );
        
        if (PlaneOrderCommandType.PLANE_ORDER_CTRL_UNKNOWN == command.type){
            planeOrderType = InputService.INPUT_SET_PLANE_ORDER_TYPE_UNKNOWN;
        }        
        
        if (PlaneOrderCommandType.PLANE_ORDER_CTRL_TOP == command.type){
            planeOrderType = InputService.INPUT_SET_PLANE_ORDER_TYPE_TOP;
        }        
        
//        if (PlaneOrderCommandType.PLANE_ORDER_CTRL_UP == command.type){
//            planeOrderType = InputService.INPUT_SET_PLANE_ORDER_TYPE_UP;
//        }        
        
//        if (PlaneOrderCommandType.PLANE_ORDER_CTRL_DOWN == command.type){
//            planeOrderType = InputService.INPUT_SET_PLANE_ORDER_TYPE_DOWN;
//        }        
        
        if (PlaneOrderCommandType.PLANE_ORDER_CTRL_BOTTOM == command.type){
            planeOrderType = InputService.INPUT_SET_PLANE_ORDER_TYPE_BOTTOM;
        }        
        
        inputSourceData[0] = planeOrderType;
        inputSourceData[1] = command.numLayers;
        
        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_TYPE_PLANE_ORDER,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("set plane order FAIL.");
        }
    	*/        
        return InputService.INPUT_RET_OK; 
    }

    private class PlaneOrderSwap {
         public int layer;

        public PlaneOrderSwap() {
             this.layer = 0;
        }
    }
        
    private int setPlaneOrder(String output, PlaneOrderSwap swapLayer ) throws TVMException {
    	/* Because HW limitation, the API has no effect, and you can use setPlaneArray() to set z-order */    	
    	/*
    	OutputDevice outputDevice = this.findOutput(output);
        int ret = 0;
        int inputSourceData[] = { swapLayer.layer };
        
        if (null == outputDevice) {
            throw new TVMException("setPlaneOrder FAIL.");
        }
        
        Logger.i(TAG, " setPlaneOrderSwap " + "Layer" + swapLayer.layer);
         
        inputSourceData[0] = swapLayer.layer;
         
        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_TYPE_PLANE_ORDER_SWAP,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("set plane order swap FAIL.");
        }
        */
        return InputService.INPUT_RET_OK; 
    }
    
    private class PlaneOrder {
        public int currLayer;
        public int numLayers;
 
        public PlaneOrder() {
            this.currLayer = this.numLayers = 0;
        }
    }
    
    private PlaneOrder getPlaneOrder(String output) throws TVMException  {
    	/* Because HW limitation, we don't to supply the API, and you can use getPlaneArray() to get z-order */    	
        /*
    	OutputDevice outputDevice = this.findOutput(output);
        int ret = 0;
        PlaneOrder planeOrder = new PlaneOrder();
        int inputSourceData[] = { 
        		planeOrder.currLayer, 
        		planeOrder.numLayers };
        
        if (null == outputDevice) {
	    throw new TVMException("getPlaneOrder FAIL.");
        }
        
        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_GET_TYPE_INPUT_PLANE_ORDER,
                inputSourceData);
        
        if (ret >= 0) {
        	planeOrder.currLayer = inputSourceData[0];
        	planeOrder.numLayers = inputSourceData[1];            
        }
     
        return planeOrder;
        */
    	return null;
    }
      
    public enum PlaneName{
        GLPMX_MAIN,
        GLPMX_PIP,
        GLPMX_OSD1,
        GLPMX_OSD2
    }

    /**
     * Get plane array.
     * 
     * @return PlaneName[]
     *            The order of video plane and OSD plane.
     */    
    public PlaneName[] getPlaneArray() throws TVMException {
        int ret = 0;
        int inputSourceData[] = { 
                INPUT_PLANE_NAME_GLPMX_MAIN, 
                INPUT_PLANE_NAME_GLPMX_PIP, 
                INPUT_PLANE_NAME_GLPMX_OSD1, 
                INPUT_PLANE_NAME_GLPMX_OSD2 };
        PlaneName planes[] = new PlaneName[4];
        
        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_GET_TYPE_PLANE_ARRAY,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("set INPUT_GET_TYPE_PLANE_ARRAY FAIL.");
        }
        
        for (int i = 0; i < inputSourceData.length && i < planes.length; i++){
            if (INPUT_PLANE_NAME_GLPMX_MAIN == inputSourceData[i]){
                planes[i] = PlaneName.GLPMX_MAIN;
            }
            if (INPUT_PLANE_NAME_GLPMX_PIP == inputSourceData[i]){
                planes[i] = PlaneName.GLPMX_PIP;
            }
            if (INPUT_PLANE_NAME_GLPMX_OSD1 == inputSourceData[i]){
                planes[i] = PlaneName.GLPMX_OSD1;
            }
            if (INPUT_PLANE_NAME_GLPMX_OSD2 == inputSourceData[i]){
                planes[i] = PlaneName.GLPMX_OSD2;
            }
        }
        
        return planes;
    }
 
    /**
     * Set plane order.
     * 
     * @param PlaneName[]
     *            The order of video plane and OSD plane.
     * @return int
     *            Success or fail.
     */   
    public int setPlaneArray(PlaneName[] planes) throws TVMException {
        boolean hasMain, hasPip, hasOSD1, hasOSD2;
        int ret = 0;
        int inputSourceData[] = { 
                INPUT_PLANE_NAME_GLPMX_MAIN, 
                INPUT_PLANE_NAME_GLPMX_PIP, 
                INPUT_PLANE_NAME_GLPMX_OSD1, 
                INPUT_PLANE_NAME_GLPMX_OSD2 };
        
        if (4 != planes.length){
            throw new TVMException("planes array length is not 4.");
        }
        
        hasMain = hasPip = hasOSD1 = hasOSD2 = false;
        for (int i = 0; i < planes.length && i < inputSourceData.length; i++){
            Logger.i(TAG, " planes[i] " + i + "  " + planes[i]);
            if (PlaneName.GLPMX_MAIN == planes[i]){
                hasMain = true;
                inputSourceData[i] = INPUT_PLANE_NAME_GLPMX_MAIN;
            }
            
            if (PlaneName.GLPMX_PIP == planes[i]){
                hasPip = true;
                inputSourceData[i] = INPUT_PLANE_NAME_GLPMX_PIP;
            }
            
            if (PlaneName.GLPMX_OSD1 == planes[i]){
                hasOSD1 = true;
                inputSourceData[i] = INPUT_PLANE_NAME_GLPMX_OSD1;
            }
            
            if (PlaneName.GLPMX_OSD2 == planes[i]){
                hasOSD2 = true;
                inputSourceData[i] = INPUT_PLANE_NAME_GLPMX_OSD2;
            }
        }
        
        if (!(hasMain&&hasPip&&hasOSD1&&hasOSD2)){
            throw new TVMException("planes array should has all info of GLPMX_MAIN, GLPMX_PIP, GLPMX_OSD1 and GLPMX_OSD2.");
        }
        
        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_SET_TYPE_PLANE_ARRAY,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("set INPUT_GET_TYPE_PLANE_ARRAY FAIL.");
        }
        
        return InputService.INPUT_RET_OK;
    }
    
    public enum AutoAdjustType{
        AUTO_TYPE_VGA_ADJUST,
        AUTO_TYPE_PHASE,
        AUTO_TYPE_COLOR
    }
 
    /**
     * Set auto adjust.
     * 
     * @param output
     *            Screen output string.
     * @param AutoAdjustType
     *            Auto adjust type which defined in AutoAdjustType.          
     * @return int
     *            Success or fail.
     */     
    public int setAutoAdjust(String output, AutoAdjustType autoAdjustType) throws TVMException {
        OutputDevice outputDevice = this.findOutput(output);
        int ret = 0;
        int adjustType = InputService.INPUT_SET_AUTO_ADJUST_TYPE_UNKNOWN;
        int inputSourceData[] = { adjustType };
        
        if (null == outputDevice) {
            throw new TVMException("setAutoAdjust FAIL.");
        }
        
        if (AutoAdjustType.AUTO_TYPE_VGA_ADJUST == autoAdjustType){
            Logger.i(TAG, " AutoAdjustType.AUTO_TYPE_VGA_ADJUST ");
            adjustType = InputService.INPUT_SET_AUTO_ADJUST_TYPE_VGA_ADJUST;
        }
        
        if (AutoAdjustType.AUTO_TYPE_PHASE == autoAdjustType){
            Logger.i(TAG, " AUTO_TYPE_PHASE ");
            adjustType = InputService.INPUT_SET_AUTO_ADJUST_TYPE_PHASE;
        }
        
        if (AutoAdjustType.AUTO_TYPE_COLOR == autoAdjustType){
            Logger.i(TAG, " AUTO_TYPE_COLOR ");
            adjustType = InputService.INPUT_SET_AUTO_ADJUST_TYPE_COLOR;
        }
        
        inputSourceData[0] = adjustType;
        
        Logger.i(TAG, " inputSourceData[0] " + inputSourceData[0]);
        
        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_AUTO_ADJUST,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("auto adjust FAIL.");
        }
        
        return InputService.INPUT_RET_OK; 
    }
  
    /**
     * Get current input state, such as INPUT_STATE_NORMAL, INPUT_STATE_PIP, INPUT_STATE_POP.
     * 
     * @return InputState
     *            input state which defined in InputState.
     */   
    public InputState getCurrentState(){
        return this.stateMechine.getCurrentState();
    }

    /**
     * Enter pop state and return the current focus screen output.
     * We will stop main and sub's service that had binded throughout input service,
     * and application can bind its input or open mmp and so on to screen output later.
     *      
     * @return String
     *            The focus screen output string.
     */ 
    public String enterPOPAndReturnCurrentFocusOutput() throws TVMException {
        OutputDevice mainDevice = this.findOutput(OutputDevice.OUTPUT_MAIN);
        OutputDevice subDevice = this.findOutput(OutputDevice.OUTPUT_SUB);
        int ret = 0;
        
        if (!this.stateMechine.doOperation(InterStateMechine.OPERATION_ENTER_POP)){
            //throw new TVMException("can not enter pop in CURRENT state.");
            Logger.i(TAG, "Already enter pop!!! " );
            return mainDevice.getName();
        }
        
        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_SET_ENTER_POP_AND_RETURN_FOCUS,
                null);
        if (ret < 0) {
            throw new TVMException("enterPOPAndReturnCurrentFocusOutput FAIL.");
        }

        this.pipPopFocus.changeFocus(mainDevice);
        this.broadcastService.updateTVMode(InputState.INPUT_STATE_POP);
        mainDevice.setLastInput(null);
        subDevice.setLastInput(null);

        return mainDevice.getName();
    }

    /**
     * Enter pip state and return the current focus screen output.
     * We will stop main and sub's service that had binded throughout input service,
     * and application can bind its input or open mmp and so on to screen output later.
     *      
     * @return String
     *            The focus screen output string.
     */ 
    public String enterPIPAndReturnCurrentFocusOutput() throws TVMException {
        OutputDevice mainDevice = this.findOutput(OutputDevice.OUTPUT_MAIN);
        OutputDevice subDevice = this.findOutput(OutputDevice.OUTPUT_SUB);
        int ret = 0;
        
        if (!this.stateMechine.doOperation(InterStateMechine.OPERATION_ENTER_PIP)){
            //throw new TVMException("can not enter pip in CURRENT state.");
            Logger.i(TAG, "Already enter pip!!! " );
            return mainDevice.getName();
        }
        
        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_SET_ENTER_PIP_AND_RETURN_FOCUS,
                null);
        if (ret < 0) {
            throw new TVMException("enterPIPAndReturnCurrentFocusOutput FAIL.");
        }

        this.pipPopFocus.changeFocus(mainDevice);
        this.broadcastService.updateTVMode(InputState.INPUT_STATE_PIP);
        mainDevice.setLastInput(null);
        subDevice.setLastInput(null);

        return mainDevice.getName();
    }
    
    /**
     * Enter normal state and return the current focus screen output.
     * We will stop main and sub's service that had binded throughout input service,
     * and application can bind its input or open mmp and so on to screen output later.
     *      
     * @return String
     *            The focus screen output string.
     */ 
    public String enterNormalAndReturnCurrentOutput() throws TVMException {
        OutputDevice mainDevice = this.findOutput(OutputDevice.OUTPUT_MAIN);
        OutputDevice subDevice = this.findOutput(OutputDevice.OUTPUT_SUB);
        int ret = 0;
        
        if (!this.stateMechine.doOperation(InterStateMechine.OPERATION_ENTER_NORMAL)){
            //throw new TVMException("can not enter normal in CURRENT state.");
            Logger.i(TAG, "Already enter normal!!! " );
            return mainDevice.getName();
        }
        
        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_SET_ENTER_NORMAL,
                null);
        if (ret < 0) {
            throw new TVMException("enter normal FAIL.");
        }

        this.pipPopFocus.changeFocus(mainDevice);
        this.broadcastService.updateTVMode(InputState.INPUT_STATE_NORMAL);
        subDevice.setLastInput(null);

        return mainDevice.getName();
    }

    /**
     * Set focus of designated screen output which audio can output from it.
     * 
     * @param dstOutput
     *            The screen output string which focus change to.
     *
     * @return int
     *            Success or fail.
     */     
    public int focusChangeTo(String dstOutput) throws TVMException {
        OutputDevice outputDevice = this.findOutput(dstOutput);
        int ret = 0;
        int inputSourceData[] = { outputDevice.getOutput() };
        
        if (null == outputDevice) {
            throw new TVMException("find output FAIL.");
        }
        /* Not to judge state because client may set focus to main in normal mode */
        /*
        if (!this.stateMechine.doOperation(InterStateMechine.OPERATION_CHANGE_FOCUS)){
            throw new TVMException("can not change focus in CURRENT state.");
        }
        */        
        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_SET_FOCUS_CHANGE_TO,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("change focus FAIL.");
        }
        
        this.pipPopFocus.changeFocus(outputDevice);
        
        return InputService.INPUT_RET_OK; 
    }
 
    /**
     * Get current focus screen output string.
     * 
     * @return String
     *            Current focus screen output string.
     */ 
    public String getCurrentFocus(){
        OutputDevice outputDevice = this.pipPopFocus.getFocusedOutput();
        
        return outputDevice.getName();
    }
 
    /**
     * Stop designated screen output.
     * 
     * @param designateOutput
     *            The screen output string which need to stop.
     *            
     * @param needSync
     *            The stop action is sync or not.
     *            
     * @return int
     *            Success or fail.
     */ 
    public int stopDesignateOutput(String designateOutput, boolean needSync) throws TVMException{
        OutputDevice outputDevice = this.findOutput(designateOutput);
        int ret = 0;
        int isNeedSync = 0;
        int[] inputSourceData = {isNeedSync};
        
        Logger.i(TAG, "stopDesignateOutput " + designateOutput);

        if (null == outputDevice) {
            throw new TVMException("designateOutput is null!!!");
        }
        
        if (needSync){
            isNeedSync = 1;
        }
        else{
            isNeedSync = 0;
        }
        
        inputSourceData[0] = isNeedSync;
        
        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_STOP_OUTPUT, inputSourceData);
        if (ret < 0) {
            throw new TVMException("SDK stop output FAIL.");
        }

        outputDevice.setLastInput(null);
        
        return InputService.INPUT_RET_OK;
    }
 
    /**
     * Start video stream on designated screen output.
     * 
     * @param designateOutput
     *            The screen output string which need to stop.
     *            
     * @return int
     *            Success or fail.
     */ 
    public int startVideoStream(String designateOutput) throws TVMException{
        OutputDevice outputDevice = this.findOutput(designateOutput);
        int ret = 0;
        int isNeedSync = 1;
        
        Logger.i(TAG, "startVideoStream:" + designateOutput);

        if (null == outputDevice) {
            throw new TVMException("designateOutput is null!!!");
        }

        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_TYPE_START_VIDEO_STREAM, 
                null);
        if (ret < 0) {
            throw new TVMException("SDK start video stream FAIL.");
        }
        
        return InputService.INPUT_RET_OK;
    }
    
    /**
     * Stop video stream on designated screen output.
     * 
     * @param designateOutput
     *            The screen output string which need to stop.
     *            
     * @return int
     *            Success or fail.
     */ 
    public int stopVideoStream(String designateOutput) throws TVMException{
        OutputDevice outputDevice = this.findOutput(designateOutput);
        int ret = 0;
        int isNeedSync = 1;
        
        Logger.i(TAG, "stopVideoStream:" + designateOutput);

        if (null == outputDevice) {
            throw new TVMException("designateOutput is null!!!");
        }

        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_TYPE_STOP_VIDEO_STREAM, 
                null);
        if (ret < 0) {
            throw new TVMException("SDK stop video stream FAIL.");
        }
        
        return InputService.INPUT_RET_OK;
    }
    
    /**
     * Start audio stream on designated screen output.
     * 
     * @param designateOutput
     *            The screen output string which need to stop.
     *            
     * @return int
     *            Success or fail.
     */ 
    public int startAudioStream(String designateOutput) throws TVMException{
        OutputDevice outputDevice = this.findOutput(designateOutput);
        int ret = 0;
        int isNeedSync = 1;
        
        Logger.i(TAG, "startAudioStream:" + designateOutput);

        if (null == outputDevice) {
            throw new TVMException("designateOutput is null!!!");
        }

        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_TYPE_START_AUDIO_STREAM, 
                null);
        if (ret < 0) {
            throw new TVMException("SDK start audio stream FAIL.");
        }
        
        return InputService.INPUT_RET_OK;
    }
    
    /**
     * Stop audio stream on designated screen output.
     * 
     * @param designateOutput
     *            The screen output string which need to stop.
     *            
     * @return int
     *            Success or fail.
     */ 
    public int stopAudioStream(String designateOutput) throws TVMException{
        OutputDevice outputDevice = this.findOutput(designateOutput);
        int ret = 0;
        int isNeedSync = 1;
        
        Logger.i(TAG, "stopAudioStream:" + designateOutput);

        if (null == outputDevice) {
            throw new TVMException("designateOutput is null!!!");
        }

        ret = exchangeInputSourceData(outputDevice.getOutput(),
                InputService.INPUT_SET_TYPE_STOP_AUDIO_STREAM, 
                null);
        if (ret < 0) {
            throw new TVMException("SDK stop audio stream FAIL.");
        }
        
        return InputService.INPUT_RET_OK;
    }
    
    /**
     * Get signal status of designated input.
     * This API can co-work with auto detect source.
     * 
     * @param input
     *            Input string.
     *            
     * @return InputSignalStatus
     *            Input signal status which defined in InputSignalStatus.
     */ 
    public InputSignalStatus getInputSignalStatus( String input ) throws TVMException {
        InputSignalStatus inputSignalStatus = InputSignalStatus.SignalStatusLoss;
        InputSourceRecord inputSourceRecord = null;
        InputSourceType inputSourceType = null; 
        
        Logger.i(TAG, "getInputSignalStatus input==" + input);
        
        /* find input */
        for (int i = 0; i < this.inputSourceTypesNum; i++) {
            inputSourceRecord = this.inputSourceTypes[i].getInputSourceRecord(input);
            if (null == inputSourceRecord) {
                continue;
            }
            
            inputSourceType = this.inputSourceTypes[i];
            
            break;
        }
        
        if (null == inputSourceRecord){
            throw new TVMException("can not find the input-" + input + " getInputSignalStatus FAIL");
        }

        int ret = 0;
        int signalStatus = InputService.INPUT_GET_SIGNAL_STATUS_UNKNOWN;
        int inputSourceData[] = { 
                inputSourceType.getInputType(),
                inputSourceRecord.getInternalIdx(),
                signalStatus };
        
        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_GET_TYPE_INPUT_SIGNAL_STATUS,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("change focus FAIL.");
        }
        
        signalStatus = inputSourceData[2];
        Logger.i(TAG, "getInputSignalStatus " + signalStatus);
        
        switch (signalStatus){
        case INPUT_GET_SIGNAL_STATUS_LOSS:
            inputSignalStatus = InputSignalStatus.SignalStatusLoss;
            break;
        case INPUT_GET_SIGNAL_STATUS_LOCKED:
            inputSignalStatus = InputSignalStatus.SignalStatusLocked;
            break;
        }
        
        Logger.i(TAG, "getInputSignalStatus inputSignalStatus==" + inputSignalStatus);
        
        return inputSignalStatus;
    }
    
    //TODO
    protected int lockOutput(){
        return 0;
    }
 
    //TODO
    protected int unlockOutput(){
        return 0;
    }

    /**
     * Set special property to screen output.
     * 
     * @param output
     *            screen output string.
     * @param setType
     *            The set type. 
     *            InputExchange static elements contain all of the type.
     *           
     * @param inputExchange
     *            the temporary storage. It is just a super class. Different
     *            type Different detail inputExchangeData
     * @return int
     * 			  Success or fail.
     */
    public int setOutputProperty(String output, int setType, InputExchange inputExchange) {
        OutputDevice outputDevice = this.findOutput(output);

        if (null == outputDevice) {
            return InputService.INPUT_RET_FAIL;
        }

        if (InputExchange.INPUT_SET_TYPE_OUTPUT_MUTE == setType) {
            if (inputExchange instanceof InputExchangeOutputMute) {
                InputExchangeOutputMute inputExchangeOutputMute = (InputExchangeOutputMute) inputExchange;
                try {
                    ITVRemoteService service = TVManager.getRemoteTvService();
                    if (service != null) {
                        service.inputServiceSetOutputMute(outputDevice.getOutput(), inputExchangeOutputMute.isDoMute());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        return InputService.INPUT_RET_OK;
    }
    
    /**
     * Set mjc bypass window to tell companion chip(8283) which region will not speicil handle.
     * 
     * @param windowID
     *            window ID(0~4) which set to driver.
     * @param r
     *            The size and position which set to driver.
     * @return int
     * 		   success or fail.
     * @throws TVMException 
     */
    public int setMjcBypassWindow(int windowId, Rect r) throws TVMException {
    	int ret;
        int inputSourceData[] = { windowId, r.left, r.right, r.top, r.bottom };
        
        if (null == r) {
            throw new TVMException("rect is null!!!");
        }

        ret = exchangeInputSourceData(OutputDevice.OUTPUT_UNKNOWN,
                InputService.INPUT_SET_TYPE_MJC_BYPASS_WINDOW,
                inputSourceData);
        if (ret < 0) {
            throw new TVMException("set mjc bypass window FAIL.");
        }
        
        return InputService.INPUT_RET_OK; 
    }
    
    public InputSignalStatus getOutputSignalStatus(String output) throws TVMException{
    	InputSignalStatus outputSignalStatus = InputSignalStatus.SignalStatusLoss;
    	for (int i = 0; i < this.outputDevicesNum; i++) {
            if (this.outputDevices[i].isSameOutput(output)) {
            	int signalStatus = INPUT_GET_SIGNAL_STATUS_LOSS;
            	int inputSourceData[] = {INPUT_GET_SIGNAL_STATUS_LOSS};
                int ret = this.exchangeInputSourceData(this.outputDevices[i].getOutput(), 
                		InputService.INPUT_GET_TYPE_OUTPUT_SIGNAL_STATUS,
                		inputSourceData);
                if (ret < 0) {
                    throw new TVMException("INPUT_GET_TYPE_OUTPUT_SIGNAL_STATUS FAIL.");
                }
                signalStatus = inputSourceData[0];
                Logger.i(TAG, "getOutputSignalStatus " + signalStatus);
                
                switch (signalStatus){
                case INPUT_GET_SIGNAL_STATUS_LOSS:
                	outputSignalStatus = InputSignalStatus.SignalStatusLoss;
                    break;
                case INPUT_GET_SIGNAL_STATUS_LOCKED:
                	outputSignalStatus = InputSignalStatus.SignalStatusLocked;
                    break;
                }
                
                Logger.i(TAG, "getOutputSignalStatus outputSignalStatus==" + outputSignalStatus);
                
                return outputSignalStatus;
            }
        } 
    	throw new TVMException("Invalid output!"); 
    }
    /**
     * Input service init, it is called by TVManager post_init(),
     * and we can init all input source records.
     * 
     */    
    public void inputServiceInit(){
    	this.fillgRecord();
    	
    	TVManager tvManager = TVManager.getInstance(null);
    	
    	this.broadcastService = (BroadcastService) tvManager.getService(BroadcastService.BrdcstServiceName);    	
    	this.pipPopFocus.setBroadcastService(broadcastService);
    	
    	this.ciService = (CIService) tvManager.getService(CIService.CIServiceName);
    }

    protected void fillgRecord() {
        /* input source record will initial here */
        int ret = 0;
        int i = 0;
        InputRecord inputRecord = new InputRecord();

        inputRecord.reset();
        while (ret >= 0) {
            try {
                ITVRemoteService service = TVManager.getRemoteTvService();
                if (service != null) {
                    ret = service.inputServiceGetRecord_proxy(i++, inputRecord);
                }
                if (ret >= 0) {
                    this.addInputSourceRecord(inputRecord.inputType, inputRecord.gourp, inputRecord.gourpSize,
                            inputRecord.id, inputRecord.internalIdx);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    }
}

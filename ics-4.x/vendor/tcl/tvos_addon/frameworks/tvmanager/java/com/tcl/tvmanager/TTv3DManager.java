package com.tcl.tvmanager;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.mediatekk.tvcommon.TVConfigurer;
import com.mediatekk.tvcommon.TVOption;
import com.mediatek.tv.common.BaseConfigType;

//import com.tcl.tvmanager.vo.EnTCL3DAspectRatio;
//import com.tcl.tvmanager.vo.EnTCL3DColorTemperature;
//import com.tcl.tvmanager.vo.EnTCL3DPictureMode;
//import com.tcl.tvmanager.vo.EnTCL3DVideo3DTo2D;
import com.tcl.tvmanager.vo.EnTCL3DVideoDisplayFormat;

//import com.tvos.common.vo.TvOsType;
//import com.tvos.common.TvManager;
//import com.tvos.common.vo.TvOsType.Enum3dType;
//import com.tvos.common.ThreeDimensionManager;
//import com.tvos.common.exception.TvCommonException;

public class TTv3DManager {
    private static final String TAG = "TTv3DManager";
    private Context mContext;

    private static TTv3DManager sInstance = null;
    public static TVConfigurer mCfg = null;

    private TTv3DManager(Context context) {
        mContext = context;
        mCfg = TVConfigurer.getInstance(context);
    }

    // TODO 需要做同步处理
    public static TTv3DManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized(TTv3DManager.class){
                if (sInstance == null) {
                    sInstance = new TTv3DManager(context);
                }
            }
        }

        return sInstance;
    }

    public boolean setDisplayFormat(EnTCL3DVideoDisplayFormat displayFormat) {
    boolean ret = false;

    //assert(mCfg);
    TVOption<Integer> tvOption3DMode = (TVOption<Integer>) mCfg.getOption(BaseConfigType.CFG_3D_MODE);
    int modeValue = BaseConfigType.CFG_3D_MODE_OFF;
	
	switch(displayFormat) {
	case EN_TCL_3D_NONE:
	    modeValue = BaseConfigType.CFG_3D_MODE_OFF;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_NONE::");
	    break;
	case EN_TCL_3D_AUTO:
	    modeValue = BaseConfigType.CFG_3D_MODE_AUTO;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_AUTO::");
	    break;
    case EN_TCL_3D_SIDE_BY_SIDE:
        modeValue = BaseConfigType.CFG_3D_MODE_SIDE_SIDE;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_SIDE_BY_SIDE::");
        break;
    case EN_TCL_3D_TOP_BOTTOM:
        modeValue = BaseConfigType.CFG_3D_MODE_TOP_AND_BTM;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_TOP_BOTTOM::");
        break;
    case EN_TCL_3D_FRAME_PACKING:
        modeValue = BaseConfigType.CFG_3D_MODE_FRM_SEQ;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_FRAME_PACKING::");
        break;
    case EN_TCL_3D_LINE_ALTERNATIVE:
	    modeValue = BaseConfigType.CFG_3D_MODE_LINE_INTERLEAVE;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_ALTERNATIVE::");
        break;
    case EN_TCL_3D_2DTO3D:
        modeValue = BaseConfigType.CFG_3D_MODE_2D_TO_3D;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_2DTO3D::");
        break;
    case EN_TCL_3D_CHECK_BOARD:
	    modeValue = BaseConfigType.CFG_3D_MODE_CHK_BOARD;
	    Log.d(TAG,"setDisplayFormat____EN_TCL_3D_CHECK_BOARD::");
        break;
    case EN_TCL_3D_PIXEL_ALTERNATIVE:
	    modeValue = BaseConfigType.CFG_3D_MODE_OFF;
	    Log.d(TAG,"setDisplayFormat____donot support EN_TCL_3D_PIXEL_ALTERNATIVE, set to EN_TCL_3D_NONE::");
        break;
    case EN_TCL_3D_ALTERNATIVE:
	    modeValue = BaseConfigType.CFG_3D_MODE_OFF;
	    Log.d(TAG,"setDisplayFormat____donot support EN_TCL_3D_ALTERNATIVE, set to EN_TCL_3D_NONE::");
        break;
	default:
	    modeValue = BaseConfigType.CFG_3D_MODE_OFF;
	    Log.d(TAG," setDisplayFormat____set to default:EN_TCL_3D_NONE::");
	    break;
	}

    ret = tvOption3DMode.set(modeValue);	

    if(displayFormat == EnTCL3DVideoDisplayFormat.EN_TCL_3D_AUTO){
        int setValue = BaseConfigType.CFG_VID_3D_NAV_AUTO_CHG_AUTO;
        TVOption<Integer> tvOption3DNav = (TVOption<Integer>) mCfg.getOption(BaseConfigType.CFG_3D_NAV_AUTO);
        ret |= tvOption3DNav.set(setValue);
    }

    return ret;

    }

    public EnTCL3DVideoDisplayFormat getDisplayFormat(){
        TVOption<Integer> tvOption3DMode = (TVOption<Integer>) mCfg.getOption(BaseConfigType.CFG_3D_MODE);
        int format = tvOption3DMode.get();
        EnTCL3DVideoDisplayFormat retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_NONE;
        switch(format){
        case BaseConfigType.CFG_3D_MODE_OFF:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_NONE;
            break;
        case BaseConfigType.CFG_3D_MODE_AUTO:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_AUTO;
            break;
        case BaseConfigType.CFG_3D_MODE_SIDE_SIDE:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_SIDE_BY_SIDE;
            break;
        case BaseConfigType.CFG_3D_MODE_TOP_AND_BTM:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_TOP_BOTTOM;
            break;
        case BaseConfigType.CFG_3D_MODE_FRM_SEQ:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_FRAME_PACKING;
            break;
        case BaseConfigType.CFG_3D_MODE_LINE_INTERLEAVE:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_LINE_ALTERNATIVE;
            break;
        case BaseConfigType.CFG_3D_MODE_2D_TO_3D:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_2DTO3D;
            break;
        case BaseConfigType.CFG_3D_MODE_CHK_BOARD:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_CHECK_BOARD;
            break;
        default:
            retFormat = EnTCL3DVideoDisplayFormat.EN_TCL_3D_NONE;
	    Log.d(TAG,"getDisplayFormat____get default , return EN_TCL_3D_NONE::");
            break;
        }

        return retFormat;
    }
}

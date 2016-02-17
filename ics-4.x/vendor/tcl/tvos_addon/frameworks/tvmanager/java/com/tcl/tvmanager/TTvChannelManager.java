package com.tcl.tvmanager;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.app.Activity;
import android.os.RemoteException;

import com.mediatek.tv.model.ChannelInfo;
import com.tcl.tvmanager.TTvCommonManager;

import com.mediatekk.tvcommon.TVChannel;
import com.mediatekk.tvcommon.TVCommonNative;
import com.tcl.tvmanager.vo.EnTCLInputSource;

import com.tcl.tvmanager.vo.EnTCLMemberServiceType;
import com.tcl.tvmanager.vo.ProgramInfo;


public class TTvChannelManager {
	
//	protected TTvChannelManager(ChannelInfo info) {
//		//super(info);
//		// TODO Auto-generated constructor stub
//	}

//	private static TVChannel mtkTvChannel;
	private static TTvChannelManager m_tvChannelManager;
//	private DataReader mDataReader;
	private static Context mContext;

	
	public static TTvChannelManager getInstance(Context context) {
		if(m_tvChannelManager==null) {
			m_tvChannelManager=new TTvChannelManager();
		}
		mContext = context;
		return m_tvChannelManager;
	}
	
	public ProgramInfo getCurrentProgramInfo() {
		ProgramInfo CurrentProInfo;
		ChannelInfo info = null;
		TVChannel tvchannel = null;
		try {
			tvchannel = TVCommonNative.getDefault(mContext).getCurrentChannel();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CurrentProInfo=new ProgramInfo();
		CurrentProInfo.programName = tvchannel.getChannelName();
		CurrentProInfo.index       = tvchannel.getChannelNum();
		CurrentProInfo.number      = CurrentProInfo.index;
		return CurrentProInfo;
		
	}

	public void setChannelChangeFreezeMode(boolean freezeMode) {

	}

/*
	public int  getProgramCount(MEnTCLMemberServiceType ServiceType) {
		TTvCommonManager tvCommon;
		if(!(ServiceType == MEnTCLMemberServiceType.EN_TCL_ATV ||
				ServiceType == MEnTCLMemberServiceType.EN_TCL_DTV))
			return 0;
		mDataReader = DataReader.getInstance(mContext);
		mDataReader.getChannelList(); // add by apple
		tvCommon = TTvCommonManager.getInstance(mContext);
		List<TVChannel> list  = tvCommon.getChannels();
		//TVLog.e(tag, "initChannelDate,list:" + list);
		if(null==list){
			return 0;
		}
		return list.size();
	}
	*/
	
	/*modify by zouhc@tcl.com*/
	public List<ProgramInfo> getChannelList(EnTCLInputSource inputSource) {
		TTvCommonManager tvCommon;
		if(inputSource != EnTCLInputSource.EN_TCL_DTV)
			return null;
		//mDataReader = DataReader.getInstance(mContext);
		//mDataReader.getChannelList(); // add by apple
		tvCommon = TTvCommonManager.getInstance(mContext);
		List<TVChannel> list  = tvCommon.getChannels();
		if(null==list) return null;
		//TVLog.e(tag, "initChannelDate,list:" + list);
		List<ProgramInfo> programList = new ArrayList<ProgramInfo>();
		for (TVChannel ch : list) {
			if (!ch.isSkip()) {
				//ChannelInfo info = null;

				ProgramInfo info = new ProgramInfo();
				info.programName = ch.getChannelName();
				if(ch.isAnalog()){
					info.index = ch.getChannelNum();
					info.number=info.index ;
					if(info.index > 899){
						info.index  = info.index  - 899;
					}
				}else{
					info.index = ch.getChannelNum();
					info.number=info.index ;
				}
				programList.add(info);
			}
		}
		return programList;
	}

      //选择频道
	public void selectProgram(int channelNumber, EnTCLMemberServiceType ServiceType) {
		TTvCommonManager tvCommon;
		if(!(ServiceType == EnTCLMemberServiceType.EN_TCL_ATV ||
				ServiceType == EnTCLMemberServiceType.EN_TCL_DTV))
			return;
		tvCommon=TTvCommonManager.getInstance(mContext);
		tvCommon.select(channelNumber);
	}
	
		/**
	 * @see {@link ITVCommon#channelUp()} add by zouhc@tcl.com
	 */
	public void programUp() {
		try {
			TVCommonNative.getDefault(mContext).channelUp();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see {@link ITVCommon#channelDown()} add by zouhc@tcl.com
	 */
	public void programDown() {
		try {
			TVCommonNative.getDefault(mContext).channelDown();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


       /**
        * @for cyberui1404 add (more Country lanages)
        */
	public String getSystemCountryCode() {

		String country = "";
		return country;
	}
	
}

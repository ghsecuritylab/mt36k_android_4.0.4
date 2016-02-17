package com.mediatek.tvcm;

import com.mediatek.tv.common.ConfigType;

import android.content.Context;

/**
 * Customer implementation entry.
 * 
 * TV Common logic can be extended for each Components. Customer class is
 * firstly loaded and extended TVContent object ,if exists, is created.
 * 
 * @author mtk40063
 * 
 */
public class TVCustomer extends TVComponent {
	protected TVCustomer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create entry object of TVContent. If customer implementation extends new
	 * Components, this should create the content object.
	 * 
	 * @param context
	 * @return
	 */
	static TVContent createContent(Context context) {
		return new com.mediatek.tvcm.TVContent(context);
	}
	
	
	
	
	protected void init() {
		TvLog(" Customer Base Implementation ");

		// Now we check whether need a special channel number changing...
		TVOptionRange<Integer> wakeup = (TVOptionRange<Integer>) getContent()
				.getConfigurer().getOption(ConfigType.CFG_WAKE_UP_REASON);
		int wrReason = ConfigType.WAKE_UP_REASON_AC_POWER;
		if (wakeup != null) {
			wrReason = wakeup.get();
			// This should be re-implement for the new requirement.
			if (wrReason == ConfigType.WAKE_UP_REASON_RTC
					|| wrReason == ConfigType.WAKE_UP_REASON_RTC_SPECIAL) {
				TVStorage st = getContent().getStorage();						
				String chNum;
				chNum = st.get(TVTimerManager.POWERON_CHANNEL, "0");
				TVChannelSelector cs = getContent().getChannelSelector();
				cs.select(new Short(chNum));
			}
		}

	}

	public String getInitInput() {
		return "atv";
	}

	
}

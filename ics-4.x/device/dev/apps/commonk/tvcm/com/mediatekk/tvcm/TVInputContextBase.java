package com.mediatekk.tvcm;

import java.util.List;

import com.mediatekk.tvcm.TVChannelSelector.SelectorListener;

/**
 * This is a base (and stupid?) implementation of Input Context. This only
 * contains a channel list and current channel storage. TODO: Config,
 * 
 * @author mtk40063
 * 
 */
public class TVInputContextBase implements TVInputContext, SelectorListener {
	// final String LIST_PAL_NAME = "CHLST[PAL]";
	final String FIELD_START_CH = "START_CHANNEL";
	short curChNum;
	int cfgGrpIdx = 0;
	TVContent content = null;
	String hwName;
	String chListName = "TVList";

	TVInputContextBase(TVContent content, String chListName, String hwName,
			int cfgGrpIdx) {

		this.hwName = hwName;
		this.cfgGrpIdx = cfgGrpIdx;
		TVChannelManager cm = content.getChannelManager();

		this.chListName = chListName;
		this.content = content;
		TVStorage st = content.getStorage();
		String curChStr = st.get(chListName + FIELD_START_CH);
		if (curChStr == null) {
			// TODO:I need to implement a getFirstChannel, I am sure...
			TVChannelList list = cm.getList(chListName);
			TVChannel ch = list.firstChannel(false, null);
			if (ch != null) {
				curChNum = ch.getChannelNum();
			} else {
				// No channel
				curChNum = 0;
			}
		} else {
			curChNum = new Short(curChStr);
		}
	}
	public int getCfgGrpIdx(TVInput input) {
		return cfgGrpIdx;
	}

	public String getHWName(TVInput input) {
		return hwName;
	}

	public void onConnected(TVInput input, TVOutput output) {
		if (input == null) {
			return;
		}

		// Set config group.
		TVConfigurer cfger = input.getInputManager().getContent()
				.getConfigurer();
		if(output.getName().equals(TVInputManager.OUTPUT_NAME_MAIN)) {
			cfger.setGroup(cfgGrpIdx);
		}
		// Set default channel list for this input context.
		TVChannelManager cm = input.getInputManager().getContent()
				.getChannelManager();
		cm.setCurrList(chListName);

		// Set default channel list for this input context.
		TVChannelSelector cs = input.getInputManager().getContent()
				.getChannelSelector();
		cs.addSelectorListener(this);
		TVChannelList curList = cm.getList(chListName);
		if (curList != null) {
			cs.setCurrentCh(curList.findChannelByNumber(curChNum));
		} else {
			cs.setCurrentCh(null);
		}
		if (!cs.select(curChNum)) {
			cs.select(cm.firstChannel(false, null));
		}
		cm.getContent().sendUpdate();
	}

	public void onDisconnected(TVInput input, TVOutput output) {
		TVChannelSelector cs = input.getInputManager().getContent()
				.getChannelSelector();
		cs.removeSelectorListener(this);
	}

	public void onBlock(TVChannel ch) {

	}

	public void onChannelSelect(TVChannel ch) {
		if (ch != null) {
			curChNum = ch.getChannelNum();
			TVStorage st = content.getStorage();
			st.set(chListName + FIELD_START_CH, new Short(curChNum).toString());
		}
	}

	public void onSignalChange(boolean hasSignal) {

	}

	public void onScramble(boolean hasScramble, String state) {

	}
}

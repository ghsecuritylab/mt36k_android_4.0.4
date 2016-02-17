package com.mediatekk.tvcm;

public interface TVInputContext {
	public String getHWName(TVInput input);
	public int getCfgGrpIdx(TVInput input) ;
	public void onConnected(TVInput input, TVOutput output);
	public void onDisconnected(TVInput input, TVOutput output);
}

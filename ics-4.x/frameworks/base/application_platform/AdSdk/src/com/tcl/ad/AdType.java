package com.tcl.ad;

public enum AdType {
	
	AD_TYPE_TEXT("T"),
	AD_TYPE_IMAGE("I"),
	AD_TYPE_VIDEO("V"),
	AD_TYPE_AUDIO("A");
	
	private String type;
	
	private AdType(String format) {
		this.type = format;
	}

	@Override
	public String toString() {
		return type;
	}
	
	public static AdType type(String t) {
		AdType adType = null;
		
		if(t != null) {
			if(t.equals("T"))
				adType = AD_TYPE_TEXT;
			if(t.equals("I"))
				adType = AD_TYPE_IMAGE;
			if(t.equals("V"))
				adType = AD_TYPE_VIDEO;
			if(t.equals("A"))
				adType = AD_TYPE_AUDIO;
		}
		
		return adType;
	}
}

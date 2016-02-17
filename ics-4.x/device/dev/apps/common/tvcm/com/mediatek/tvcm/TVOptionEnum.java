package com.mediatek.tvcm;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * TVOption with several possible values.
 * 
 * This may be not used currently.... Normally we just
 * need export the option itself with several constant
 * fields like :
 * <pre>
 * public static final T VALUE1;
 * public static final T VALUE2;
 * </pre>
 * 
 * User may want to bind data such as resource to
 * a option, this class can help user maintain the mapping.
 * E.g.
 * <pre>
 	    TVOptionRange<Integer> optSc = (TVOptionRange<Integer>) sc
				.getOption(TVScanner.SCAN_OPTION_SCAN_MODE);
				
		//Create enum option for this option		
		TVOptionEnum<String, Integer> enumSc = optSc.createEnum(
				new String[]{"TERR", "cable"}, new Integer[]{
				TVScanner.ScanModeOption.SCAN_TERRESTRIAL,
				TVScanner.ScanModeOption.SCAN_CABLE});
				
				
		System.out.println("<#######>for Cable val is " + enumSc.getBound("cable"));
		System.out.println("<#######>for TERR val is " + enumSc.getBound("TERR"));
		System.out.println("<#######>for SCAN_TERRESTRIAL user is " + enumSc.getUser(TVScanner.ScanModeOption.SCAN_TERRESTRIAL));
		System.out.println("<#######>for SCAN_CABLE user is " + enumSc.getUser(TVScanner.ScanModeOption.SCAN_CABLE));
 * </pre>
 * If someday the value changed, the mapping would be automatically generated and safe.
 * 
 * @author mtk40063
 * @deprecated
 * @param <T>
 */


public abstract class TVOptionEnum<E, T  extends Comparable<T>> extends TVOptionRange<T> {
	private final E usrVals[];
	private final T vals[];

	TVOptionEnum(E usrVals[], T vals[]) {
		//FIX, should copy the ref??
		//OR let user can do some tricky things...
		if(usrVals == null || vals == null) {
			throw new IllegalArgumentException();
		}
		if(usrVals.length != vals.length) {
			throw new IllegalArgumentException();
		}
		this.usrVals = usrVals;
		this.vals = vals;
	}

	
	
	public E getUser(T val, E defUsrVal) {
		for(int i = 0; i < vals.length; i ++) {
			if(vals[i].equals(val)) {
				return usrVals[i];
			}
		}
		return defUsrVal;
	}
	public T getBound(E usrVal, T defVal) {
		for(int i = 0; i < usrVals.length; i ++) {
			if(usrVals[i].equals(usrVal)) {
				return vals[i];
			}
		}
		return defVal;
	}
	
	public E getUser(T val) {
		return getUser(val, null);
	}
	public T getBound(E usrVal) {
		return getBound(usrVal, null);
	}
}

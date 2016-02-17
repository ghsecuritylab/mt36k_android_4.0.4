package com.mediatek.tv.model;

/**
 * TV Runtime information
 * 
 */
public class RunTime {
    private static byte currentBrdcstType;

    /**
     * @see TVCommon#BRDCST_MEDIUM_DIG_TERRESTRIAL
     * @see TVCommon#BRDCST_MEDIUM_DIG_CABLE
     * @see TVCommon#BRDCST_MEDIUM_DIG_SATELLITE
     * @see TVCommon#BRDCST_MEDIUM_ANA_TERRESTRIAL
     * @see TVCommon#BRDCST_MEDIUM_ANA_CABLE
     * @see TVCommon#BRDCST_MEDIUM_ANA_SATELLITE
     * 
     */
    private static byte currentBrdcstMedium;

    /**
     * @return the currentBrdcstType
     */
    public static byte getCurrentBrdcstType() {
        return currentBrdcstType;
    }

    /**
     * @param currentBrdcstType
     *            the currentBrdcstType to set
     */
    public static void setCurrentBrdcstType(byte currentBrdcstType) {
        RunTime.currentBrdcstType = currentBrdcstType;
    }

    /**
     * @return the currentBrdcstMedium
     */
    public static byte getCurrentBrdcstMedium() {
        return currentBrdcstMedium;
    }

    /**
     * @param currentBrdcstMedium
     *            the currentBrdcstMedium to set
     */
    public static void setCurrentBrdcstMedium(byte currentBrdcstMedium) {
        RunTime.currentBrdcstMedium = currentBrdcstMedium;
    }

}

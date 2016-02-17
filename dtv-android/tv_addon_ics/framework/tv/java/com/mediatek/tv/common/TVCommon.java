package com.mediatek.tv.common;

/**
 * TV system configure
 * 
 */
public class TVCommon {
    // #define BRDCST_TYPE_UNKNOWN ((BRDCST_TYPE_T) 0) /**< */
    // #define BRDCST_TYPE_ANALOG ((BRDCST_TYPE_T) 1) /**< */
    // #define BRDCST_TYPE_DVB ((BRDCST_TYPE_T) 2) /**< */
    // #define BRDCST_TYPE_ATSC ((BRDCST_TYPE_T) 3) /**< */
    // #define BRDCST_TYPE_SCTE ((BRDCST_TYPE_T) 4) /**< */
    // #define BRDCST_TYPE_ISDB ((BRDCST_TYPE_T) 5) /**< */
    // #define BRDCST_TYPE_FMRDO ((BRDCST_TYPE_T) 6) /**< */
    // #define BRDCST_TYPE_DTMB ((BRDCST_TYPE_T) 7) /**< */
    // #define BRDCST_TYPE_MHP ((BRDCST_TYPE_T) 8) /**< */
    // typedef UINT8 BRDCST_MEDIUM_T; /**< */
    // #define BRDCST_MEDIUM_UNKNOWN ((BRDCST_MEDIUM_T) 0) /**< */
    // #define BRDCST_MEDIUM_DIG_TERRESTRIAL ((BRDCST_MEDIUM_T) 1) /**< */
    // #define BRDCST_MEDIUM_DIG_CABLE ((BRDCST_MEDIUM_T) 2) /**< */
    // #define BRDCST_MEDIUM_DIG_SATELLITE ((BRDCST_MEDIUM_T) 3) /**< */
    // #define BRDCST_MEDIUM_ANA_TERRESTRIAL ((BRDCST_MEDIUM_T) 4) /**< */
    // #define BRDCST_MEDIUM_ANA_CABLE ((BRDCST_MEDIUM_T) 5) /**< */
    // #define BRDCST_MEDIUM_ANA_SATELLITE ((BRDCST_MEDIUM_T) 6) /**< */
    // #define BRDCST_MEDIUM_1394 ((BRDCST_MEDIUM_T) 7) /**< */

    public static byte BRDCST_TYPE_UNKNOWN = 0;
    public static byte BRDCST_TYPE_ANALOG = 1;
    public static byte BRDCST_TYPE_DVB = 2;
    public static byte BRDCST_TYPE_ATSC = 3;
    public static byte BRDCST_TYPE_SCTE = 4;
    public static byte BRDCST_TYPE_ISDB = 5;
    public static byte BRDCST_TYPE_FMRDO = 6;
    public static byte BRDCST_TYPE_DTMB = 7;
    public static byte BRDCST_TYPE_MHP = 8;

    public static byte BRDCST_MEDIUM_UNKNOWN = 0;
    public static byte BRDCST_MEDIUM_DIG_TERRESTRIAL = 1;
    public static byte BRDCST_MEDIUM_DIG_CABLE = 2;
    public static byte BRDCST_MEDIUM_DIG_SATELLITE = 3;
    public static byte BRDCST_MEDIUM_ANA_TERRESTRIAL = 4;
    public static byte BRDCST_MEDIUM_ANA_CABLE = 5;
    public static byte BRDCST_MEDIUM_ANA_SATELLITE = 6;
    public static byte BRDCST_MEDIUM_1394 = 7;

    // TUNER_BANDWIDTH_T;
    public static int BW_UNKNOWN = 0;
    public static int BW_6_MHz = 1;
    public static int BW_7_MHz = 2;
    public static int BW_8_MHz = 3;

    // TUNER_MODULATION_T u_tuner.h
    public static int MOD_UNKNOWN = 0;
    public static int MOD_PSK_8 = 1;
    public static int MOD_VSB_8 = 2;
    public static int MOD_VSB_16 = 3;
    public static int MOD_QAM_16 = 4;
    public static int MOD_QAM_32 = 5;
    public static int MOD_QAM_64 = 6;
    public static int MOD_QAM_80 = 7;
    public static int MOD_QAM_96 = 8;
    public static int MOD_QAM_112 = 9;
    public static int MOD_QAM_128 = 10;
    public static int MOD_QAM_160 = 11;
    public static int MOD_QAM_192 = 12;
    public static int MOD_QAM_224 = 13;
    public static int MOD_QAM_256 = 14;
    public static int MOD_QAM_320 = 15;
    public static int MOD_QAM_384 = 16;
    public static int MOD_QAM_448 = 17;
    public static int MOD_QAM_512 = 18;
    public static int MOD_QAM_640 = 19;
    public static int MOD_QAM_768 = 20;
    public static int MOD_QAM_896 = 21;
    public static int MOD_QAM_1024 = 22;
    public static int MOD_QPSK = 23;
    public static int MOD_OQPSK = 24;
    public static int MOD_BPSK = 25;
    public static int MOD_VSB_AM = 26;
    public static int MOD_QAM_4_NR = 27;
    public static int MOD_FM_RADIO = 28;

    public static int debugLevel = 0;
    public static int debugLevelDebug = 1;
    public static int debugLevelError = 2;
    public static int debugLevelInfo = 4;
    public static int debugLevelWarning = 8;
    public static int debugLevelVerbose = 16;

}

package com.mediatek.media;

import android.util.Log;

public class SubtitleAttr {

    private static final int DEFAULTTYPE = 1 << 0;
    private static final int DISPLAYMODE = 1 << 1;
    private static final int HILTSTYLE   = 1 << 2;
    private static final int TIMEOFFSET  = 1 << 3;
    private static final int FONTENC     = 1 << 4;
    private static final int SHOWHIDE    = 1 << 5;
    private static final int FONTINFO    = 1 << 6;
    private static final int BGCOLOR     = 1 << 7;
    private static final int TEXTCOLOR   = 1 << 8;
    private static final int BODERTYPE   = 1 << 9;
    private static final int BORDERWIDTH = 1 << 10;
    private static final int ROLLTYPE    = 1 << 11;
    private static final int DISPLAYRECT = 1 << 12;
    
    public SubtitleAttr() {
        
    }
    
    public class SubtitleDisplayMode {
        
        public static final int OFF           = 0; 
        public static final int SINGLE_LINE   = 1; 
        public static final int MULTI_LINE    = 2; 
        
        private int dispMode;
        private short u2Param;
        
        public SubtitleDisplayMode(int DispMode, short u2Param) {
            super();
            this.dispMode = DispMode;
            this.u2Param = u2Param;
        }
    
    }
    
    public class SubtitleHiltStyle {
        
        public static final int BY_LINE   = 0; 
        public static final int KARAOKE   = 1;  
        
        private int hiltStyle;
        private short u2Param;
        
        public SubtitleHiltStyle(int hiltStyle, short u2Param) {
            super();
            this.hiltStyle = hiltStyle;
            this.u2Param = u2Param;
        }
        
    }
    
    public class SubtitleTimeOffset {
        
        public static final int OFF         = 0;
        public static final int AUTO        = 1;
        public static final int USER_DEF    = 2;
        
        private int timeOffset;
        private int offsetValue;
        
        public SubtitleTimeOffset(int timeOffset, int offsetValue) {
            super();
            this.timeOffset = timeOffset;
            this.offsetValue = offsetValue;
        }

    }
    
    public class SubtitleFontEnc {
        
        public static final int AUTO    = 0; 
        public static final int UTF8    = 1; 
        public static final int UTF16   = 2; 
        public static final int BIG5    = 3; 
        public static final int GB      = 4; 

        private int fontEncType;
        private short u2Param;
        
        public SubtitleFontEnc(int fontEncType, short u2Param) {
            super();
            this.fontEncType = fontEncType;
            this.u2Param = u2Param;
        }
    
    }
    
    public class SubtitleFontInfo {//modify
        
        /* font size */
        public static final int SIZE_SMALL    = 0;
        public static final int SIZE_MEDIUM   = 1;
        public static final int SIZE_LARGE    = 2;
        public static final int SIZE_CUSTOM   = 3;
        public static final int SIZE_NUMBER   = 4;
 
        /* font style */
        
        public static final int STYLE_REGULAR       = 0; 
        public static final int STYLE_ITALIC        = 1; 
        public static final int STYLE_BOLD          = 2; 
        public static final int STYLE_UNDERLINE     = 3; 
        public static final int STYLE_STRIKEOUT     = 4; 
        public static final int STYLE_OUTLINE       = 5; 
        public static final int STYLE_SHADOW_RIGHT  = 6; 
        public static final int STYLE_SHADOW_LEFT   = 7; 
        public static final int STYLE_DEPRESSED     = 8; 
        public static final int STYLE_RAISED        = 9; 
        public static final int STYLE_UNIFORM       = 10;
        public static final int STYLE_BLURRED       = 11;

        /* cmap encoding */
        
        public static final int ENCODING_NONE             = 0;     
        public static final int ENCODING_MS_SYMBOL        = 1;     
        public static final int ENCODING_UNICODE          = 2;     
        public static final int ENCODING_SJIS             = 3;     
        public static final int ENCODING_GB2312           = 4;     
        public static final int ENCODING_BIG5             = 5;    
        public static final int ENCODING_WANSUNG          = 6;      
        public static final int ENCODING_JOHAB            = 7;      
        public static final int ENCODING_ADOBE_STANDARD   = 8;     
        public static final int ENCODING_ADOBE_EXPERT     = 9;    
        public static final int ENCODING_ADOBE_CUSTOM     = 10;   
        public static final int ENCODING_ADOBE_LATIN_1    = 11;     
        public static final int ENCODING_OLD_LATIN_2      = 12;     
        public static final int ENCODING_APPLE_ROMAN      = 13;     

        private int fontSize;
        private int fontStyle;
        private int cmapEncoding;
        private String fontName;
        private short width;
        private byte customSize;
        
        public SubtitleFontInfo(int fontSize, int fontStyle, int cmapEncoding,
                String fontName, short width, byte customSize) {
            super();
            this.fontSize = fontSize;
            this.fontStyle = fontStyle;
            this.cmapEncoding = cmapEncoding;
            this.fontName = fontName;
            this.width = width;
            this.customSize = customSize;
        }
    
    }
    
    public class SubtitleColor {
        private byte a;
        private byte r;
        private byte g;
        private byte b;
        
        public SubtitleColor(byte a, byte r, byte g, byte b) {
            super();
            this.a = a;
            this.r = r;
            this.g = g;
            this.b = b;
        }                
    }
    
        /* Subtitle boderType  */
    
       public static final int SBTL_BDR_TYPE_NULL         = 0; 
       public static final int SBTL_BDR_TYPE_SOLID_LINE   = 1; 

        /* Subtitle rollType  */
    
       public static final int SBTL_ROLL_TYPE_DEF = 0;
    
    
    public class SubtitleDisplayRect {
        
        private   int   u4X; 
        private   int   u4Y; 
        private   int   u4W; 
        private   int   u4H;
        
        public SubtitleDisplayRect(int u4x, int u4y, int u4w, int u4h) {
            super();
            u4X = u4x;
            u4Y = u4y;
            u4W = u4w;
            u4H = u4h;
        } 
    
    }

    private int mask = 0;
    private SubtitleDisplayMode displayMode; 
    private SubtitleHiltStyle hiltStyle;   
    private SubtitleTimeOffset timeOffset;  
    private SubtitleFontEnc fontEnc;     
    private boolean showHide;
    private SubtitleFontInfo fontInfo;     
    private SubtitleColor bgColor;
    private SubtitleColor textColor;
    private int boderType;
    private int borderWidth;
    private int rollType;  
    private SubtitleDisplayRect displayRect; 
    
    public void setDefaultType() {
    	Log.v("SubtitleAttrTAG", "setDefaultType: before    mask = " + mask);
        mask |= DEFAULTTYPE;
        Log.v("SubtitleAttrTAG", "setDefaultType: after     mask = " + mask);
    }


    public void setDisplayMode(SubtitleDisplayMode displayMode) {
        this.displayMode = displayMode;
        Log.v("SubtitleAttrTAG", "setDisplayMode: before    mask = " + mask);
        mask |= DISPLAYMODE;
        Log.v("SubtitleAttrTAG", "setDisplayMode: after     mask = " + mask);
    }


    public void setHiltStyle(SubtitleHiltStyle hiltStyle) {
        this.hiltStyle = hiltStyle;
        Log.v("SubtitleAttrTAG", "setHiltStyle: before   mask = " + mask);
        mask |= HILTSTYLE;
        Log.v("SubtitleAttrTAG", "setHiltStyle: after    mask = " + mask);
    }


    public void setTimeOffset(SubtitleTimeOffset timeOffset) {
        this.timeOffset = timeOffset;
        Log.v("SubtitleAttrTAG", "setTimeOffset: before   mask = " + mask);
        mask |= TIMEOFFSET;
        Log.v("SubtitleAttrTAG", "setTimeOffset: after    mask = " + mask);
    }


    public void setFontEnc(SubtitleFontEnc fontEnc) {
        this.fontEnc = fontEnc;
        Log.v("SubtitleAttrTAG", "setFontEnc: before  mask = " + mask);
        mask |= FONTENC;
        Log.v("SubtitleAttrTAG", "setFontEnc: after   mask = " + mask);
    }

    
    public void setShowHide(boolean showHide) {
        this.showHide = showHide;
        Log.v("SubtitleAttrTAG", "setShowHide: before  mask = " + mask);
        mask |= SHOWHIDE;
        Log.v("SubtitleAttrTAG", "setShowHide: after   mask = " + mask);
    }


    public void setFontInfo(SubtitleFontInfo fontInfo) {
        this.fontInfo = fontInfo;
        Log.v("SubtitleAttrTAG", "setFontInfo: before  mask = " + mask);
        mask |= FONTINFO;
        Log.v("SubtitleAttrTAG", "setFontInfo: after   mask = " + mask);
    }


    public void setBgColor(SubtitleColor bgColor) {
        this.bgColor = bgColor;
        Log.v("SubtitleAttrTAG", "setBgColor: before  mask = " + mask);
        mask |= BGCOLOR;
        Log.v("SubtitleAttrTAG", "setBgColor: after   mask = " + mask);
    }

    public void setTextColor(SubtitleColor textColor) {
        this.textColor = textColor;
        Log.v("SubtitleAttrTAG", "setTextColor: before  mask = " + mask);
        mask |= TEXTCOLOR;
        Log.v("SubtitleAttrTAG", "setTextColor: after   mask = " + mask);
    }

    public void setBoderType(int boderType) {
        this.boderType = boderType;
        Log.v("SubtitleAttrTAG", "setBoderType: before  mask = " + mask);
        mask |= BODERTYPE;
        Log.v("SubtitleAttrTAG", "setBoderType: after   mask = " + mask);
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        Log.v("SubtitleAttrTAG", "setBorderWidth: before mask = " + mask);
        mask |= BORDERWIDTH;
        Log.v("SubtitleAttrTAG", "setBorderWidth: after  mask = " + mask);
    }
    
    public void setRollType(int rollType) {
        this.rollType = rollType;
        Log.v("SubtitleAttrTAG", "setRollType: before mask = " + mask);
        mask |= ROLLTYPE;
        Log.v("SubtitleAttrTAG", "setRollType: after  mask = " + mask);
    }

    public void setDisplayRect(SubtitleDisplayRect displayRect) {
        this.displayRect = displayRect;
        Log.v("SubtitleAttrTAG", "setDisplayRect: before mask = " + mask);
        mask |= DISPLAYRECT;
        Log.v("SubtitleAttrTAG", "setDisplayRect: after  mask = " + mask);
    }

    public boolean isValid() {
    	Log.v("SubtitleAttrTAG", "isValid: mask = " + mask);
        return mask != 0;
    }

}

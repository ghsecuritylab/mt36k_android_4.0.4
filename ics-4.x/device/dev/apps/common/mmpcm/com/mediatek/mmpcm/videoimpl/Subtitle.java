package com.mediatek.mmpcm.videoimpl;

public class Subtitle {
    /* subtitle display mode */
    public static final int DISPLAY_MODE_OFF = 0;
    public static final int DISPLAY_MODE_SINGLE_LINE = 1;
    public static final int DISPLAY_MODE_MULTI_LINE = 2;

    /* subtitle high light style */
    public static final int HIGH_LIGHT_STYLE_BY_LINE = 0;
    public static final int HIGH_LIGHT_STYLE_KARAOKE = 1;

    /* subtitle time offset */
    public static final int TIME_OFFSET_OFF = 0;
    public static final int TIME_OFFSET_AUTO = 1;
    public static final int TIME_OFFSET_DEF = 2;

    /* subtitle font encode */
    public static final int FONT_ENCODE_AUTO = 0;
    public static final int FONT_ENCODE_UTF8 = 1;
    public static final int FONT_ENCODE_UTF16 = 2;
    public static final int FONT_ENCODE_BIG5 = 3;
    public static final int FONT_ENCODE_GB = 4;

    /* subtitle font size */
    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_MEDIUM = 1;
    public static final int FONT_SIZE_LARGE = 2;
    public static final int FONT_SIZE_CUSTOM = 3;
    public static final int FONT_SIZE_NUMBER = 4;

    /* subtitle font style */
    public static final int FONT_STYLE_REGULAR = 0;
    public static final int FONT_STYLE_ITALIC = 1;
    public static final int FONT_STYLE_BOLD = 2;
    public static final int FONT_STYLE_UNDERLINE = 3;
    public static final int FONT_STYLE_STRIKEOUT = 4;
    public static final int FONT_STYLE_OUTLINE = 5;
    public static final int FONT_STYLE_SHADOW_RIGHT = 6;
    public static final int FONT_STYLE_SHADOW_LEFT = 7;
    public static final int FONT_STYLE_DEPRESSED = 8;
    public static final int FONT_STYLE_RAISED = 9;
    public static final int FONT_STYLE_UNIFORM = 10;
    public static final int FONT_STYLE_BLURRED = 11;

    /* subtitle cmap encoding */
    public static final int FONT_ENCODING_NONE = 0;
    public static final int FONT_ENCODING_MS_SYMBOL = 1;
    public static final int FONT_ENCODING_UNICODE = 2;
    public static final int FONT_ENCODING_SJIS = 3;
    public static final int FONT_ENCODING_GB2312 = 4;
    public static final int FONT_ENCODING_BIG5 = 5;
    public static final int FONT_ENCODING_WANSUNG = 6;
    public static final int FONT_ENCODING_JOHAB = 7;
    public static final int FONT_ENCODING_ADOBE_STANDARD = 8;
    public static final int FONT_ENCODING_ADOBE_EXPERT = 9;
    public static final int FONT_ENCODING_ADOBE_CUSTOM = 10;
    public static final int FONT_ENCODING_ADOBE_LATIN_1 = 11;
    public static final int FONT_ENCODING_OLD_LATIN_2 = 12;
    public static final int FONT_ENCODING_APPLE_ROMAIN = 13;

    /* subtitle boder type */
    public static final int BDR_TYPE_NULL = 0;
    public static final int BDR_TYPE_SOLID_LINE = 1;

    /* subtitle roll type */
    public static final int ROLL_TYPE_DEF = 0;
    
    
    public class FontInfo {
        public int fontSize;
        public int fontStyle;
        public int cmapEncoding;
        public String fontName;
        public short width;
        public byte customSize;
    }
    
    public class Color{
        public byte alpha;
        public byte r;
        public byte g;
        public byte b;
    }
}
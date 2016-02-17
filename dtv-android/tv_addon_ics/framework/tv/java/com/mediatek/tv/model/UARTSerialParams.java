package com.mediatek.tv.model;

@SuppressWarnings("unused")
public class UARTSerialParams {
    /**
     * definition of operation mode of UART Serial Port
     */
    public static final int IOM_UART_OPERATION_MODE_NORMAL = (1 << 0);
    public static final int IOM_UART_OPERATION_MODE_TRANSPARENT = (1 << 1);
    public static final int IOM_UART_OPERATION_MODE_CLI = (1 << 2);
    public static final int IOM_UART_OPERATION_MODE_FACTORY = (1 << 3);

    /**
     * definition of UART Serial Port ID
     */

    private static final int IOM_UART_SERIAL_ID_0 = 0;
    public static final int IOM_UART_SERIAL_ID_1 = 1;
    private static final int IOM_UART_SERIAL_ID_2 = 2;
    public static final int IOM_UART_SERIAL_ID_FACTORY = 7;

    /**
     * definition of speed of UART Serial Port
     */
    public static final int IOM_UART_SPEED_75 = 0;
    public static final int IOM_UART_SPEED_110 = 1;
    public static final int IOM_UART_SPEED_134 = 2;
    public static final int IOM_UART_SPEED_150 = 3;
    public static final int IOM_UART_SPEED_300 = 4;
    public static final int IOM_UART_SPEED_600 = 5;
    public static final int IOM_UART_SPEED_1200 = 6;
    public static final int IOM_UART_SPEED_1800 = 7;
    public static final int IOM_UART_SPEED_2400 = 8;
    public static final int IOM_UART_SPEED_4800 = 9;
    public static final int IOM_UART_SPEED_9600 = 10;
    public static final int IOM_UART_SPEED_14400 = 11;
    public static final int IOM_UART_SPEED_19200 = 12;
    public static final int IOM_UART_SPEED_38400 = 13;
    public static final int IOM_UART_SPEED_57600 = 14;
    public static final int IOM_UART_SPEED_115200 = 15;
    public static final int IOM_UART_SPEED_128000 = 16;
    public static final int IOM_UART_SPEED_230400 = 17;
    public static final int IOM_UART_SPEED_460800 = 18;
    public static final int IOM_UART_SPEED_921600 = 19;

    /**
     * definition of data length of UART Serial Port
     */
    public static final int IOM_UART_DATA_LEN_4 = 0;
    public static final int IOM_UART_DATA_LEN_5 = 1;
    public static final int IOM_UART_DATA_LEN_6 = 2;
    public static final int IOM_UART_DATA_LEN_7 = 3;
    public static final int IOM_UART_DATA_LEN_8 = 4;
    public static final int IOM_UART_DATA_LEN_9 = 5;

    /**
     * definition of parity of UART Serial Port
     */
    public static final int IOM_UART_PARITY_NONE = 0;
    public static final int IOM_UART_PARITY_EVEN = 1;
    public static final int IOM_UART_PARITY_ODD = 2;
    public static final int IOM_UART_PARITY_MARK = 3;
    public static final int IOM_UART_PARITY_SPACE = 4;

    /**
     * definition of stop bit of UART Serial Port
     */
    public static final int IOM_UART_STOP_BIT_1 = 0;
    public static final int IOM_UART_STOP_BIT_1_5 = 1;
    public static final int IOM_UART_STOP_BIT_2 = 2;

    /**
     * definition of notify code of UART Serial Port
     */
    public static final int IOM_NFY_COND_NONE = 0;
    public static final int IOM_NFY_COND_BTN_DOWN = 1;
    public static final int IOM_NFY_COND_BTN_UP = 2;
    public static final int IOM_NFY_COND_BTN_REPEAT = 3;
    public static final int IOM_NFY_COND_REC_DBG_DATA = 4;
    public static final int IOM_NFY_COND_REC_UART_DATA = 5;
    public static final int IOM_NFY_COND_XMT_COMPLETE = 6;
    public static final int IOM_NFY_COND_AUTO_SLEEP_TIMER_RESET = 7;
    public static final int IOM_NFY_COND_AUTO_SLEEP_PROMPT = 8;
    public static final int IOM_NFY_COND_AUTO_SLEEP_TIMEOUT = 9;
}

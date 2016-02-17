package com.mediatek.tv.common;

/**
 * This class provides TV manager common exception
 * 
 */
public class TVMException extends Exception {
    private static final long serialVersionUID = 1L;
    private int errorCode;
    private String errorMessage;

    public TVMException() {
        super();
    }

    /**
     * Construct
     * 
     * @param errorMessage
     *            error message
     * 
     */
    public TVMException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    /**
     * Construct
     * 
     * @param errorCode
     *            error code
     * @param errorMessage
     *            error message
     * 
     */
    public TVMException(int errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Get error code
     * 
     * 
     * @return int error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Get error message
     * 
     * 
     * @return String error message
     */
    public String getErrorMessage() {
        return errorMessage;// + getTraceInfo();
    }

    public String toString() {
        return "Error Code=" + this.errorCode + //
                " \tError Message=" + this.errorMessage;
    }
    // private static String getTraceInfo() {
    // StringBuffer sb = new StringBuffer();
    // StackTraceElement[] stacks = new Throwable().getStackTrace();
    // // int stackLen = stacks.length;
    // sb.append("class: ")//
    // .append(stacks[1].getClassName())//
    // .append("; method ")//
    // .append(stacks[1].getMethodName())//
    // .append(";number: ")//
    // .append(stacks[1].getLineNumber());
    // return sb.toString();
    // }

}

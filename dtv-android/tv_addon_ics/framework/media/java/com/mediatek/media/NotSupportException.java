package com.mediatek.media;

public class NotSupportException  extends RuntimeException {
    
     /**
     * Constructs a new {@code IllegalStateException} that includes the current
     * stack trace.
     */
    public NotSupportException() {
        super();
    }

    /**
     * Constructs a new {@code IllegalStateException} with the current stack
     * trace and the specified detail message.
     * 
     * @param detailMessage
     *            the detail message for this exception.
     */
    public NotSupportException(String detailMessage) {
        super(detailMessage);
    }
    
    /**
     * Constructs a new {@code IllegalStateException} with the current stack
     * trace, the specified detail message and the specified cause.
     * 
     * @param message
     *            the detail message for this exception.
     * @param cause
     *            the cause of this exception.
     * @since 1.5
     */
    public NotSupportException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new {@code IllegalStateException} with the current stack
     * trace and the specified cause.
     * 
     * @param cause
     *            the cause of this exception, may be {@code null}.
     * @since 1.5
     */
    public NotSupportException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }

}

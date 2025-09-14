package org.example.bookstore.exception;

/**
 * Common exception class for all bookstore exceptions.
 */
public class BookstoreRuntimeException extends RuntimeException {
    public BookstoreRuntimeException() {
        super();
    }

    public BookstoreRuntimeException(String message) {
        super(message);
    }

    public BookstoreRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookstoreRuntimeException(Throwable cause) {
        super(cause);
    }

    protected BookstoreRuntimeException(String message, Throwable cause, boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

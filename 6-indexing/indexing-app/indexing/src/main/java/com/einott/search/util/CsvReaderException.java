package com.einott.search.util;

public class CsvReaderException extends RuntimeException {

    public CsvReaderException() {
    }

    public CsvReaderException(String message) {
        super(message);
    }

    public CsvReaderException(Throwable cause) {
        super(cause);
    }

    public CsvReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}

package com.ljc.seatunnel.datasource.client.exception;

public class DataSourceSDKException extends RuntimeException {

    public DataSourceSDKException(String message) {
        super(message);
    }

    public DataSourceSDKException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceSDKException(Throwable cause) {
        super(cause);
    }

    public DataSourceSDKException(String message, Object... args) {
        super(String.format(message, args));
    }
}

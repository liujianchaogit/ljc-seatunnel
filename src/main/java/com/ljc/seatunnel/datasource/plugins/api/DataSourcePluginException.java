package com.ljc.seatunnel.datasource.plugins.api;

public class DataSourcePluginException extends RuntimeException {

    public DataSourcePluginException(String message) {
        super(message);
    }

    public DataSourcePluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourcePluginException(Throwable cause) {
        super(cause);
    }

    public DataSourcePluginException() {
        super();
    }
}

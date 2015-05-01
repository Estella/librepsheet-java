package com.repsheet.librepsheet;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class RepsheetConnectionException extends JedisConnectionException {
    public RepsheetConnectionException(String message) {
        super(message);
    }

    public RepsheetConnectionException(Throwable cause) {
        super(cause);
    }

    public RepsheetConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
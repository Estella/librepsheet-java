package com.repsheet.librepsheet;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class RepsheetConnectionException extends JedisConnectionException {
    public RepsheetConnectionException(final String message) {
        super(message);
    }

    public RepsheetConnectionException(final Throwable cause) {
        super(cause);
    }

    public RepsheetConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

package com.repsheet.librepsheet;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Connection {
    private final String host;
    private final JedisPool pool;

    public Connection(final String host) {
        this.host = host;
        this.pool = new JedisPool(new JedisPoolConfig(), this.host);
    }

    public final JedisPool getPool() {
        return this.pool;
    }
}

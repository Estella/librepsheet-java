package com.repsheet.librepsheet;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Connection {
    private final String host;
    private final Integer port;
    private final Integer timeout;
    private final JedisPool pool;

    public Connection(String host, Integer port, Integer timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.pool = new JedisPool(new JedisPoolConfig(), this.host);
    }

    public JedisPool getPool() {
        return this.pool;
    }
}

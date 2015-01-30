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

    public Actor lookup(final Connection connection, final Actor.Type type, final String value) {
        Actor actor;

        actor = Actor.query(connection, type, value, Actor.Status.WHITELISTED);
        if (actor.getStatus() == Actor.Status.WHITELISTED) {
            return actor;
        }

        actor = Actor.query(connection, type, value, Actor.Status.BLACKLISTED);
        if (actor.getStatus() == Actor.Status.BLACKLISTED) {
            return actor;
        }

        actor = Actor.query(connection, type, value, Actor.Status.MARKED);
        if (actor.getStatus() == Actor.Status.MARKED) {
            return actor;
        }

        return new Actor(type, value, Actor.Status.OK, null);
    }
}

package com.repsheet.librepsheet;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Connection {
    public enum Status { OK, ERROR }

    private final String host;
    private final JedisPool pool;

    public Connection(final String host) {
        this.host = host;
        this.pool = new JedisPool(new JedisPoolConfig(), this.host);
    }

    public final JedisPool getPool() {
        return this.pool;
    }

    public final Actor lookup(final Actor.Type type, final String value) {
        Actor actor;

        actor = Actor.query(pool, type, value, Actor.Status.WHITELISTED);
        if (actor.getStatus() == Actor.Status.WHITELISTED) {
            return actor;
        }

        actor = Actor.query(pool, type, value, Actor.Status.BLACKLISTED);
        if (actor.getStatus() == Actor.Status.BLACKLISTED) {
            return actor;
        }

        actor = Actor.query(pool, type, value, Actor.Status.MARKED);
        if (actor.getStatus() == Actor.Status.MARKED) {
            return actor;
        }

        return new Actor(type, value, Actor.Status.OK, null);
    }

    public final Status blacklist(final String actor, final Actor.Type type, final String reason) {
        String keyspace = Util.stringFromType(type);
        try (Jedis jedis = pool.getResource()) {
            if (keyspace == null) {
                return Status.ERROR;
            } else {
                jedis.set(actor + ":repsheet:" + keyspace + ":blacklisted", reason);
                return Status.OK;
            }
        }
    }

    public final Status blacklist(final String actor, final Actor.Type type, final String reason, final int expiry) {
        String keyspace = Util.stringFromType(type);
        try (Jedis jedis = pool.getResource()) {
            if (keyspace == null) {
                return Status.ERROR;
            } else {
                jedis.setex(actor + ":repsheet:" + keyspace + ":blacklisted", expiry, reason);
                return Status.OK;
            }
        }
    }

    public final Status whitelist(final String actor, final Actor.Type type, final String reason) {
        String keyspace = Util.stringFromType(type);
        try (Jedis jedis = pool.getResource()) {
            if (keyspace == null) {
                return Status.ERROR;
            } else {
                jedis.set(actor + ":repsheet:" + keyspace + ":whitelisted", reason);
                return Status.OK;
            }
        }
    }

    public final Status whitelist(final String actor, final Actor.Type type, final String reason, final int expiry) {
        String keyspace = Util.stringFromType(type);
        try (Jedis jedis = pool.getResource()) {
            if (keyspace == null) {
                return Status.ERROR;
            } else {
                jedis.setex(actor + ":repsheet:" + keyspace + ":whitelisted", expiry, reason);
                return Status.OK;
            }
        }
    }

    public final Status mark(final String actor, final Actor.Type type, final String reason) {
        String keyspace = Util.stringFromType(type);
        try (Jedis jedis = pool.getResource()) {
            if (keyspace == null) {
                return Status.ERROR;
            } else {
                jedis.set(actor + ":repsheet:" + keyspace + ":marked", reason);
                return Status.OK;
            }
        }
    }

    public final Status mark(final String actor, final Actor.Type type, final String reason, final int expiry) {
        String keyspace = Util.stringFromType(type);
        try (Jedis jedis = pool.getResource()) {
            if (keyspace == null) {
                return Status.ERROR;
            } else {
                jedis.setex(actor + ":repsheet:" + keyspace + ":marked", expiry, reason);
                return Status.OK;
            }
        }
    }
}

package com.repsheet.librepsheet;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;


public final class Actor {
    public enum Type { IP, USER, CIDR }
    public enum Status { MARKED, WHITELISTED, BLACKLISTED, OK }

    private final Type type;
    private final String value;
    private final Status status;
    private final String reason;

    public Actor(final Type type, final String value, final Status status, final String reason) {
        this.type = type;
        this.value = value;
        this.status = status;
        this.reason = reason;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Status getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public Long getTTL(final Connection connection) {
        try (Jedis jedis = connection.getPool().getResource()) {
            return jedis.ttl(value + ":repsheet:" + Util.stringFromType(type) + ":" + Util.keyspaceFromStatus(status));
        }
    }

    public static Actor query(final JedisPool connection, final Type type, final String value, final Status status) {
        String keyspace = Util.keyspaceFromStatus(status);

        switch (type) {
            case IP:
                try (Jedis jedis = connection.getResource()) {
                    String reply = jedis.get(value + ":repsheet:ip:" + keyspace);
                    if (reply != null) {
                        return new Actor(type, value, status, reply);
                    }

                    Set<String> blocks = jedis.keys("*:repsheet:cidr:" + keyspace);
                    BulkCIDRProcessor processor = new BulkCIDRProcessor(blocks, value);
                    ForkJoinPool pool = new ForkJoinPool();
                    pool.execute(processor);
                    List<String> results = processor.join();
                    pool.shutdown();
                    if (!results.isEmpty()) {
                        return new Actor(type, value, status, jedis.get(results.get(0) + ":repsheet:cidr:" + keyspace));
                    }
                }
                break;
            case USER:
                try (Jedis jedis = connection.getResource()) {
                    String reply = jedis.get(value + ":repsheet:users:" + keyspace);
                    if (reply != null) {
                        return new Actor(type, value, status, reply);
                    }
                }
                break;
            default:
                break;
        }

        return new Actor(type, value, Status.OK, null);
    }
}

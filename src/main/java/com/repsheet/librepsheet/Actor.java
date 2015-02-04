package com.repsheet.librepsheet;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.ipfilter.CIDR;
import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;


public final class Actor {
    public enum Type { IP, USER, USERAGENT }
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

    public static Actor query(final Connection connection, final Type type, final String value, final Status status) {
        String keyspace = keyspaceFromStatus(status);

        switch (type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(value + ":repsheet:ip:" + keyspace);
                    if (reply != null) {
                        return new Actor(type, value, status, reply);
                    }

                    Set<String> blocks = jedis.keys("*:repsheet:cidr:" + keyspace);
                    BulkCIDRProcessor processor = new BulkCIDRProcessor(blocks, value);
                    ForkJoinPool pool = new ForkJoinPool();
                    pool.execute(processor);
                    List<String> results = processor.join();
                    if (!results.isEmpty()) {
                        return new Actor(type, value, status, jedis.get(results.get(0) + ":repsheet:cidr:" + keyspace));
                    }
                }
                break;
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(value + ":repsheet:users:" + keyspace);
                    if (reply != null) {
                        return new Actor(type, value, status, reply);
                    }
                }
                break;
            case USERAGENT:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(value + ":repsheet:useragents:" + keyspace);
                    if (reply != null) {
                        return new Actor(type, value, status, reply);
                    }
                }
            default:
                break;
        }

        return new Actor(type, value, Status.OK, null);
    }

    private static String keyspaceFromStatus(final Status status) {
        switch (status) {
            case WHITELISTED:
                return "whitelisted";
            case BLACKLISTED:
                return "blacklisted";
            case MARKED:
                return "marked";
            default:
                return null;
        }
    }
}

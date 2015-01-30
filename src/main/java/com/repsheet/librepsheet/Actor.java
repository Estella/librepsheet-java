package com.repsheet.librepsheet;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.ipfilter.CIDR;
import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;


public final class Actor {
    public enum Type { IP, USER, USERAGENT }
    public enum Status { MARKED, WHITELISTED, BLACKLISTED, OK }

    private static final int KEYSPACELENGTH = 3;
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
        switch (type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(value + ":repsheet:ip:" + keyspaceFromStatus(status));
                    if (reply != null) {
                        return new Actor(type, value, status, reply);
                    }

                    Set<String> blocks = jedis.keys("*:repsheet:cidr:" + keyspaceFromStatus(status));
                    for (String s : blocks) {
                        try {
                            String[] parts = s.split(":");
                            String block = StringUtils.join(Arrays.asList(parts).subList(0, parts.length - KEYSPACELENGTH), ":");
                            CIDR cidr = CIDR.newCIDR(block);
                            InetAddress address = InetAddress.getByName(value);
                            if (cidr.contains(address)) {
                                return new Actor(type, value, status, jedis.get(s));
                            }
                        } catch (UnknownHostException e) {
                            // TODO: figure out what should actually happen here. Probably log, but no logging infrastructure is setup at the moment
                        }
                    }
                }
                break;
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(value + ":repsheet:users:" + keyspaceFromStatus(status));
                    if (reply != null) {
                        return new Actor(type, value, status, reply);
                    }
                }
                break;
            case USERAGENT:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(value + ":repsheet:useragents:" + keyspaceFromStatus(status));
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

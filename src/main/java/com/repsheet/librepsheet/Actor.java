package com.repsheet.librepsheet;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.ipfilter.CIDR;
import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;


public final class Actor {
    public enum Type { IP, USER }
    public enum Status { MARKED, WHITELISTED, BLACKLISTED, OK }

    private static final int KEYSPACELENGTH = 3;
    private final Type type;
    private final String value;
    private Status status = Status.OK;
    private String reason = null;

    private Actor(final Type type, final String value) {
        this.type = type;
        this.value = value;
    }


    public Status getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public static Actor lookup(final Connection connection, final Type type, final String value) {
        Actor actor = new Actor(type, value);

        query(connection, actor, Status.WHITELISTED);
        if (actor.reason != null) {
            actor.status = Status.WHITELISTED;
            return actor;
        }

        query(connection, actor, Status.BLACKLISTED);
        if (actor.reason != null) {
            actor.status = Status.BLACKLISTED;
            return actor;

        }

        query(connection, actor, Status.MARKED);
        if (actor.reason != null) {
            actor.status = Status.MARKED;
            return actor;
        }

        actor.status = Status.OK;

        return actor;
    }

    private static void query(final Connection connection, final Actor actor, final Status status) {
        switch (actor.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:ip:" + keyspaceFromStatus(status));
                    if (reply != null) {
                        actor.reason = reply;
                    }

                    Set<String> blocks = jedis.keys("*:repsheet:cidr:" + keyspaceFromStatus(status));
                    for (String s : blocks) {
                        try {
                            String[] parts = s.split(":");
                            String block = StringUtils.join(Arrays.asList(parts).subList(0, parts.length - KEYSPACELENGTH), ":");
                            CIDR cidr = CIDR.newCIDR(block);
                            InetAddress address = InetAddress.getByName(actor.value);
                            if (cidr.contains(address)) {
                                actor.reason = jedis.get(s);
                                actor.status = status;
                                return;
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:users:" + keyspaceFromStatus(status));
                    if (reply != null) {
                        actor.reason = reply;
                    }
                }
            default:
                break;
        }
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

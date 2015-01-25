package com.repsheet.librepsheet;

import redis.clients.jedis.Jedis;

public class Actor {
    public enum Type { IP, USER };
    public enum Status { MARKED, WHITELISTED, BLACKLISTED, OK }

    private final Type type;
    private final String value;
    private Status status = Status.OK;
    private String reason = null;

    private Actor(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Status getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public static Actor lookup(Connection connection, Type type, String value) {
        Actor actor = new Actor(type, value);

        isWhitelisted(connection, actor);
        if (actor.reason != null) {
            actor.status = Status.WHITELISTED;
            return actor;
        }

        isBlacklisted(connection, actor);
        if (actor.reason != null) {
            actor.status = Status.BLACKLISTED;
            return actor;

        }

        isMarked(connection, actor);
        if (actor.reason != null) {
            actor.status = Status.MARKED;
            return actor;
        }

        actor.status = Status.OK;

        return actor;
    }

    private static void isWhitelisted(Connection connection, Actor actor) {
        switch(actor.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:ip:whitelist");
                    if (reply != null) {
                        actor.reason = reply;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:users:whitelist");
                    if (reply != null) {
                        actor.reason = reply;
                    }
                }
        }
    }

    private static void isBlacklisted(Connection connection, Actor actor) {
        switch (actor.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:ip:blacklist");
                    if (reply != null) {
                        actor.reason = reply;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:users:blacklist");
                    if (reply != null) {
                        actor.reason = reply;
                    }
                }
        }
    }

    private static void isMarked(Connection connection, Actor actor) {
        switch (actor.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:ip");
                    if (reply != null) {
                        actor.reason = reply;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(actor.value + ":repsheet:users");
                    if (reply != null) {
                        actor.reason = reply;
                    }
                }
        }
    }
}
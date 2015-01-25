package com.repsheet.librepsheet;

import redis.clients.jedis.Jedis;

public class Actor {
    public enum Type { CIDR, IP, USER };
    public enum Status { MARKED, WHITELISTED, BLACKLISTED, OK }

    private final Type type;
    private final String value;

    public Actor(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public ActorStatus lookup(Connection connection) {
        String reason;

        reason = isWhitelisted(connection);
        if (reason != null) {
            return new ActorStatus(Status.WHITELISTED, reason);
        }

        reason = isBlacklisted(connection);
        if (reason != null) {
            return new ActorStatus(Status.BLACKLISTED, reason);
        }

        reason = isMarked(connection);
        if (reason != null) {
            return new ActorStatus(Status.MARKED, reason);
        }

        return new ActorStatus(Status.OK, null);
    }

    public String isWhitelisted(Connection connection) {
        switch(this.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(this.value + ":repsheet:ip:whitelist");
                    if (reply != null) {
                        return reply;
                    } else {
                        return null;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply = jedis.get(this.value + ":repsheet:users:whitelist");
                    if (reply != null) {
                        return reply;
                    } else {
                        return null;
                    }
                }
            case CIDR:
                throw new UnsupportedOperationException("CIDR support is not yet implemented");
            default:
                return null;
        }
    }

    public String isBlacklisted(Connection connection) {
        switch (this.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply;
                    reply = jedis.get(this.value + ":repsheet:ip:blacklist");
                    if (reply != null) {
                        return reply;
                    } else {
                        return null;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply;
                    reply = jedis.get(this.value + ":repsheet:users:blacklist");
                    if (reply != null) {
                        return reply;
                    } else {
                        return null;
                    }
                }
            case CIDR:
                throw new UnsupportedOperationException("CIDR support is not yet implemented");
            default:
                return null;
        }
    }

    public String isMarked(Connection connection) {
        switch (this.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply;
                    reply = jedis.get(this.value + ":repsheet:ip");
                    if (reply != null) {
                        return reply;
                    } else {
                        return null;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    String reply;
                    reply = jedis.get(this.value + ":repsheet:users");
                    if (reply != null) {
                        return reply;
                    } else {
                        return null;
                    }
                }
            case CIDR:
                throw new UnsupportedOperationException("CIDR support is not yet implemented");
            default:
                return null;
        }
    }
}
package com.repsheet.librepsheet;

import redis.clients.jedis.Jedis;

public class Actor {
    public enum ActorType { CIDR, IP, USER };
    public enum ActorStatus { MARKED, WHITELISTED, BLACKLISTED, OK }

    private final ActorType type;
    private final String value;

    public Actor(ActorType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ActorStatus status(Connection connection) {
        if (isWhitelisted(connection)) {
            return ActorStatus.WHITELISTED;
        } else if (isBlacklisted(connection)) {
            return ActorStatus.BLACKLISTED;
        } else if (isMarked(connection)) {
            return ActorStatus.MARKED;
        }

        return ActorStatus.OK;
    }

    public boolean isWhitelisted(Connection connection) {
        switch(this.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    if (jedis.exists(this.value + ":repsheet:ip:whitelist")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    if (jedis.exists(this.value + ":repsheet:users:whitelist")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case CIDR:
                throw new UnsupportedOperationException("CIDR support is not yet implemented");
            default:
                return false;
        }
    }

    public boolean isBlacklisted(Connection connection) {
        switch (this.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    if (jedis.exists(this.value + ":repsheet:ip:blacklist")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    if (jedis.exists(this.value + ":repsheet:users:blacklist")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case CIDR:
                throw new UnsupportedOperationException("CIDR support is not yet implemented");
            default:
                return false;
        }
    }

    public boolean isMarked(Connection connection) {
        switch (this.type) {
            case IP:
                try (Jedis jedis = connection.getPool().getResource()) {
                    if (jedis.exists(this.value + ":repsheet:ip")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case USER:
                try (Jedis jedis = connection.getPool().getResource()) {
                    if (jedis.exists(this.value + ":repsheet:users")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case CIDR:
                throw new UnsupportedOperationException("CIDR support is not yet implemented");
            default:
                return false;
        }
    }
}

package com.repsheet.librepsheet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import redis.clients.jedis.Jedis;

public class ActorTest {
    private Connection connection;

    @Before
    public void setUp() {
        connection = new Connection("localhost", 6379, 5);
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.flushDB();
        }
    }

    @Test
    public void testIsWhitelistedReturnsReasonWhenIPWhitelisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:whitelist", "test");
        }
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        assertEquals("test", actor.isWhitelisted(connection));
    }

    @Test
    public void testIsWhitelistedReturnsNullWhenIPNotWhitelisted() {
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        assertEquals(null, actor.isWhitelisted(connection));
    }

    @Test
    public void testIsWhitelistedReturnsReasonWhenUserWhitelisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("test:repsheet:users:whitelist", "test");
        }
        Actor actor = new Actor(Actor.Type.USER, "test");
        assertEquals("test", actor.isWhitelisted(connection));
    }

    @Test
    public void testIsWhitelistedReturnsNullWhenUserNotWhitelisted() {
        Actor actor = new Actor(Actor.Type.USER, "test");
        assertEquals(null, actor.isWhitelisted(connection));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsWhitelistedThrowsExceptionWhenCalledWithCIDR() {
        Actor actor = new Actor(Actor.Type.CIDR, "1.1.1.0/24");
        actor.isWhitelisted(connection);
    }

    @Test
    public void testIsBlacklistedReturnsReasonWhenIPBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
        }
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        assertEquals("test", actor.isBlacklisted(connection));
    }

    @Test
    public void testIsBlacklistedReturnsNullWhenIPNotBlacklisted() {
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        assertEquals(null, actor.isBlacklisted(connection));
    }

    @Test
    public void testIsBlacklistedReturnsReasonWhenUserBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("test:repsheet:users:blacklist", "test");
        }
        Actor actor = new Actor(Actor.Type.USER, "test");
        assertEquals("test", actor.isBlacklisted(connection));
    }

    @Test
    public void testIsBlacklistedReturnsFalseWhenUserNotBlacklisted() {
        Actor actor = new Actor(Actor.Type.USER, "test");
        assertEquals(null, actor.isBlacklisted(connection));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsBlacklistedThrowsExceptionWhenCalledWithCIDR() {
        Actor actor = new Actor(Actor.Type.CIDR, "1.1.1.0/24");
        actor.isBlacklisted(connection);
    }

    @Test
    public void testIsMarkedReturnsReasonWhenIPMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip", "test");
        }
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        assertEquals("test", actor.isMarked(connection));
    }

    @Test
    public void testIsMarkedReturnsFalseWhenIPNotMarked() {
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        assertEquals(null, actor.isMarked(connection));
    }

    @Test
    public void testIsMarkedReturnsReasonWhenUserMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("test:repsheet:users", "test");
        }
        Actor actor = new Actor(Actor.Type.USER, "test");
        assertEquals("test", actor.isMarked(connection));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsMarkedThrowsExceptionWhenCalledWithCIDR() {
        Actor actor = new Actor(Actor.Type.CIDR, "1.1.1.0/24");
        actor.isMarked(connection);
    }

    @Test
    public void testStatusReturnsWhitelistedWhenAlsoBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:whitelist", "test");
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
            jedis.set("1.1.1.1:repsheet:ip", "test");
        }
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        ActorStatus actorStatus = actor.lookup(connection);
        assertEquals(Actor.Status.WHITELISTED, actorStatus.getStatus());
        assertEquals("test", actorStatus.getReason());
    }

    @Test
    public void testStatusReturnsBlacklistedWhenBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
        }
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        ActorStatus actorStatus = actor.lookup(connection);
        assertEquals(Actor.Status.BLACKLISTED, actorStatus.getStatus());
        assertEquals("test", actorStatus.getReason());
    }

    @Test
    public void testStatusReturnsMarkedWhenMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip", "test");
        }
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        ActorStatus actorStatus = actor.lookup(connection);
        assertEquals(Actor.Status.MARKED, actorStatus.getStatus());
        assertEquals("test", actorStatus.getReason());
    }

    @Test
    public void testStatusReturnsOKWhenNothingKnown() {
        Actor actor = new Actor(Actor.Type.IP, "1.1.1.1");
        ActorStatus actorStatus = actor.lookup(connection);
        assertEquals(Actor.Status.OK, actorStatus.getStatus());
    }
}

package com.repsheet.librepsheet;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.sun.xml.internal.ws.addressing.WsaTubeHelperImpl;
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
    public void testIsWhitelistedReturnsTrueWhenIPWhitelisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:whitelist", "test");
        }
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(true, actor.isWhitelisted(connection));
    }

    @Test
    public void testIsWhitelistedReturnsFalseWhenIPNotWhitelisted() {
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(false, actor.isWhitelisted(connection));
    }

    @Test
    public void testIsWhitelistedReturnsTrueWhenUserWhitelisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("test:repsheet:users:whitelist", "test");
        }
        Actor actor = new Actor(Actor.ActorType.USER, "test");
        assertEquals(true, actor.isWhitelisted(connection));
    }

    @Test
    public void testIsWhitelistedReturnsTrueWhenUserNotWhitelisted() {
        Actor actor = new Actor(Actor.ActorType.USER, "test");
        assertEquals(false, actor.isWhitelisted(connection));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsWhitelistedThrowsExceptionWhenCalledWithCIDR() {
        Actor actor = new Actor(Actor.ActorType.CIDR, "1.1.1.0/24");
        actor.isWhitelisted(connection);
    }

    @Test
    public void testIsBlacklistedReturnsTrueWhenIPBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
        }
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(true, actor.isBlacklisted(connection));
    }

    @Test
    public void testIsBlacklistedReturnsFalseWhenIPNotBlacklisted() {
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(false, actor.isBlacklisted(connection));
    }

    @Test
    public void testIsBlacklistedReturnsTrueWhenUserBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("test:repsheet:users:blacklist", "test");
        }
        Actor actor = new Actor(Actor.ActorType.USER, "test");
        assertEquals(true, actor.isBlacklisted(connection));
    }

    @Test
    public void testIsBlacklistedReturnsFalseWhenUserNotBlacklisted() {
        Actor actor = new Actor(Actor.ActorType.USER, "test");
        assertEquals(false, actor.isBlacklisted(connection));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsBlacklistedThrowsExceptionWhenCalledWithCIDR() {
        Actor actor = new Actor(Actor.ActorType.CIDR, "1.1.1.0/24");
        actor.isBlacklisted(connection);
    }

    @Test
    public void testIsMarkedReturnsTrueWhenIPMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip", "test");
        }
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(true, actor.isMarked(connection));
    }

    @Test
    public void testIsMarkedReturnsFalseWhenIPNotMarked() {
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(false, actor.isMarked(connection));
    }

    @Test
    public void testIsMarkedReturnsTrueWhenUserMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("test:repsheet:users", "test");
        }
        Actor actor = new Actor(Actor.ActorType.USER, "test");
        assertEquals(true, actor.isMarked(connection));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsMarkedThrowsExceptionWhenCalledWithCIDR() {
        Actor actor = new Actor(Actor.ActorType.CIDR, "1.1.1.0/24");
        actor.isMarked(connection);
    }

    @Test
    public void testStatusReturnsWhitelistedWhenAlsoBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:whitelist", "test");
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
            jedis.set("1.1.1.1:repsheet:ip", "test");
        }
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(Actor.ActorStatus.WHITELISTED, actor.status(connection));
    }

    @Test
    public void testStatusReturnsBlacklistedWhenBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
        }
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(Actor.ActorStatus.BLACKLISTED, actor.status(connection));
    }

    @Test
    public void testStatusReturnsMarkedWhenMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip", "test");
        }
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(Actor.ActorStatus.MARKED, actor.status(connection));
    }

    @Test
    public void testStatusReturnsOKWhenNothingKnown() {
        Actor actor = new Actor(Actor.ActorType.IP, "1.1.1.1");
        assertEquals(Actor.ActorStatus.OK, actor.status(connection));
    }
}

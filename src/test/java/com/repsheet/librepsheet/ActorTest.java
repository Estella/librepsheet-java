package com.repsheet.librepsheet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import redis.clients.jedis.Jedis;

public class ActorTest {
    private Connection connection;

    @Before
    public void setUp() {
        connection = new Connection("localhost");
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.flushDB();
        }
    }

    @Test
    public void testStatusReturnsWhitelistedWhenAlsoBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:whitelist", "test");
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
        }
        Actor actor = Actor.lookup(connection, Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
        assertEquals("test", actor.getReason());
    }

    @Test
    public void testStatusReturnsBlacklistedWhenBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:blacklist", "test");
        }
        Actor actor = Actor.lookup(connection, Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
        assertEquals("test", actor.getReason());
    }

    @Test
    public void testStatusReturnsMarkedWhenMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip", "test");
        }
        Actor actor = Actor.lookup(connection, Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
        assertEquals("test", actor.getReason());
    }

    @Test
    public void testStatusReturnsOKWhenNothingKnown() {
        Actor actor = Actor.lookup(connection, Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.OK, actor.getStatus());
    }

    @Test
    public void testStatusReturnsWhitelistedWhenIPv4InCIDRBlock() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("10.0.1.0/24:repsheet:cidr:whitelist", "test");
        }
        Actor actor = Actor.lookup(connection, Actor.Type.IP, "10.0.1.15");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
    }

    @Test
    public void testStatusReturnsBlacklistedWhenIPv6InCIDRBlock() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1fff:0:0a88:85a3:0:0:ac1f:8001/24:repsheet:cidr:blacklist", "test");
        }
        Actor actor = Actor.lookup(connection, Actor.Type.IP, "1fff:0:0a88:85a3:0:0:ac1f:8002");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
    }

    @Test
    public void testStatusReturnsMarkedWhenIPv4InCIDRBlock() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("10.0.1.0/24:repsheet:cidr", "test");
        }
        Actor actor = Actor.lookup(connection, Actor.Type.IP, "10.0.1.15");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
    }
}

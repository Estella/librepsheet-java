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
}

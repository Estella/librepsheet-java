package com.repsheet.librepsheet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class ConnectionTest {
    private static final int TWENTY_TIMEOUT = 20;

    protected Connection connection;
    
    @Before
    public void setUp() {
        connection = new Connection("localhost");
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.flushDB();
        }
    }

    @Test
    public void testConnectionReturnsUsableConnectionPool() {
        try (Jedis jedis = connection.getPool().getResource()) {
            String response = jedis.ping();
            assertEquals("PONG", response);
        }
    }

    @Test
    public void testStatusReturnsWhitelistedWhenAlsoBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:whitelisted", "test");
            jedis.set("1.1.1.1:repsheet:ip:blacklisted", "test");
        }
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
        assertEquals("test", actor.getReason());
    }

    @Test
    public void testStatusReturnsBlacklistedWhenBlacklisted() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:blacklisted", "test");
        }
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
        assertEquals("test", actor.getReason());
    }

    @Test
    public void testStatusReturnsMarkedWhenMarked() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1.1.1.1:repsheet:ip:marked", "test");
        }
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
        assertEquals("test", actor.getReason());
    }

    @Test
    public void testStatusReturnsOKWhenNothingKnown() {
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.OK, actor.getStatus());
    }

    @Test
    public void testStatusReturnsWhitelistedWhenIPv4InCIDRBlock() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("10.0.1.0/24:repsheet:cidr:whitelisted", "test");
        }
        Actor actor = connection.lookup(Actor.Type.IP, "10.0.1.15");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
    }

    @Test
    public void testStatusReturnsBlacklistedWhenIPv6InCIDRBlock() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("1fff:0:0a88:85a3:0:0:ac1f:8001/24:repsheet:cidr:blacklisted", "test");
        }
        Actor actor = connection.lookup(Actor.Type.IP, "1fff:0:0a88:85a3:0:0:ac1f:8002");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
    }

    @Test
    public void testStatusReturnsMarkedWhenIPv4InCIDRBlock() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("10.0.1.0/24:repsheet:cidr:marked", "test");
        }
        Actor actor = connection.lookup(Actor.Type.IP, "10.0.1.15");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
    }

    @Test
    public void testMultipleCIDRBlocksThatOverlap() {
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.set("10.0.1.0/24:repsheet:cidr:whitelisted", "test");
            jedis.set("10.0.0.0/8:repsheet:cidr:whitelisted", "test");
            jedis.set("10.0.1.15/32:repsheet:cidr:whitelisted", "test");
        }
        Actor actor = connection.lookup(Actor.Type.IP, "10.0.1.15");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
    }

    @Test
    public void testBlacklistIPWithoutExpiry() {
        connection.blacklist("1.1.1.1", Actor.Type.IP, "test");
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
    }

    @Test
    public void testBlacklistUserWithoutExpiry() {
        connection.blacklist("repsheet", Actor.Type.USER, "test");
        Actor actor = connection.lookup(Actor.Type.USER, "repsheet");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
    }

    @Test
    public void testBlacklistIPWithExpiry() {
        connection.blacklist("1.1.1.1", Actor.Type.IP, "test", TWENTY_TIMEOUT);
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
        assertTrue(actor.getTTL(connection) > 15);
    }

    @Test
    public void testBlacklistUserWithExpiry() {
        connection.blacklist("repsheet", Actor.Type.USER, "test", TWENTY_TIMEOUT);
        Actor actor = connection.lookup(Actor.Type.USER, "repsheet");
        assertEquals(Actor.Status.BLACKLISTED, actor.getStatus());
        assertTrue(actor.getTTL(connection) > 15);
    }

    @Test
    public void testWhitelistIPWithoutExpiry() {
        connection.whitelist("1.1.1.1", Actor.Type.IP, "test");
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
    }

    @Test
    public void testWhitelistUserWithoutExpiry() {
        connection.whitelist("repsheet", Actor.Type.USER, "test");
        Actor actor = connection.lookup(Actor.Type.USER, "repsheet");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
    }

    @Test
    public void testWhitelistIPWithExpiry() {
        connection.whitelist("1.1.1.1", Actor.Type.IP, "test", TWENTY_TIMEOUT);
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
        assertTrue(actor.getTTL(connection) > 15);
    }

    @Test
    public void testWhitelistUserWithExpiry() {
        connection.whitelist("repsheet", Actor.Type.USER, "test", TWENTY_TIMEOUT);
        Actor actor = connection.lookup(Actor.Type.USER, "repsheet");
        assertEquals(Actor.Status.WHITELISTED, actor.getStatus());
        assertTrue(actor.getTTL(connection) > 15);
    }

    @Test
    public void testMarkIPWithoutExpiry() {
        connection.mark("1.1.1.1", Actor.Type.IP, "test");
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
    }

    @Test
    public void testMarkUserWithoutExpiry() {
        connection.mark("repsheet", Actor.Type.USER, "test");
        Actor actor = connection.lookup(Actor.Type.USER, "repsheet");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
    }

    @Test
    public void testMarkIPWithExpiry() {
        connection.mark("1.1.1.1", Actor.Type.IP, "test", TWENTY_TIMEOUT);
        Actor actor = connection.lookup(Actor.Type.IP, "1.1.1.1");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
        assertTrue(actor.getTTL(connection) > 15);
    }

    @Test
    public void testMarkUserWithExpiry() {
        connection.mark("repsheet", Actor.Type.USER, "test", TWENTY_TIMEOUT);
        Actor actor = connection.lookup(Actor.Type.USER, "repsheet");
        assertEquals(Actor.Status.MARKED, actor.getStatus());
        assertTrue(actor.getTTL(connection) > 15);
    }

    @Test
    public void looksUpForAllKeysWithGivenStatus() {
        connection.blacklist("bob", Actor.Type.USER, "test", TWENTY_TIMEOUT);
        connection.whitelist("1.1.1.1", Actor.Type.IP, "test", TWENTY_TIMEOUT);
        connection.blacklist("1.2.3.4", Actor.Type.IP, "test", TWENTY_TIMEOUT);

        Set<String> keys = connection.lookupByStatus(Actor.Status.BLACKLISTED);

        Assert.assertTrue(keys.contains("1.2.3.4:repsheet:ip:blacklisted"));
        Assert.assertTrue(keys.contains("bob:repsheet:user:blacklisted"));
    }

    @Test
    public void returnsEmptySetWhenUnacceptedTypeProvidedToLookup() {
        connection.blacklist("bob", Actor.Type.USER, "test", TWENTY_TIMEOUT);
        connection.whitelist("1.1.1.1", Actor.Type.IP, "test", TWENTY_TIMEOUT);

        Set<String> keys = connection.lookupByStatus(Actor.Status.OK);

        Assert.assertEquals(0, keys.size());
    }
}

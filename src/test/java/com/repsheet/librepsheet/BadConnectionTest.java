package com.repsheet.librepsheet;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class BadConnectionTest {
    protected Connection badConnection;

    @Before
    public void setUp() {
        badConnection = new Connection("badhost");
    }

    @Test(expected = JedisConnectionException.class)
    public void testConnectionReturnsUsableConnectionPoolShouldThrowUp() {
        badConnection = new Connection("badhost");
        badConnection.getPool().getResource();
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testBlacklistIPWithoutExpiryShouldThrowUp() {
        badConnection.blacklist("1.1.1.1", Actor.Type.IP, "test");
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testBlacklistUserWithoutExpiryShouldThrowUp() {
        badConnection.blacklist("repsheet", Actor.Type.USER, "test");
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testBlacklistIPWithExpiryShouldThrowUp() {
        badConnection.blacklist("1.1.1.1", Actor.Type.IP, "test", 20);
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testBlacklistUserWithExpiryShouldThrowUp() {
        badConnection.blacklist("repsheet", Actor.Type.USER, "test", 20);
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testWhitelistIPWithoutExpiryShouldThrowUp() {
        badConnection.whitelist("1.1.1.1", Actor.Type.IP, "test");
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testWhitelistUserWithoutExpiryShouldThrowUp() {
        badConnection.whitelist("repsheet", Actor.Type.USER, "test");
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testWhitelistIPWithExpiryShouldThrowUp() {
        badConnection.whitelist("1.1.1.1", Actor.Type.IP, "test", 20);
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testWhitelistUserWithExpiryShouldThrowUp() {
        badConnection.whitelist("repsheet", Actor.Type.USER, "test", 20);
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testMarkIPWithoutExpiryShouldThrowUp() {
        badConnection.mark("1.1.1.1", Actor.Type.IP, "test");
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testMarkUserWithoutExpiryShouldThrowUp() {
        badConnection.mark("repsheet", Actor.Type.USER, "test");
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testMarkIPWithExpiryShouldThrowUp() {
        badConnection.mark("1.1.1.1", Actor.Type.IP, "test", 20);
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testMarkUserWithExpiryShouldThrowUp() {
        badConnection.mark("repsheet", Actor.Type.USER, "test", 20);
    }
}

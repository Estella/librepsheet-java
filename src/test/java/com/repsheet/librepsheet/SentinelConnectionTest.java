package com.repsheet.librepsheet;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SentinelConnectionTest extends ConnectionTest {
    // Sentinel hosts are configured in Makefile, run make start first to start the redis server
    protected static HostAndPort sentinel1 = new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT);
    protected static HostAndPort sentinel2 = new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT + 1);
    protected static HostAndPort sentinel3 = new HostAndPort("localhost", Protocol.DEFAULT_SENTINEL_PORT + 3);

    @Before
    @Override
    public void setUp() {
        Set<String> sentinels = new HashSet<>();
        sentinels.add(sentinel1.toString());
        sentinels.add(sentinel2.toString());

        connection = new Connection("mymaster", sentinels);
        try (Jedis jedis = connection.getPool().getResource()) {
            jedis.flushDB();
        }
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testConnectionThrowsUpIfMasterNameIsNotCorrect() {
        Set<String> sentinels = new HashSet<>();
        sentinels.add(sentinel1.toString());
        sentinels.add(sentinel2.toString());

        new Connection("badmaster", sentinels);
    }

    @Test(expected = RepsheetConnectionException.class)
    public void testConnectionThrowsUpIfSentinelsAreInvalid() {
        Set<String> sentinels = new HashSet<>();
        sentinels.add(new HostAndPort("localhost", 1111).toString());
        sentinels.add(new HostAndPort("localhost", 1112).toString());

        new Connection("mymaster", sentinels);
    }
}

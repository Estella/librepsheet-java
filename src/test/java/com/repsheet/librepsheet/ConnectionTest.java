package com.repsheet.librepsheet;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import redis.clients.jedis.Jedis;

public class ConnectionTest {
    @Test
    public void testConnectionReturnsUsableConnectionPool() {
        Connection connection = new Connection("localhost", 6379, 5);
        try (Jedis jedis = connection.getPool().getResource()) {
            String response = jedis.ping();
            assertEquals("PONG", response);
        }
    }
}

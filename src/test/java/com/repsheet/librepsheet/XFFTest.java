package com.repsheet.librepsheet;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class XFFTest {
    @Test
    public void testSingleIPv4Address() {
        assertEquals("1.1.1.1", XFF.getSourceAddress("1.1.1.1"));
    }

    @Test
    public void testMultipleIPv4Addresses() {
        assertEquals("1.1.1.1", XFF.getSourceAddress("1.1.1.1, 2.2.2.2, 3.3.3.3"));
    }

    @Test
    public void testSingleInvalidIPv4Address() {
        assertEquals(null, XFF.getSourceAddress("bad"));
    }

    @Test
    public void testSingleIPv6Address() {
        assertEquals("2607:fb90:2c1a:664:0:45:287c:1301", XFF.getSourceAddress("2607:fb90:2c1a:664:0:45:287c:1301"));
    }

    @Test
    public void testMultipleIPv6Addresses() {
        assertEquals("2607:fb90:2c1a:664:0:45:287c:1301", XFF.getSourceAddress("2607:fb90:2c1a:664:0:45:287c:1301, ::1"));
    }

    @Test
    public void testMixedAddresses() {
        assertEquals("2607:fb90:2c1a:664:0:45:287c:1301", XFF.getSourceAddress("2607:fb90:2c1a:664:0:45:287c:1301, 66.249.84.231, 63.80.12.214, 209.170.78.1417"));
    }

    @Test
    public void testMaliciousXFF() {
        assertEquals(null, XFF.getSourceAddress("\\x5000 8.8.8.8, 12.34.56.78, 98.76.54.32"));
    }
}

package com.repsheet.librepsheet;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for CIDRUtils.
 *
 * @author saserpoosh
 * @since 10/28/15
 */
public class CIDRUtilsTest {

    private static final String CIDR_BLOCK = "10.0.1.0/24:repsheet:cidr:whitelisted";

    @Test
    public void extractsCidrValueOutOfCidrBlock() {
        String cidr = CIDRUtils.fetchCidr(CIDR_BLOCK);
        Assert.assertEquals("10.0.1.0/24", cidr);
    }

    @Test
    public void knowsWhenCidrContainsAddress() {
        String ipAddress = "10.0.1.15";
        Assert.assertTrue(CIDRUtils.cidrContainsAddress(CIDR_BLOCK, ipAddress));
    }

    @Test
    public void returnsFalseWhenCidrBlockIsMalformed() {
        String malformedCidrBlock = "10.0.1.0/24:cidr:whitelisted";
        String ipAddress = "10.0.1.15";
        Assert.assertFalse(CIDRUtils.cidrContainsAddress(malformedCidrBlock, ipAddress));
    }
}

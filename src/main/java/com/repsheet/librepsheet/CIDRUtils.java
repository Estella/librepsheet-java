package com.repsheet.librepsheet;

import com.aaronbedra.orchard.CIDR;
import com.aaronbedra.orchard.OrchardException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public final class CIDRUtils {

    private static final int KEYSPACELENGTH = 3;
    private static final String BLOCK_SEPARATOR = ":";

    private CIDRUtils() { }

    public static String fetchCidr(final String cidrBlock) {
        String[] parts = cidrBlock.split(BLOCK_SEPARATOR);
        return StringUtils.join(Arrays.asList(parts).subList(0, parts.length - KEYSPACELENGTH), BLOCK_SEPARATOR);
    }

    public static boolean cidrContainsAddress(final String cidrBlock, final String address) {
        try {
            String cidr = fetchCidr(cidrBlock);
            return CIDR.valueOf(cidr).contains(address);
        } catch (OrchardException ignored) {
            return false;
        }
    }
}

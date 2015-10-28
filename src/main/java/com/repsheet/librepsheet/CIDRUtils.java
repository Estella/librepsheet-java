package com.repsheet.librepsheet;

import com.aaronbedra.orchard.CIDR;
import com.aaronbedra.orchard.OrchardException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Utility class for working with Repsheet CIRD blocks.
 *
 * @author saserpoosh
 * @since 10/28/15
 */
public class CIDRUtils {

    private static final int KEYSPACELENGTH     = 3;
    private static final String BLOCK_SEPARATOR = ":";

    public static String fetchCidr(String cidrBlock) {
        String[] parts = cidrBlock.split(BLOCK_SEPARATOR);
        return StringUtils.join(Arrays.asList(parts).subList(0, parts.length - KEYSPACELENGTH), BLOCK_SEPARATOR);
    }

    public static boolean cidrContainsAddress(String cidrBlock, String address) {
        try {
            String cidr = fetchCidr(cidrBlock);
            return CIDR.valueOf(cidr).contains(address);
        } catch (OrchardException ignored) {
            return false;
        }
    }
}

package com.repsheet.librepsheet;

import org.apache.commons.validator.routines.InetAddressValidator;

public final class XFF {
    private XFF() { }

    public static String getSourceAddress(final String header) {
        String rawSource = header.split(",")[0];
        if (InetAddressValidator.getInstance().isValid(rawSource)) {
            return rawSource;
        } else {
            return null;
        }
    }
}

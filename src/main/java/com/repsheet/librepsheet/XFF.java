package com.repsheet.librepsheet;

import org.apache.commons.validator.routines.InetAddressValidator;

public class XFF {
    public static String getSourceAddress(String header) {
        String rawSource = header.split(",")[0];
        if (InetAddressValidator.getInstance().isValid(rawSource)) {
            return rawSource;
        } else {
            return null;
        }
    }
}

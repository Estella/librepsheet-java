package com.repsheet.librepsheet;

public class Util {

    public static String keyspaceFromStatus(final Actor.Status status) {
        switch (status) {
            case WHITELISTED:
                return "whitelisted";
            case BLACKLISTED:
                return "blacklisted";
            case MARKED:
                return "marked";
            default:
                return null;
        }
    }

    public static String stringFromType(final Actor.Type type) {
        switch (type) {
            case IP:
                return "ip";
            case USER:
                return "users";
            case CIDR:
                return "cidr";
            default:
                return null;
        }
    }
}

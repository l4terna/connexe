package com.laterna.connexemain.v1.permission.enumeration;

import io.seruco.encoding.base62.Base62;
import lombok.Getter;
import java.util.BitSet;

@Getter
public enum Permission {
    // General
//    1100000
//    00000000001
//    0000000
    VIEW_CHANNELS(0),
    MANAGE_CHANNELS(1),
    MANAGE_ROLES(2),
    MANAGE_CATEGORIES(15), // <-- max

    // Member
    CREATE_INVITE(3),

    // Text
    SEND_MESSAGES(4),
    MANAGE_MESSAGES(5),
    ATTACH_FILES(6),
    ADD_REACTIONS(7),

    // Voice
    VOICE_CONNECT(8),
    SPEAK(9),
    MUTE_MEMBERS(10),
    DEAFEN_MEMBERS(11),

    // Admin
    KICK_MEMBERS(12),
    BAN_MEMBERS(13),
    MANAGE_HUB(14);

    private final int bitPosition;

    Permission(int bitPosition) {
        this.bitPosition = bitPosition;
    }

    public static class Builder {
        private final BitSet permissions;

        private Builder() {
            this.permissions = new BitSet();
        }

        public Builder add(Permission... permissions) {
            for (Permission permission : permissions) {
                this.permissions.set(permission.getBitPosition());
            }
            return this;
        }

        public Builder addAll() {
            add(Permission.values());
            return this;
        }

        public String toBinary() {
            StringBuilder sb = new StringBuilder(permissions.length());
            for (int i = 0; i < permissions.length(); i++) {
                sb.append(permissions.get(i) ? '1' : '0');
            }

            return sb.toString();
        }

        public String toBase62() {
            Base62 base62 = Base62.createInstance();
            return new String(base62.encode(permissions.toByteArray()));
        }

        public BitSet build() {
            return (BitSet) permissions.clone();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static BitSet fromBase62(String encoded) {
        Base62 base62 = Base62.createInstance();
        byte[] decoded = base62.decode(encoded.getBytes());
        return BitSet.valueOf(decoded);
    }

    public static boolean has(BitSet permissions, Permission permission) {
        return permissions.get(permission.getBitPosition());
    }

    public static boolean hasAll(BitSet permissions, Permission... permissionList) {
        for (Permission permission : permissionList) {
            if (!has(permissions, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasAny(BitSet permissions, Permission... permissionList) {
        for (Permission permission : permissionList) {
            if (has(permissions, permission)) {
                return true;
            }
        }
        return false;
    }

    public static String toBinary(String base62) {
        BitSet raw = fromBase62(base62);

        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            sb.append(raw.get(i) ? '1' : '0');
        }

        return sb.toString();
    }
}
package org.autosense.commons;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.UUID;

/*
 * Credit : Physical Web
 * https://github.com/google/physical-web/
 *
 */

public class Utils {
    private static final byte URL_FRAME_TYPE = 0x10;
    private static final byte TITLE_TYPE = 0x0e;
    private static final String URN_UUID = "urn:uuid:";

    private static final HashMap<Byte, String> URI_SCHEMES = new HashMap<Byte, String>() {{
        put((byte) 0, "http://www.");
        put((byte) 1, "https://www.");
        put((byte) 2, "http://");
        put((byte) 3, "https://");
        put((byte) 4, URN_UUID);
    }};

    private static final HashMap<Byte, String> URL_CODES = new HashMap<Byte, String>() {{
        put((byte) 0, ".com/");
        put((byte) 1, ".org/");
        put((byte) 2, ".edu/");
        put((byte) 3, ".net/");
        put((byte) 4, ".info/");
        put((byte) 5, ".biz/");
        put((byte) 6, ".gov/");
        put((byte) 7, ".com");
        put((byte) 8, ".org");
        put((byte) 9, ".edu");
        put((byte) 10, ".net");
        put((byte) 11, ".info");
        put((byte) 12, ".biz");
        put((byte) 13, ".gov");
    }};

    public static String decode(byte[] serviceData) {
        StringBuilder urlBuilder = new StringBuilder();
        String scheme = URI_SCHEMES.get(serviceData[2]);
        if (scheme != null) {
            urlBuilder.append(scheme);
            if (scheme.equals(URN_UUID)) {
                return decodeUrnUuid(serviceData, urlBuilder);
            }
            return decodeUrl(serviceData, urlBuilder);
        }

        return null;
    }

    private static String decodeUrl(byte[] serviceData, StringBuilder urlBuilder) {
        for (int i = 3; i < serviceData.length; i++) {
            byte b = serviceData[i];
            String expansion = URL_CODES.get(b);
            if (expansion == null) {
                urlBuilder.append((char) b);
            } else {
                urlBuilder.append(expansion);
            }
        }
        return urlBuilder.toString();
    }

    private static String decodeUrnUuid(byte[] serviceData, StringBuilder urnBuilder) {
        ByteBuffer buf = ByteBuffer.wrap(serviceData);
        buf.order(ByteOrder.BIG_ENDIAN);
        long mostSignificantBytes, leastSignificantBytes;
        try {
            buf.position(3);
            mostSignificantBytes = buf.getLong();
            leastSignificantBytes = buf.getLong();
        } catch (BufferUnderflowException e){
            return "";
        }
        UUID uuid = new UUID(mostSignificantBytes, leastSignificantBytes);
        urnBuilder.append(uuid.toString());
        return urnBuilder.toString();
    }
}

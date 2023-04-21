package me.lyric.infinity.api.util.bytes;

public class ByteChanger {

    public static byte FALSE = 0;
    public static byte TRUE = 1;

    public static boolean byteToBoolean(byte value) {
        return value == TRUE;
    }
}

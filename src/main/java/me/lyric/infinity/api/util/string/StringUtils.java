package me.lyric.infinity.api.util.string;

/**
 * @author lyric
 */

public class StringUtils {

    public static boolean contains(String name, String... items) {
        boolean flag = false;

        for (String i : items) {
            if (i.equalsIgnoreCase(name)) {
                flag = true;

                break;
            }
        }

        return flag;
    }
    public static String getTitle(String in) {
        in = Character.toUpperCase(in.toLowerCase().charAt(0)) + in.toLowerCase().substring(1);
        return in;
    }
}

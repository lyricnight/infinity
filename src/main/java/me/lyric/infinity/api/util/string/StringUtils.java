package me.lyric.infinity.api.util.string;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.Infinity;

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

    /**
     * @apiNote method returns color code from setting.
     * @param value - string setting value
     * @return color.
     */

    public static String getCodeFromSetting(String value)
    {
        switch (value)
        {
            case "None":
                return "";
            case "Black":
                return "§0";
            case "DarkGray":
                return "§8";
            case "Gray":
                return "§7";
            case "DarkBlue":
                return "§1";
            case "Blue":
                return "§9";
            case "DarkGreen":
                return "§2";
            case "Green":
                return "§a";
            case "DarkAqua":
                return "§3";
            case "Aqua":
                return "§b";
            case "DarkRed":
                return "§4";
            case "Red":
                return "§c";
            case "DarkPurple":
                return "§5";
            case "Purple":
                return "§d";
            case "Gold":
                return "§6";
            case "Yellow":
                return "§e";
        }
        Infinity.LOGGER.error("Couldn't return a proper colour! String value passed to method: " + value);
        return "";
    }

    public static String coloredString(String string, String color) {
        String coloredString;

        coloredString = getCodeFromSetting(color) + string + ChatFormatting.RESET;

        return coloredString;
    }

}

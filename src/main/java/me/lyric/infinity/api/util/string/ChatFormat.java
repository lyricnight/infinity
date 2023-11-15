package me.lyric.infinity.api.util.string;

import com.mojang.realmsclient.gui.ChatFormatting;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

import java.util.Arrays;
import java.util.List;

/**
 * for changing watermark in chat thing
 * huh
 */
public class ChatFormat {
    public static final String BLACK = String.valueOf(ChatFormatting.BLACK);
    public static final String DARK_BLUE = String.valueOf(ChatFormatting.DARK_BLUE);
    public static final String DARK_GREEN = String.valueOf(ChatFormatting.DARK_GREEN);
    public static final String DARK_AQUA = String.valueOf(ChatFormatting.DARK_AQUA);
    public static final String DARK_RED = String.valueOf(ChatFormatting.DARK_RED);
    public static final String DARK_PURPLE = String.valueOf(ChatFormatting.DARK_PURPLE);
    public static final String GOLD = String.valueOf(ChatFormatting.GOLD);
    public static final String GRAY = String.valueOf(ChatFormatting.GRAY);
    public static final String DARK_GRAY = String.valueOf(ChatFormatting.DARK_GRAY);
    public static final String BLUE = String.valueOf(ChatFormatting.BLUE);
    public static final String GREEN = String.valueOf(ChatFormatting.GREEN);
    public static final String AQUA = String.valueOf(ChatFormatting.AQUA);
    public static final String RED = String.valueOf(ChatFormatting.RED);
    public static final String LIGHT_PURPLE = String.valueOf(ChatFormatting.LIGHT_PURPLE);
    public static final String YELLOW = String.valueOf(ChatFormatting.YELLOW);
    public static final String WHITE = String.valueOf(ChatFormatting.WHITE);
    public static final String OBFUSCATED = String.valueOf(ChatFormatting.OBFUSCATED);
    public static final String BOLD = String.valueOf(ChatFormatting.BOLD);
    public static final String STRIKE = String.valueOf(ChatFormatting.STRIKETHROUGH);
    public static final String UNDERLINE = String.valueOf(ChatFormatting.UNDERLINE);
    public static final String ITALIC = String.valueOf(ChatFormatting.ITALIC);
    public static final String RESET = String.valueOf(ChatFormatting.RESET);


    public static String coloredString(String string, String color) {
        String coloredString = string;

        coloredString = color + string + ChatFormatting.RESET;

        return coloredString;
    }
    public static List<String> getAll()
    {
        return Arrays.asList(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE);
    }

    public enum Color {
        NONE,
        WHITE,
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW

    }
}
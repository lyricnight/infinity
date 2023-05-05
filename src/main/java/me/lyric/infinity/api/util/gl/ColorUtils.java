package me.lyric.infinity.api.util.gl;

import me.lyric.infinity.api.util.minecraft.IGlobals;

public class ColorUtils implements IGlobals {
    public ColorUtils(final int i, final int i1, final int i2, final int i3) {
    }
    public static int toRGBA(final int r, final int g, final int b) {
        return toRGBA(r, g, b, 255);
    }

    public static int toRGBA(final int r, final int g, final int b, final int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

}

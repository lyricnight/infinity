package me.lyric.infinity.api.util.string;


import net.minecraft.client.Minecraft;
import java.awt.*;

public class FontUtil {

    public void drawString(String text, int x, int y, int color, boolean shadow) {
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, shadow);
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, shadow);
    }

    public static void drawString(String text, int x, int y, int color) {
            Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color);
    }

    public static void drawStringWithShadow(String text, int x, int y, int color) {
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public static float getFontHeight() {
            return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public float getFontHeight(double scale) {
        return (float) (getFontHeight() * scale);
    }

    public float getStringWidth(String text, double scale) {
        return (float) (getStringWidth(text) * scale);
    }

    public static float getStringWidth(String text) {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }
}


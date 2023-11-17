package me.lyric.infinity.api.util.string;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class ClientFont implements IGlobals {
    private static final Pattern colorPattern = Pattern.compile("\u00c2\u00a7[0123456789abcdefklmnor]");
    public final int height = 9;
    private final String name;
    private final float size;
    private int scaleFactor = new ScaledResolution(mc).getScaleFactor();
    private UnicodeFont font;
    private float aAFactor;

    public ClientFont(String name, float size) {
        this.name = name;
        this.size = size;
        ScaledResolution sr = new ScaledResolution(mc);
        try {
            this.scaleFactor = sr.getScaleFactor();
            this.font = new UnicodeFont(this.getFontByName(name).deriveFont(size * (float)this.scaleFactor / 2.0f));
            this.font.addAsciiGlyphs();
            this.font.getEffects().add(new ColorEffect(Color.WHITE));
            this.font.loadGlyphs();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        this.aAFactor = sr.getScaleFactor();
    }

    private Font getFontByName(String name) throws IOException, FontFormatException {
        return this.getFontFromInput("/assets/infinity/font/" + name + ".ttf");
    }

    private Font getFontFromInput(String path) throws IOException, FontFormatException {
        return Font.createFont(0, Objects.requireNonNull(ClientFont.class.getResourceAsStream(path)));
    }

    public int drawString(String text, float x2, float y2, int color) {
        if (text == null) {
            return 0;
        }
        ScaledResolution resolution = new ScaledResolution(mc);
        try {
            if (resolution.getScaleFactor() != this.scaleFactor) {
                this.scaleFactor = resolution.getScaleFactor();
                this.font = new UnicodeFont(this.getFontByName(this.name).deriveFont(this.size * (float)this.scaleFactor / 2.0f));
                this.font.addAsciiGlyphs();
                this.font.getEffects().add(new ColorEffect(Color.WHITE));
                this.font.loadGlyphs();
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        aAFactor = resolution.getScaleFactor();
        GlStateManager.pushMatrix();
        GlStateManager.scale((float)(1.0f / this.aAFactor), (float)(1.0f / this.aAFactor), (float)(1.0f / this.aAFactor));
        y2 *= aAFactor;
        float originalX = x2 *= aAFactor;
        float red = (color >> 16 & 0xFF) / 255.0f;
        float green = (color >> 8 & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = (color >> 24 & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
        char[] characters = text.toCharArray();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        String[] parts = colorPattern.split(text);
        int index = 0;
        for (String s2 : parts) {
            for (String s22 : s2.split("\n")) {
                for (String s3 : s22.split("\r")) {
                    font.drawString(x2, y2, s3, new org.newdawn.slick.Color(color));
                    x2 += font.getWidth(s3);
                    if ((index += s3.length()) >= characters.length || characters[index] != '\r') continue;
                    x2 = originalX;
                    ++index;
                }
                if (index >= characters.length || characters[index] != '\n') continue;
                x2 = originalX;
                y2 += getHeight(s22) * 2.0f;
                ++index;
            }
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        return (int) x2;
    }

    public int drawStringWithShadow(String text, float x2, float y2, int color) {
        drawString(StringUtils.stripControlCodes(text), x2 + 0.5f, y2 + 0.5f, 0);
        return drawString(text, x2, y2, color);
    }

    public int drawString(String text, float x2, float y2, int color, boolean shadow) {
        if (shadow) {
            drawStringWithShadow(text, x2, y2, color);
        } else {
            drawString(text, x2, y2, color);
        }
        return drawString(text, x2, y2, color);
    }

    public float getHeight(String s2) {
        return font.getHeight(s2) / 2.0f;
    }

    public float getStringWidth(String text) {
        return font.getWidth(text) / 2.0f;
    }

    public String getName() {
        return name;
    }

    public float getSize() {
        return size;
    }
}
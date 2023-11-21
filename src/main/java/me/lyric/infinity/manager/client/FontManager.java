package me.lyric.infinity.manager.client;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.string.Renderer;
import me.lyric.infinity.impl.modules.client.Internals;

import java.awt.*;

public class FontManager implements IGlobals {

    public Font font = new Font("Comfortaa-Regular", 0, 18);

    public Renderer renderer;

    public FontManager()
    {
        renderer = new Renderer(font, Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).aalias.getValue(), Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).frac.getValue());
    }

    public void drawString(String text, float x, float y, int color, boolean shadow)
    {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).cfont.getValue())
        {
            renderer.drawString(text, x, y, color, shadow);
        }
        else
        {
            mc.fontRenderer.drawString(text, x, y, color, shadow);
        }
    }

    public void drawCenteredString(String text, float x, float y, int color, boolean shadow)
    {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).cfont.getValue())
        {
            if (shadow)
            {
                renderer.drawCenteredStringWithShadow(text, x, y, color);
            }
            else
            {
                renderer.drawCenteredString(text, x, y, color);
            }
        }
    }

    public int getStringWidth(String text)
    {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).cfont.getValue())
        {
            return renderer.getStringWidth(text);
        }
        else
        {
            return mc.fontRenderer.getStringWidth(text);
        }
    }

    public int getHeight(String text)
    {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).cfont.getValue())
        {
            return renderer.getStringHeight(text);
        }
        else
        {
            return mc.fontRenderer.FONT_HEIGHT;
        }
    }


    public void setFonts(float size, boolean alias, boolean fractional)
    {
        font = font.deriveFont(size);
        font = font.deriveFont(0);
        renderer = new Renderer(font, alias, fractional);
    }

}

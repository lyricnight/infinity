package me.lyric.infinity.manager.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.string.CustomFontRenderer;
import me.lyric.infinity.impl.modules.client.Fonts;
import me.lyric.infinity.manager.Managers;

import java.awt.*;

public class FontManager implements IGlobals {

    private CustomFontRenderer renderer = new CustomFontRenderer(new Font("Arial", Font.PLAIN, 17), true, true);

    public void drawString(String text, float x, float y, int color, boolean shadow)
    {
        if (Managers.MODULES.getModuleByClass(Fonts.class).isEnabled())
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
        if (Managers.MODULES.getModuleByClass(Fonts.class).isEnabled())
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
        if (Managers.MODULES.getModuleByClass(Fonts.class).isEnabled())
        {
            return renderer.getStringWidth(text);
        }

        return mc.fontRenderer.getStringWidth(text);
    }

    public float getStringHeight()
    {
        if (Managers.MODULES.getModuleByClass(Fonts.class).isEnabled())
        {
            return renderer.getHeight();
        }

        return mc.fontRenderer.FONT_HEIGHT;
    }
    public void setFontRenderer(Font font, boolean antiAlias, boolean metrics)
    {
        renderer = new CustomFontRenderer(font, antiAlias, metrics);
    }
}

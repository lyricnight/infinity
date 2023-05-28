package me.lyric.infinity.api.util.minecraft;

import net.minecraft.client.Minecraft;

/**
 * @author lyric
 * wrapper
 * {@link Minecraft}
 */

public interface IGlobals {
    Minecraft mc = Minecraft.getMinecraft();
}

package me.lyric.infinity.mixin.mixins.gui;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author lyric
 * @apiNote used for MixinGuiNewChat customFont
 */


@Mixin( Gui.class )
public abstract class MixinGui
{
    @Shadow
    public static void drawRect(final int left, final int top, final int right, final int bottom, final int color) {
    }

    @Shadow
    protected abstract void drawGradientRect(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);

    @Shadow
    public abstract void drawTexturedModalRect(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
}
package me.lyric.infinity.api.util.metadata;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MathUtils
{
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }

    public static double distanceTo(final BlockPos blockPos) {
        return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static double distanceTo(final double x, final double y, final double z) {
        if (mc.player == null) {
            return 0.0;
        }
        final float f = (float)(mc.player.posX - x);
        final float g = (float)(mc.player.posY - y);
        final float h = (float)(mc.player.posZ - z);
        return MathHelper.sqrt(f * f + g * g + h * h);
    }
}

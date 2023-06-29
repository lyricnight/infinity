package me.lyric.infinity.api.util.metadata;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MathUtils implements IGlobals
{
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
    public static double roundToClosest(double num, double low, double high) {
        double d1 = num - low;
        double d2 = high - num;

        if (d2 > d1) {
            return low;

        } else {
            return high;
        }
    }
    public static int square(int i)
    {
        return i * i;
    }

    public static float square(float i)
    {
        return i * i;
    }

    public static double square(double i)
    {
        return i * i;
    }
}

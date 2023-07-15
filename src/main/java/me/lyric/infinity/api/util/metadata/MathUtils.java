package me.lyric.infinity.api.util.metadata;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    public static float roundFloat(double number, int scale) {
        BigDecimal bd = BigDecimal.valueOf(number);
        bd = bd.setScale(scale, RoundingMode.FLOOR);
        return bd.floatValue();
    }
    public static int square(int i)
    {
        return i * i;
    }

}

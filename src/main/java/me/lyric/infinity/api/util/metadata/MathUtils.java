package me.lyric.infinity.api.util.metadata;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }
    public static double angle(Vec3d vec3d, Vec3d other) {
        double lengthSq = vec3d.length() * other.length();

        if (lengthSq < 1.0E-4D) {
            return 0.0;
        }

        double dot = vec3d.dotProduct(other);
        double arg = dot / lengthSq;

        if (arg > 1) {
            return 0.0;
        } else if (arg < -1) {
            return 180.0;
        }

        return Math.acos(arg) * 180.0f / Math.PI;
    }
    public static float rad(final float angle) {
        return (float) (angle * Math.PI / 180);
    }
    public static int square(int i)
    {
        return i * i;
    }
    public static Vec3d fromTo(Vec3d from, Vec3d to) {
        return fromTo(from.x, from.y, from.z, to);
    }

    public static Vec3d fromTo(Vec3d from, double x, double y, double z) {
        return fromTo(from.x, from.y, from.z, x, y, z);
    }

    public static Vec3d fromTo(double x, double y, double z, Vec3d to) {
        return fromTo(x, y, z, to.x, to.y, to.z);
    }

    public static Vec3d fromTo(double x, double y, double z, double x2, double y2, double z2) {
        return new Vec3d(x2 - x, y2 - y, z2 - z);
    }


    //shoutout a-level maths core stats
    public static float linearInterpolation(float now, float to, float toInterp) {
        now -= (now - to) * clamp(toInterp, 0, 1);
        return now;
    }

}

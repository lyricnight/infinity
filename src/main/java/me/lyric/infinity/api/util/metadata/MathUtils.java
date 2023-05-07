package me.lyric.infinity.api.util.metadata;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtils
{
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }
    public static double distanceTo(final Entity entity) {
        return distanceTo(entity.posX, entity.posY, entity.posZ);
    }

    public static double distanceTo(final BlockPos blockPos) {
        return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static double distanceTo(final Vec3d vec3d) {
        return distanceTo(vec3d.x, vec3d.y, vec3d.z);
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

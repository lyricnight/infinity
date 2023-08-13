package me.lyric.infinity.api.util.gl;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.util.math.Vec3d;

/**
 * @author lyric
 * @apiNote handles interpolation of player
 */
public class Interpolation implements IGlobals {
    public static Vec3d interpolatedEyePos() {
        return mc.player.getPositionEyes(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec() {
        return mc.player.getLook(mc.getRenderPartialTicks());
    }

}

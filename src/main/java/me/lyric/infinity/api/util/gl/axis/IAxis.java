package me.lyric.infinity.api.util.gl.axis;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author lyric
 * for that annoying blockesp
 */
@FunctionalInterface
public interface IAxis {
    void render(AxisAlignedBB bb);

}

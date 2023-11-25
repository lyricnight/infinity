package me.lyric.infinity.api.util.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class BlockUtil implements IGlobals {
    public static List<BlockPos> getBlocksInRadius(double range, boolean movePredict, int predictTicks) {
        final List<BlockPos> posses = new ArrayList<>();
        float xRange = (float)Math.round(range);
        float yRange = (float)Math.round(range);
        float zRange = (float)Math.round(range);
        if (movePredict) {
            xRange += (float)(mc.player.motionX * predictTicks);
            yRange += (float)(mc.player.motionY * predictTicks);
            zRange += (float)(mc.player.motionZ * predictTicks);
        }
        for (float x = -xRange; x <= xRange; ++x) {
            for (float y = -yRange; y <= yRange; ++y) {
                for (float z = -zRange; z <= zRange; ++z) {
                    BlockPos position = mc.player.getPosition().add(x, y, z);
                    if (mc.player.getDistance(position.getX() + 0.5, (position.getY() + 1), position.getZ() + 0.5) <= range) {
                        posses.add(position);
                    }
                }
            }
        }
        return posses;
    }
}
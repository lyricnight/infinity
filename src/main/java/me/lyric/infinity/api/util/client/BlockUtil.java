package me.lyric.infinity.api.util.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockUtil implements IGlobals {

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean canReplace(BlockPos pos) {
        return getState(pos).getMaterial().isReplaceable();
    }

    public static boolean isAir(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }
    public static List<BlockPos> getBlocksInRadius(final double range, final boolean movePredict, final int predictTicks) {
        final List<BlockPos> posses = new ArrayList<>();
        float xRange = (float)Math.round(range);
        float yRange = (float)Math.round(range);
        float zRange = (float)Math.round(range);
        if (movePredict) {
            xRange += (float)(BlockUtil.mc.player.motionX * predictTicks);
            yRange += (float)(BlockUtil.mc.player.motionY * predictTicks);
            zRange += (float)(BlockUtil.mc.player.motionZ * predictTicks);
        }
        for (float x = -xRange; x <= xRange; ++x) {
            for (float y = -yRange; y <= yRange; ++y) {
                for (float z = -zRange; z <= zRange; ++z) {
                    final BlockPos position = BlockUtil.mc.player.getPosition().add((double)x, (double)y, (double)z);
                    if (BlockUtil.mc.player.getDistance(position.getX() + 0.5, (double)(position.getY() + 1), position.getZ() + 0.5) <= range) {
                        posses.add(position);
                    }
                }
            }
        }
        return posses;
    }
}
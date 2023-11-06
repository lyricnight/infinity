package me.lyric.infinity.api.util.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class HoleUtil implements IGlobals {
    public static BlockPos[] holeOffsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
    public static boolean isHole(BlockPos pos) {
        if(pos == null)
        {
            return false;
        }
        boolean isHole = false;
        int amount = 0;
        for (BlockPos p : holeOffsets) {
            if (HoleUtil.mc.world.getBlockState(pos.add(p)).getMaterial().isReplaceable()) continue;
            ++amount;
        }
        if (amount == 5) {
            isHole = true;
        }
        return isHole;
    }

    public static boolean isObbyHole(BlockPos pos) {
        boolean isHole = true;
        int bedrock = 0;
        for (BlockPos off : holeOffsets) {
            Block b = HoleUtil.mc.world.getBlockState(pos.add((Vec3i)off)).getBlock();
            if (!HoleUtil.isSafeBlock(pos.add((Vec3i)off))) {
                isHole = false;
                continue;
            }
            if (b != Blocks.OBSIDIAN && b != Blocks.ENDER_CHEST && b != Blocks.ANVIL) continue;
            ++bedrock;
        }
        if (HoleUtil.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtil.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            isHole = false;
        }
        if (bedrock < 1) {
            isHole = false;
        }
        return isHole;
    }

    public static boolean isBedrockHoles(BlockPos pos) {
        boolean isHole = true;
        for (BlockPos off : holeOffsets) {
            Block b = HoleUtil.mc.world.getBlockState(pos.add((Vec3i)off)).getBlock();
            if (b == Blocks.BEDROCK) continue;
            isHole = false;
        }
        if (HoleUtil.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtil.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            isHole = false;
        }
        return isHole;
    }

    public static Hole isDoubleHole(BlockPos pos) {
        if (HoleUtil.checkOffset(pos, 1, 0)) {
            return new Hole(false, true, pos, pos.add(1, 0, 0));
        }
        if (HoleUtil.checkOffset(pos, 0, 1)) {
            return new Hole(false, true, pos, pos.add(0, 0, 1));
        }
        return null;
    }

    public static boolean checkOffset(BlockPos pos, int offX, int offZ) {
        return HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && HoleUtil.mc.world.getBlockState(pos.add(offX, 0, offZ)).getBlock() == Blocks.AIR && HoleUtil.isSafeBlock(pos.add(0, -1, 0)) && HoleUtil.isSafeBlock(pos.add(offX, -1, offZ)) && HoleUtil.isSafeBlock(pos.add(offX * 2, 0, offZ * 2)) && HoleUtil.isSafeBlock(pos.add(-offX, 0, -offZ)) && HoleUtil.isSafeBlock(pos.add(offZ, 0, offX)) && HoleUtil.isSafeBlock(pos.add(-offZ, 0, -offX)) && HoleUtil.isSafeBlock(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && HoleUtil.isSafeBlock(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    static boolean isSafeBlock(BlockPos pos) {
        return HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST || HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.BEACON;
    }

    public static List<Hole> getHoles(double range, BlockPos playerPos, boolean doubles) {
        ArrayList<Hole> holes = new ArrayList<Hole>();
        List<BlockPos> circle = HoleUtil.getSphere(range, playerPos, true, false);
        for (BlockPos pos : circle) {
            Hole dh;
            if (HoleUtil.mc.world.getBlockState(pos).getBlock() != Blocks.AIR) continue;
            if (HoleUtil.isObbyHole(pos)) {
                holes.add(new Hole(false, false, pos));
                continue;
            }
            if (HoleUtil.isBedrockHoles(pos)) {
                holes.add(new Hole(true, false, pos));
                continue;
            }
            if (!doubles || (dh = HoleUtil.isDoubleHole(pos)) == null || HoleUtil.mc.world.getBlockState(dh.pos1.add(0, 1, 0)).getBlock() != Blocks.AIR && HoleUtil.mc.world.getBlockState(dh.pos2.add(0, 1, 0)).getBlock() != Blocks.AIR) continue;
            holes.add(dh);
        }
        return holes;
    }

    static List<BlockPos> getSphere(double range, BlockPos pos, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int)range;
        while ((double)x <= (double)cx + range) {
            int z = cz - (int)range;
            while ((double)z <= (double)cz + range) {
                int y = sphere ? cy - (int)range : cy;
                while (true) {
                    double d2;
                    double d = y;
                    double d3 = d2 = sphere ? (double)cy + range : (double)cy + range;
                    if (!(d < d2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < range * range) || hollow && dist < (range - 1.0) * (range - 1.0))) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }
    public static class Hole {
        public boolean bedrock;
        public boolean doubleHole;
        public BlockPos pos1;
        public BlockPos pos2;

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1, BlockPos pos2) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
        }
    }
}

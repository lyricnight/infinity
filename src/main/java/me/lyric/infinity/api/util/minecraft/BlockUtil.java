package me.lyric.infinity.api.util.minecraft;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtil implements IGlobals {

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static BlockPos[] getHorizontalOffsets(BlockPos pos) {
        return new BlockPos[] {
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west(),
                pos.down()
        };
    }

    public static int getPlaceAbility(BlockPos pos, boolean raytrace) {
        return getPlaceAbility(pos, raytrace, true);
    }

    public static int getPlaceAbility(BlockPos pos, boolean raytrace, boolean checkForEntities) {
        Block block = getBlock(pos);

        if (!(block instanceof BlockAir
                || block instanceof BlockLiquid
                || block instanceof BlockTallGrass
                || block instanceof BlockFire
                || block instanceof BlockDeadBush
                || block instanceof BlockSnow)) return 0;

        if (raytrace && !raytraceCheck(pos, 0.0f)) return -1;

        if (checkForEntities && checkForEntities(pos)) return 1;

        for (EnumFacing side : getPossibleSides(pos)) {

            if (!canBeClicked(pos.offset(side))) continue;
            return 3;

        }
        return 2;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<>();

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);

            if (!getBlock(neighbor).canCollideCheck(getState(neighbor), false) || canReplace(neighbor)) continue;

            facings.add(side);

        }
        return facings;
    }

    //Checkers

    public static boolean canReplace(BlockPos pos) {
        return getState(pos).getMaterial().isReplaceable();
    }

    public static boolean canPlaceCrystal(BlockPos pos) {
        BlockPos boost = pos.add(0, 1, 0);
        BlockPos boost2 = pos.add(0, 2, 0);

        try {
            return (getBlock(pos) == Blocks.BEDROCK || getBlock(pos) == Blocks.OBSIDIAN) && getBlock(boost) == Blocks.AIR && getBlock(boost2) == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static boolean checkForEntities(BlockPos blockPos) {
        for (Entity entity : mc.world.loadedEntityList) {

            if (entity instanceof EntityItem
                    || entity instanceof EntityEnderCrystal
                    || entity instanceof EntityXPOrb
                    || entity instanceof EntityExpBottle
                    || entity instanceof EntityArrow) {

                continue;
            }

            if (new AxisAlignedBB(blockPos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    public static boolean raytraceCheck(BlockPos pos, float height) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), (float) pos.getY() + height, pos.getZ()), false, true, false) == null;
    }
    public static boolean isAir(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }
    public static List<BlockPos> getSphere(final double radius, AirType airType, EntityPlayer entityPlayer) {
        ArrayList<BlockPos> sphere = new ArrayList<>();
        try
        {
            BlockPos pos = new BlockPos(entityPlayer.getPositionVector());
            int posX = pos.getX();
            int posY = pos.getY();
            int posZ = pos.getZ();
            int radiuss = (int) radius;
            for (int x = posX - radiuss; x <= posX + radius; ++x) {
                for (int z = posZ - radiuss; z <= posZ + radius; ++z) {
                    for (int y = posY - radiuss; y < posY + radius; ++y) {
                        double dist = (posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y);
                        BlockPos position;
                        if (dist < radius * radius) {
                            position = new BlockPos(x, y, z);
                            if ((mc.world.getBlockState(position).getBlock().equals(Blocks.AIR) && airType.equals(AirType.IgnoreAir)) || (!mc.world.getBlockState(position).getBlock().equals(Blocks.AIR) && airType.equals(AirType.OnlyAir)))
                                continue;

                            sphere.add(position);
                        }
                    }
                }
            }
            return sphere;
        }
        catch (NullPointerException e)
        {
            //???
        }
        return null;
    }


    public enum AirType {
        OnlyAir,
        IgnoreAir,
        None
    }
}
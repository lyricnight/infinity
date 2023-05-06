package me.lyric.infinity.api.util.minecraft;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.mixin.mixins.accessors.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class CombatUtil implements IGlobals {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final List<BlockPos> surrounded = Arrays.asList(new BlockPos(0, 0, -1), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1));


    public static boolean isEnemySurrounded(EntityPlayer player) {
        Vec3d playerPos = CombatUtil.interpolateEntity(player);
        BlockPos blockpos = new BlockPos(playerPos.x, playerPos.y, playerPos.z);
        int size = 0;
        for(BlockPos bPos : surrounded) {
            if(CombatUtil.isHard(mc.world.getBlockState(blockpos.add(bPos)).getBlock())) {
                size++;
            }
        }
        return (size == 4);
    }




    public static boolean isHard(Block block) {
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK || block == Blocks.ANVIL || block == Blocks.ENDER_CHEST;
    }

    public static Vec3d interpolateEntity(Entity entity) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks(), entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks(), entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks());
    }

    public static Vec3d getHitVector(BlockPos pos, EnumFacing opposingSide) {
        return new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(opposingSide.getDirectionVec()).scale(0.5));
    }

    public static EnumFacing getPlaceSide(BlockPos blockPos) {
        EnumFacing placeableSide = null;
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos adjacent = blockPos.offset(side);
            if (mc.world.getBlockState(adjacent).getBlock().canCollideCheck(mc.world.getBlockState(adjacent), false) && !mc.world.getBlockState(adjacent).getMaterial().isReplaceable()) {
                placeableSide = side;
            }
        }
        return placeableSide;
    }
    public static boolean isValidPlacePos(boolean isOnePointThirteen, BlockPos blockPos) {
        if(!isOnePointThirteen) {
            final BlockPos boost = blockPos.add(0, 1, 0);
            final BlockPos boost2 = blockPos.add(0, 2, 0);
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        } else {
            final BlockPos boost = blockPos.add(0, 1, 0);
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
        }
    }

    public static HashMap<EnumFacing, Vec3d> cornerFacings = new HashMap<>();

    static {

        // center of top and bottom
        cornerFacings.put(EnumFacing.UP, new Vec3d(0.0f, 0.5f, 0.0f));
        cornerFacings.put(EnumFacing.DOWN, new Vec3d(0.0f, -0.5f, 0.0f));

        // north side
        cornerFacings.put(EnumFacing.NORTH, new Vec3d(0.0f, 0.5f, -0.5f));
        cornerFacings.put(EnumFacing.NORTH, new Vec3d(0.0f, 0.0f, -0.5f));
        cornerFacings.put(EnumFacing.NORTH, new Vec3d(0.0f, -0.5f, -0.5f));

        // east side
        cornerFacings.put(EnumFacing.EAST, new Vec3d(0.5f, 0.5f, 0.0f));
        cornerFacings.put(EnumFacing.EAST, new Vec3d(0.5f, 0.0f, 0.0f));
        cornerFacings.put(EnumFacing.EAST, new Vec3d(0.5f, -0.5f, 0.0f));

        // south side
        cornerFacings.put(EnumFacing.SOUTH, new Vec3d(0.0f, 0.5f, 0.5f));
        cornerFacings.put(EnumFacing.SOUTH, new Vec3d(0.0f, 0.0f, 0.5f));
        cornerFacings.put(EnumFacing.SOUTH, new Vec3d(0.0f, -0.5f, 0.5f));

        // west side
        cornerFacings.put(EnumFacing.WEST, new Vec3d(-0.5f, 0.5f, 0.0f));
        cornerFacings.put(EnumFacing.WEST, new Vec3d(-0.5f, 0.0f, 0.0f));
        cornerFacings.put(EnumFacing.WEST, new Vec3d(-0.5f, -0.5f, 0.0f));
    }
}
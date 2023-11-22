package me.lyric.infinity.manager.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.rotation.RotationUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlacementManager implements IGlobals {
    private static List<BlockPos> tickCache;

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        tickCache = new ArrayList<>();
    }

    public static boolean placeBlock(BlockPos pos, boolean rotate, String type) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        EnumFacing side = getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (!mc.player.isSneaking() && shouldShiftClick(neighbour)) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
        if (rotate)
        {
            float[] rotation = RotationUtil.getRotations(hitVec);
            RotationUtil.doRotation(type, rotation);
        }
        EnumActionResult action = mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        if (mc.player.isSneaking() && shouldShiftClick(neighbour)) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        tickCache.add(pos);
        return action == EnumActionResult.SUCCESS;
    }
    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false) || tickCache.contains(neighbour)) {
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }
        return null;
    }
    public static boolean shouldShiftClick(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();

        TileEntity tileEntity = null;

        for (TileEntity entity : mc.world.loadedTileEntityList) {
            if (!entity.getPos().equals(pos)) continue;
            tileEntity = entity;
            break;
        }
        return tileEntity != null || block instanceof BlockBed || block instanceof BlockContainer || block instanceof BlockDoor || block instanceof BlockTrapDoor || block instanceof BlockFenceGate || block instanceof BlockButton || block instanceof BlockAnvil || block instanceof BlockWorkbench || block instanceof BlockCake || block instanceof BlockRedstoneDiode;
    }
}


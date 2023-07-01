package me.lyric.infinity.manager.client;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.util.*;
import net.minecraft.block.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.*;

import java.util.*;

public class PlacementManager implements IGlobals {
    static List<BlockPos> tickCache;

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUpdate(final TickEvent.ClientTickEvent event) {
        tickCache = new ArrayList<>();
    }

    public static boolean placeBlock(final BlockPos pos, final boolean rotate) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        final EnumFacing side = getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (!mc.player.isSneaking() && shouldShiftClick(neighbour)) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
        if (rotate)
        {
            RotationManager.lookAtVec3dPacket(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true);
        }
        final EnumActionResult action = mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        if (mc.player.isSneaking() && shouldShiftClick(neighbour)) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        tickCache.add(pos);
        return action == EnumActionResult.SUCCESS;
    }
    public static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false) || tickCache.contains(neighbour)) {
                final IBlockState blockState = mc.world.getBlockState(neighbour);
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


package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.blocks.BlockPushOutEvent;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.manager.client.PlacementManager;
import me.lyric.infinity.mixin.mixins.accessors.IEntityPlayerSP;
import me.lyric.infinity.mixin.mixins.accessors.ISPacketPlayerPosLook;
import net.minecraft.block.*;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @author lyric !!
 */

public class Burrow extends Module {
    public Setting<Mode> switchMode = register(new Setting<>("Mode", "Mode for switch", Mode.SILENT));

    private Setting<Boolean> rotate = register(new Setting<>("Rotate","Rotations for placing.", true));
    private Setting<Boolean> swing = register(new Setting<>("Swing","Swing to place the block.", true));
    private Setting<Boolean> strict = new Setting<>("Strict","For stricter anticheats.", false);

    public Burrow() {
        super("Burrow", "this", Category.COMBAT);
    }

    private State state = State.WAITING;
    private Timer timer = new Timer();

    private enum State {
        WAITING,
        DISABLING
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        InventoryUtil.check(this);
        if (state == State.DISABLING) {
            if (timer.hasPassed(500)) {
                toggle();
            }
            return;
        }
        if (!mc.player.onGround) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in the air! Disabling Burrow...");
            toggle();
            return;
        }
        if (mc.world.getBlockState(new BlockPos(mc.player)).getBlock() == Blocks.AIR) {

            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

            BlockPos currentPos = pos.down();
            EnumFacing currentFace = EnumFacing.UP;

            Vec3d vec = new Vec3d(currentPos).add(0.5, 0.5, 0.5).add(new Vec3d(currentFace.getDirectionVec()).scale(0.5));

            if (rotate.getValue()) {
                if (((IEntityPlayerSP) mc.player).getLastReportedPitch() < 0) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 0, true));
                }
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, 90, true));            ((IEntityPlayerSP) mc.player).setLastReportedPosY(mc.player.posY + 1.16);
                ((IEntityPlayerSP) mc.player).setLastReportedPitch(90);
            }

            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42, mc.player.posZ, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.01, mc.player.posZ, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16, mc.player.posZ, mc.player.onGround));

            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());

            int startingItem = mc.player.inventory.currentItem;
            doSwitch(InventoryUtil.findHotbarBlock(BlockObsidian.class));
            PlacementManager.placeBlock(currentPos, rotate.getValue(), rotate.getValue(), true, true, swing.getValue());
            doSwitch(startingItem);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, getPos(), mc.player.posZ, false));
            timer.reset();
            state = State.DISABLING;
        } else {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Burrow was unable to place! Disabling Burrow...");
            toggle();
        }
    }

    private double getPos() {
        if (mc.getCurrentServerData() != null) {
            if (mc.getCurrentServerData().serverIP.toLowerCase().contains("crystalpvp")) {
                return mc.player.posY + 1.8D + (Math.random() * 0.1);
            } else if (mc.getCurrentServerData().serverIP.toLowerCase().contains("endcrystal")) {
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 4D, mc.player.posZ)).getBlock() instanceof BlockAir) {
                    return mc.player.posY + 4D;
                }
                return mc.player.posY + 3D;
            } else if (mc.getCurrentServerData().serverIP.toLowerCase().contains("netheranarchy")) {
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 8.5D, mc.player.posZ)).getBlock() instanceof BlockAir) {
                    return mc.player.posY + 8.5D;
                }
                return mc.player.posY + 9.5D;
            } else if (mc.getCurrentServerData().serverIP.toLowerCase().contains("9b9t")) {
                BlockPos currentPos = new BlockPos(mc.player.posX, mc.player.posY + 9D, mc.player.posZ);
                if (mc.world.getBlockState(currentPos).getBlock() instanceof BlockAir && mc.world.getBlockState(currentPos.up()).getBlock() instanceof BlockAir) {
                    return mc.player.posY + 9D;
                } else {
                    for (int i = 10; i < 20; i++) {
                        BlockPos iPos = new BlockPos(mc.player.posX, mc.player.posY + i, mc.player.posZ);
                        if (mc.world.getBlockState(iPos).getBlock() instanceof BlockAir && mc.world.getBlockState(iPos.up()).getBlock() instanceof BlockAir) {
                            return mc.player.posY + i;
                        }
                    }
                }
                return mc.player.posY + 20D;
            }
        }
        BlockPos currentPos = new BlockPos(mc.player.posX, mc.player.posY - 9D, mc.player.posZ);
        if (mc.world.getBlockState(currentPos).getBlock() instanceof BlockAir && mc.world.getBlockState(currentPos.up()).getBlock() instanceof BlockAir) {
            return mc.player.posY - 9D;
        } else {
            for (int i = -10; i > -20; i--) {
                BlockPos iPos = new BlockPos(mc.player.posX, mc.player.posY - i, mc.player.posZ);
                if (mc.world.getBlockState(iPos).getBlock() instanceof BlockAir && mc.world.getBlockState(iPos.up()).getBlock() instanceof BlockAir) {
                    return mc.player.posY - i;
                }
            }
        }
        return mc.player.posY - 24D;
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.currentScreen instanceof GuiDownloadTerrain) {
            toggle();
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && !strict.getValue()) {
            ((ISPacketPlayerPosLook) event.getPacket()).setYaw(mc.player.rotationYaw);
            ((ISPacketPlayerPosLook) event.getPacket()).setPitch(mc.player.rotationPitch);
        }
    }

    @EventListener
    public void onBlockPushOut(BlockPushOutEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }
        if (!mc.player.onGround) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in the air! Disabling Burrow...");
            toggle();
            return;
        }
        state = State.WAITING;
    }
    public void doSwitch(final int i) {
        if (switchMode.getValue() == Mode.NORMAL) {
            Switch.switchToSlot(i);
        }
        if (switchMode.getValue() == Mode.SILENT) {
            Switch.switchToSlotGhost(i);
        }
    }
    public enum Mode
    {
        NORMAL,
        SILENT
    }
}
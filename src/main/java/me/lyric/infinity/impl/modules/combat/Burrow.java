package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import me.lyric.infinity.api.setting.settings.BooleanSetting;
import me.lyric.infinity.api.setting.settings.ModeSetting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.api.util.time.Timer;
import me.lyric.infinity.impl.modules.movement.InstantSpeed;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.mixin.mixins.accessors.IEntityPlayerSP;
import me.lyric.infinity.mixin.mixins.accessors.ISPacketPlayerPosLook;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

/**
 * @author lyric !!
 */

@ModuleInformation(name = "Burrow", description = "this", category = Category.Combat)
public class Burrow extends Module {
    public ModeSetting switchMode = createSetting("SwitchMode","Silent",  Arrays.asList("Silent", "SilentPacket", "Slot"));

    public BooleanSetting rotate = createSetting("Rotate", true);
    public BooleanSetting swing = createSetting("Swing", true);
    public BooleanSetting strict = createSetting("Strict", false);

    public BooleanSetting cd = createSetting("Slot-Cooldown", false);

    private Timer timer = new Timer();

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        InventoryUtil.check(this);
        if (!mc.player.onGround) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in the air! Disabling Burrow...");
            disable();
            return;
        }
        if(CombatUtil.isBurrow(mc.player))
        {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "You are already burrowed! Disabling...");
            disable();
            return;
        }
        if (mc.world.getBlockState(new BlockPos(mc.player)).getBlock() == Blocks.AIR) {
            Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = true;

            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

            BlockPos currentPos = pos.down();
            EnumFacing currentFace = EnumFacing.UP;

            Vec3d vec = new Vec3d(currentPos).add(0.5, 0.5, 0.5).add(new Vec3d(currentFace.getDirectionVec()).scale(0.5));

            if (rotate.getValue()) {
                if (((IEntityPlayerSP) mc.player).getLastReportedPitch() < 0) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 0, true));
                }
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, 90, true));
                ((IEntityPlayerSP) mc.player).setLastReportedPosY(mc.player.posY + 1.16);
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
            if (InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1)
            {
                return;
            }
            int slot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            Switch.doSwitch(slot, switchMode.getValue());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(currentPos, currentFace, EnumHand.MAIN_HAND, f, f1, f2));
            if (swing.getValue()) {
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
            if (switchMode.getValue() == "Slot")
            {
                if (cd.getValue())
                {
                    Switch.switchBackAlt(slot);
                }
                else
                {
                    Switch.doSwitch(slot, "Slot");
                }
            }
            else
            {
                Switch.doSwitch(startingItem, switchMode.getValue());
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, getPos(), mc.player.posZ, false));
            timer.reset();
            disable();
        } else {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Burrow was unable to place! Disabling Burrow...");
            disable();
        }
    }
    @Override
    public void onDisable()
    {
        Managers.MODULES.getModuleByClass(InstantSpeed.class).pause = false;
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
            disable();
            return;
        }
        //im retarded why did i make this loooool
        if (event.getPacket() instanceof SPacketPlayerPosLook && !strict.getValue()) {
            ((ISPacketPlayerPosLook) event.getPacket()).setYaw(mc.player.rotationYaw);
            ((ISPacketPlayerPosLook) event.getPacket()).setPitch(mc.player.rotationPitch);
        }
    }
    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }
        if (!mc.player.onGround) {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "Player is in the air! Disabling Burrow...");
            disable();
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBlockPush(PlayerSPPushOutOfBlocksEvent event)
    {
        event.setCanceled(true);
    }
}
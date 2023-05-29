package me.lyric.infinity.impl.modules.combat;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.client.CombatUtil;
import me.lyric.infinity.api.util.client.HoleUtil;
import me.lyric.infinity.api.util.client.InventoryUtil;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.api.util.minecraft.switcher.Switch;
import me.lyric.infinity.manager.client.PlacementManager;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * @author asphyxia
 * face and extend conflict bug fixed by lyric, as well as addition of diagonals, also making it place on blockchange
 */

public class Blocker extends Module {

    private final Setting<Boolean> extend = register(new Setting<>("Extend","bot",  true));
    private final Setting<Boolean> face = register(new Setting<>("Face","bot", true));
    private final Setting<Boolean> diag = register(new Setting<>("Diagonals","bot", true));
    private final Setting<Boolean> rotate = register(new Setting<>("Rotate","bot", false));
    private final Setting<Boolean> packet = register(new Setting<>("Packet","bot", true).withParent(rotate));

    public Blocker() {
        super("Blocker", "bot", Category.COMBAT);
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockBreakAnim && HoleUtil.isInHole(RotationManager.getPlayerPos())) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();
            BlockPos pos = packet.getPosition();

            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            BlockPos placePos = null;
            BlockPos placePos2 = null;
            BlockPos placePos3 = null;
            if (diag.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos3 = (playerPos.north().west());

                if (pos.equals(playerPos.west()))
                    placePos3 = (playerPos.north().east());

                if (pos.equals(playerPos.east()))
                    placePos3 = (playerPos.south().east());

                if (pos.equals(playerPos.south()))
                    placePos3 = (playerPos.south().west());
            }

            if (extend.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().north());

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().east());

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().west());

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().south());
            }

            if (face.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos2 = (playerPos.north().add(0, 1, 0));

                if (pos.equals(playerPos.east()))
                    placePos2 = (playerPos.east().add(0, 1, 0));

                if (pos.equals(playerPos.west()))
                    placePos2 = (playerPos.west().add(0, 1, 0));

                if (pos.equals(playerPos.south()))
                    placePos2 = (playerPos.south().add(0, 1, 0));
            }

            if (placePos != null) {
                placeBlock(placePos);
            }
            if (placePos2 != null)
            {
                placeBlock(placePos2);
            }
            if (placePos3 != null)
            {
                placeBlock(placePos3);
            }
        }

    }
    //TODO: THIS IS horrible
    //why did asphy do it like this in the first place
    @EventListener(priority = ListenerPriority.HIGH)
    public void onPacketReceive2(PacketEvent.Receive event)
    {
        if (event.getPacket() instanceof SPacketBlockChange && HoleUtil.isInHole(RotationManager.getPlayerPos())) {
            SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();
            BlockPos pos = packet.getBlockPosition();

            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            BlockPos placePos = null;
            BlockPos placePos2 = null;
            BlockPos placePos3 = null;
            if (diag.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos3 = (playerPos.north().west());

                if (pos.equals(playerPos.west()))
                    placePos3 = (playerPos.north().east());

                if (pos.equals(playerPos.east()))
                    placePos3 = (playerPos.south().east());

                if (pos.equals(playerPos.south()))
                    placePos3 = (playerPos.south().west());
            }

            if (extend.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().north());

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().east());

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().west());

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().south());
            }

            if (face.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos2 = (playerPos.north().add(0, 1, 0));

                if (pos.equals(playerPos.east()))
                    placePos2 = (playerPos.east().add(0, 1, 0));

                if (pos.equals(playerPos.west()))
                    placePos2 = (playerPos.west().add(0, 1, 0));

                if (pos.equals(playerPos.south()))
                    placePos2 = (playerPos.south().add(0, 1, 0));
            }

            if (placePos != null) {
                placeBlock(placePos);
            }
            if (placePos2 != null)
            {
                placeBlock(placePos2);
            }
            if (placePos3 != null)
            {
                placeBlock(placePos3);
            }
        }
    }
    @Override
    public void onUpdate()
    {
        if(mc.player == null)
        {
            return;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

        if (obbySlot == -1 && eChestSlot == -1)
        {
            ChatUtils.sendMessage("No Obsidian or EChests! Disabling Blocker...");
            toggle();
        }
    }

    private void placeBlock(BlockPos pos){
        if (!mc.world.isAirBlock(pos)) return;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        CombatUtil.attack(pos);
        Switch.placeBlockWithSwitch(obbySlot == -1 ? eChestSlot : obbySlot, rotate.getValue(), packet.getValue(), pos, true);
    }
}
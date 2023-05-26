package me.lyric.infinity.impl.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.bush.eventbus.annotation.ListenerPriority;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.util.minecraft.BlockUtil;
import me.lyric.infinity.api.util.minecraft.InventoryUtil;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.player.UpdateWalkingPlayerEventPost;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.setting.Setting;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import me.lyric.infinity.manager.client.PlacementManager;
import me.lyric.infinity.manager.client.RotationManager;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

import static net.minecraft.util.EnumHand.MAIN_HAND;


public class Burrow extends Module {

    public Burrow()
    {
        super("Burrow", "the thing", Category.COMBAT);
    }
    private final Setting<Float> ydir = register(new Setting<>("LagHeight", "How much to move in y-dir for the burrow", 1.0f, -5.0f, 5.0f));
    private final Setting<Mode> prefer = register(new Setting<>("Blocks", "Which block to prefer.", Mode.OBSIDIAN));
    private final Setting<Boolean> cancelRotations = register(new Setting<>("RotationCancel", "Attempts to cancel all other rotations at time of burrow", false));
    private final Setting<Boolean> strict = register(new Setting<>("Strict", "lol", false));
    private final Setting<Boolean> rotate = register(new Setting<>("Rotate", "Whether to rotate to place or not.", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet","Packet rotations to prevent glitch blocks, may be slower", false).withParent(rotate));

    private final float[] offsets = new float[]{0.41f, 0.75f, 1.00f, 1.16f};
    private BlockPos startPos;

    @EventListener
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEventPost event) {
        if (!BlockUtil.isABlock(startPos, Blocks.OBSIDIAN) && !BlockUtil.isABlock(RotationManager.getPlayerPos(), Blocks.ENDER_CHEST)) {
            int slot;
            if (prefer.getValue().equals(Mode.OBSIDIAN)) {
                int obsidian = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                slot = obsidian != -1 ? obsidian : InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            } else {
                int enderChest = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
                slot = enderChest != -1 ? enderChest : InventoryUtil.findHotbarBlock(BlockObsidian.class);
            }
            if (slot == -1) {
                ChatUtils.sendMessage(ChatFormatting.BOLD + "No Obsidian or EChests! Disabling Burrow...");
                toggle();
                return;
            }

            for (float f : offsets) {
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + f, mc.player.posZ, true));
            }
            if (strict.getValue()) {
                event.setPitch(90);
            }

            int currentItem = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
            PlacementManager.placeBlock(startPos, rotate.getValue(), packet.getValue(), true);

            if (mc.player.inventory.currentItem != currentItem) {
                mc.player.inventory.currentItem = currentItem;
                mc.playerController.updateController();
            }
            mc.player.swingArm(MAIN_HAND);
            mc.player.inventory.currentItem = currentItem;

            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + ydir.getValue(), mc.player.posZ, false));

        } else {
            if (!cancelRotations.getValue()) {
                toggle();
            } else {
                if (!startPos.equals(RotationManager.getPlayerPos())
                        || mc.player.posY > mc.player.prevPosY
                        || mc.gameSettings.keyBindJump.isKeyDown()) {
                    toggle();
                }
            }
        }
    }

    @EventListener(priority = ListenerPriority.HIGH)
    public void onPacketSend(PacketEvent.Send event) {
        if (cancelRotations.getValue() && (BlockUtil.isABlock(startPos, Blocks.OBSIDIAN) || BlockUtil.isABlock(RotationManager.getPlayerPos(), Blocks.ENDER_CHEST))) {
            if (event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onEnable() {
        startPos = RotationManager.getPlayerPos();
    }
    public enum Mode
    {
        OBSIDIAN,
        ENDER_CHEST
    }
}
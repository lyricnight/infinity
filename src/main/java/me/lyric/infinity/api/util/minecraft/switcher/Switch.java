package me.lyric.infinity.api.util.minecraft.switcher;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.manager.client.PlacementManager;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.util.EnumHand.MAIN_HAND;

public class Switch implements IGlobals {
    public static void placeBlockWithSwitch(int slot, boolean rotate, boolean packet, BlockPos pos, boolean swing)
    {
        int originalSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slot;

        mc.playerController.updateController();
        PlacementManager.placeBlock(pos, rotate, packet, true, swing);

        if (mc.player.inventory.currentItem != originalSlot) {
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
        }
        if (swing)
        {
            mc.player.swingArm(MAIN_HAND);
        }
        mc.player.inventory.currentItem = originalSlot;
    }
    public static void switchToSlot(final int slot) {
        mc.player.inventory.currentItem = slot;
    }
    public static void switchToSlotGhost(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }
}

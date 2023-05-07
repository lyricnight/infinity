package me.lyric.infinity.api.util.minecraft;

import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.item.*;

public class Switch implements IGlobals
{
    public static int getHotbarItemSlot2(final Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                slot = i;
                break;
            }
        }
        if (slot == -1) {
            return mc.player.inventory.currentItem;
        }
        return slot;
    }

    public static void switchToSlot(final int slot) {
        mc.player.inventory.currentItem = slot;
    }

    public static void switchToSlot(final Item item) {
        mc.player.inventory.currentItem = getHotbarItemSlot2(item);
    }

    public static void switchToSlotGhost(final int slot) {
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(slot));
    }

    public static void switchToSlotGhost(final Item item) {
        switchToSlotGhost(getHotbarItemSlot2(item));
    }


}

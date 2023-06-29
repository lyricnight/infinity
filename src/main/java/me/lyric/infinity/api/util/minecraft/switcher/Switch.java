package me.lyric.infinity.api.util.minecraft.switcher;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.network.play.client.CPacketHeldItemChange;

/**
 * @author lyriccc
 */

public class Switch implements IGlobals {
    public static void switchToSlot(final int slot) {
        mc.player.inventory.currentItem = slot;
    }
    public static void switchToSlotGhost(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }
}

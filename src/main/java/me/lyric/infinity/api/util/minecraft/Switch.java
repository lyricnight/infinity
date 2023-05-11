package me.lyric.infinity.api.util.minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class Switch implements IGlobals
{
    public static void switchToSlotGhost(final int slot) {
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(slot));
    }

}

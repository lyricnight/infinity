package me.lyric.infinity.api.util.minecraft.switcher;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketHeldItemChange;

/**
 * @author lyriccc
 */

public class Switch implements IGlobals {
    //TODO: Fix slot modes

    public static void doSwitch(int slot, SwitchType type)
    {
        if (mc.player == null || mc.world == null) return;
        if (type == SwitchType.SILENT)
        {
            mc.player.inventory.currentItem = slot;
            ((IPlayerControllerMP)mc.playerController).syncItem();
            return;
        }
        if (type == SwitchType.SILENTPACKET)
        {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            ((IPlayerControllerMP)mc.playerController).syncItem();
            return;
        }
        if(type == SwitchType.SLOT)
        {
            //slot = convert(slot);
            //if (mc.player.inventory.currentItem != slot && slot > 35 && slot < 45) {
               // mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
           // }
        }
    }
    public static int convert(int slot) {
        if (slot == -2) {
            return 45;
        }

        if (slot > -1 && slot < 9) {
            return 36 + slot;
        }

        return slot;
    }
}

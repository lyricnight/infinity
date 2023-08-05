package me.lyric.infinity.api.util.minecraft.switcher;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.mixin.mixins.accessors.IPlayerControllerMP;
import net.minecraft.network.play.client.CPacketHeldItemChange;

/**
 * @author lyriccc
 */

public class Switch implements IGlobals {
    //TODO: Fix slot modes

    public static void doSwitch(final int slot, SwitchType type)
    {
        if (mc.player == null || mc.world == null) return;
        if (type == SwitchType.NORMAL)
        {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
            return;
        }
        if (type == SwitchType.SILENT)
        {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            ((IPlayerControllerMP)mc.playerController).syncItem();
            return;
        }
        if(type == SwitchType.SLOT)
        {
            //mc.playerController.windowClick(0, convert(slot),mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            //((IPlayerControllerMP)mc.playerController).syncItem();
        }
        if (type == SwitchType.ALTSLOT)
        {
            //short tid = mc.player.openContainer.getNextTransactionID(mc.player.inventory);
           //ItemStack stack = mc.player.openContainer.slotClick(convert(slot), mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

           // mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.inventoryContainer.windowId, convert(slot), mc.player.inventory.currentItem, ClickType.SWAP, stack, tid));
            //((IPlayerControllerMP)mc.playerController).syncItem();
        }
    }
    private static int convert(int slot) {
        if (slot == -2) {
            return 45;
        }

        if (slot > -1 && slot < 9) {
            return 36 + slot;
        }

        return slot;
    }
}

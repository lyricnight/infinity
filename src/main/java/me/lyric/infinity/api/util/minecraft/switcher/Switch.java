package me.lyric.infinity.api.util.minecraft.switcher;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;
import net.minecraft.network.play.client.CPacketHeldItemChange;

/**
 * @author lyriccc
 */

public class Switch implements IGlobals {
    //TODO: Fix slot modes

    public static void doSwitch(int slot, SwitchType type)
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
            //implementing this is such a fucking pain
            /**
            int startSlot = mc.player.inventory.currentItem;
            slot = convert(slot);
            if (mc.player.inventory.currentItem != slot && slot > 35 && slot < 45) {
                mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            }
            ItemStack newItem = mc.player.getHeldItemMainhand();
            short id = mc.player.openContainer.getNextTransactionID(mc.player.inventory);
            ItemStack fakeStack = new ItemStack(Items.END_CRYSTAL, 64);
            int newSlot = convert(slot);
            int altSlot = convert(startSlot);
            Slot currentSlot = mc.player.inventoryContainer.inventorySlots.get(altSlot);
            Slot swapSlot = mc.player.inventoryContainer.inventorySlots.get(newSlot);
            NetHandlerPlayClient conn = mc.getConnection();
            if (conn != null)
            {
                conn.sendPacket(new CPacketClickWindow(0, newSlot, mc.player.inventory.currentItem, ClickType.SWAP, fakeStack, id));
            }
            currentSlot.putStack(oldItem);
            swapSlot.putStack(newItem);
             */
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

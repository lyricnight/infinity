package me.lyric.infinity.api.util.minecraft.switcher;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketHeldItemChange;

/**
 * @author lyriccc
 */

public class Switch implements IGlobals {
    private static ItemStack stackAlt = null;

    private static int lastSlot = -1;

    private static ItemStack stackAltNew = null;

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
            lastSlot = mc.player.inventory.currentItem;
            stackAlt = mc.player.getHeldItemMainhand();
            slot = convert(slot);
            if (mc.player.inventory.currentItem != slot && slot > 35 && slot < 45) {
               mc.playerController.windowClick(0, slot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            }
            stackAltNew = mc.player.getHeldItemMainhand();
        }
    }

    public static void switchBackAlt(int slot)
    {
        short id = mc.player.openContainer.getNextTransactionID(mc.player.inventory);
        ItemStack fakeStack = new ItemStack(Items.END_CRYSTAL, 64);
        int newSlot = convert(slot);
        int altSlot = convert(lastSlot);
        Slot currentSlot = mc.player.inventoryContainer.inventorySlots.get(altSlot);
        Slot swapSlot = mc.player.inventoryContainer.inventorySlots.get(newSlot);
        mc.player.connection.sendPacket(new CPacketClickWindow(0, newSlot, mc.player.inventory.currentItem, ClickType.SWAP, fakeStack, id));
        currentSlot.putStack(stackAlt);
        swapSlot.putStack(stackAltNew);
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

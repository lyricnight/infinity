package me.lyric.infinity.api.util.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
public class InventoryUtil implements IGlobals {
    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }
    public static int findHotbar(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            return i;
        }
        return -1;
    }

    public static void check(Module module)
    {
        if(mc.player == null)
        {
            return;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

        if (obbySlot == -1 && eChestSlot == -1)
        {
            ChatUtils.sendMessage(ChatFormatting.BOLD + "No Obsidian or EChests! Disabling " + module.getName() + "!");
            module.toggle();
        }
    }

}
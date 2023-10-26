package me.lyric.infinity.api.util.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.api.util.minecraft.chat.ChatUtils;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

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
    public static ItemStack get(int slot) {
        if (slot == -2) {
            return mc.player.inventory.getItemStack();
        }

        return mc.player.inventoryContainer.getInventory().get(slot);
    }

    public static void put(int slot, ItemStack stack) {
        if (slot == -2) {
            mc.player.inventory.setItemStack(stack);
        }

        mc.player.inventoryContainer.putStackInSlot(slot, stack);
    }
    public static boolean isHolding(Item item) {
        return isHolding(mc.player, item);
    }

    public static boolean isHolding(EntityLivingBase entity, Item item) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand  = entity.getHeldItemOffhand();
        return areSame(mainHand, item) || areSame(offHand, item);
    }
    public static boolean areSame(ItemStack stack, Item item) {
        return stack != null && areSame(stack.getItem(), item);
    }
    public static boolean areSame(Item item1, Item item2) {
        return Item.getIdFromItem(item1) == Item.getIdFromItem(item2);
    }
    public static void click(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }
    public static boolean validScreen() {
        return !(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory;
    }
    public static EnumHand getHand(Item item) {
        return mc.player.getHeldItemMainhand().getItem() == item ? EnumHand.MAIN_HAND : mc.player.getHeldItemOffhand().getItem() == item ? EnumHand.OFF_HAND : null;
    }

    public static boolean equals(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null) {
            return stack2 == null;
        } else if (stack2 == null) {
            return false;
        }

        boolean empty1 = stack1.isEmpty();
        boolean empty2 = stack2.isEmpty();

        return empty1 == empty2
                && stack1.getDisplayName().equals(stack2.getDisplayName())
                && stack1.getItem() == stack1.getItem()
                && stack1.getHasSubtypes() == stack2.getHasSubtypes()
                && stack1.getMetadata() == stack2.getMetadata()
                && ItemStack.areItemStackTagsEqual(stack1, stack2);
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
package me.lyric.infinity.mixin.transformer;

import net.minecraft.item.ItemStack;

public interface IItemRenderer
{
    float getprevEquippedProgressMainHand();

    void setequippedProgressMainHand(float progress);

    void setitemStackMainHand(ItemStack itemStack);
}
package me.lyric.infinity.mixin.mixins.render;


import me.lyric.infinity.mixin.transformer.IItemRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = {ItemRenderer.class})
public abstract class MixinItemRenderer implements IItemRenderer {
    @Override
    @Accessor(value = "prevEquippedProgressMainHand")
    public abstract float getprevEquippedProgressMainHand();

    @Override
    @Accessor(value = "equippedProgressMainHand")
    public abstract void setequippedProgressMainHand(float progress);

    @Override
    @Accessor(value = "itemStackMainHand")
    public abstract void setitemStackMainHand(ItemStack itemStack);

}

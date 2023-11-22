package me.lyric.infinity.mixin.mixins.render;

import me.lyric.infinity.impl.modules.render.NoRender;
import me.lyric.infinity.manager.Managers;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {
    @Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
    public void renderArmorLayer(EntityLivingBase p_Entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo callbackInfo) {
        if(Managers.MODULES.getModuleByClass(NoRender.class).isEnabled() && Managers.MODULES.getModuleByClass(NoRender.class).NoArmor.getValue()) {
            callbackInfo.cancel();
        }
    }
}

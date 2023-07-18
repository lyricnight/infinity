package me.lyric.infinity.mixin.mixins.render;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.render.crystal.CrystalTextureEvent;
import me.lyric.infinity.api.event.render.crystal.RenderCrystalPostEvent;
import me.lyric.infinity.api.event.render.crystal.RenderCrystalPreEvent;
import me.lyric.infinity.impl.modules.render.CModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderEnderCrystal.class, priority = 0x7FFFFFFE)
public class MixinRenderEnderCrystal {

    @Final
    @Shadow
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    @Final
    @Shadow
    private ModelBase modelEnderCrystal;
    @Final
    @Shadow
    private ModelBase modelEnderCrystalNoBase;

    @Redirect(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void doRender(ModelBase modelBase, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderCrystalPreEvent renderCrystalEvent = new RenderCrystalPreEvent(modelBase, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Infinity.INSTANCE.eventBus.post(renderCrystalEvent);
        if (!renderCrystalEvent.isCancelled() && Infinity.INSTANCE.moduleManager.getModuleByClass(CModifier.class).isDisabled()) {
            modelBase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        CrystalTextureEvent crystalTextureEvent = new CrystalTextureEvent();
        MinecraftForge.EVENT_BUS.post(crystalTextureEvent);
    }

    @Inject(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = {@At(value = "RETURN")}, cancellable = true)
    public void doRender(EntityEnderCrystal entityEnderCrystal, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        RenderCrystalPostEvent renderCrystalEvent = new RenderCrystalPostEvent(this.modelEnderCrystal, this.modelEnderCrystalNoBase, entityEnderCrystal, x, y, z, entityYaw, partialTicks);
        Infinity.INSTANCE.eventBus.post(renderCrystalEvent);
        if(Infinity.INSTANCE.moduleManager.getModuleByClass(CModifier.class).isEnabled())
        {

            Minecraft mc = Minecraft.getMinecraft();
            mc.gameSettings.fancyGraphics = false;
            GL11.glPushMatrix();
            float i = Infinity.INSTANCE.moduleManager.getModuleByClass(CModifier.class).scale.getValue();
            float j = Infinity.INSTANCE.moduleManager.getModuleByClass(CModifier.class).spinSpeed.getValue();
            float k = Infinity.INSTANCE.moduleManager.getModuleByClass(CModifier.class).bounceFactor.getValue();
            float mod1 = entityEnderCrystal.innerRotation + partialTicks;
            float mod2 = MathHelper.sin((mod1 * 0.2f)) / 2.0f + 0.5f;
            mod2 += mod2 * mod2;
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(i, i, i);
            if(entityEnderCrystal.shouldShowBottom())
            {
                modelEnderCrystal.render(entityEnderCrystal, 0.0f,mod1 * j, mod2 * k, 0.0f, 0.0f, 0.0625f);
            }
            else
            {
                modelEnderCrystalNoBase.render(entityEnderCrystal, 0.0f,mod1 * j,mod2 * k, 0.0f, 0.0f, 0.0625f);
            }
            GL11.glPopMatrix();
        }
        if (renderCrystalEvent.isCancelled()) {
            info.cancel();
        }
    }
}

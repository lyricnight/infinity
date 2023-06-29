package me.lyric.infinity.mixin.mixins.render;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.render.RenderLivingEntityEvent;
import me.lyric.infinity.impl.modules.render.PlayerChams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderLivingBase.class)
public class MixinRenderLivingBase {

    @Shadow
    protected ModelBase mainModel;

    @Inject(method = {"renderModel"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V")}, cancellable = true)
    private void renderModel(EntityLivingBase entityLivingBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo info) {
        RenderLivingEntityEvent renderLivingEntityEvent = new RenderLivingEntityEvent(this.mainModel, entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        Infinity.INSTANCE.eventBus.post(renderLivingEntityEvent);
        if (renderLivingEntityEvent.isCancelled()) {
            info.cancel();
        }
    }
    @Inject(method = {"doRender"}, at = @At("HEAD"))
    public void doRenderPre(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(PlayerChams.class).isValid(entity) && Infinity.INSTANCE.moduleManager.getModuleByClass(PlayerChams.class).isEnabled()) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0f, -1100000.0f);
        }
    }

    @Inject(method = {"doRender"}, at = @At("RETURN"))
    public void doRenderPost(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(PlayerChams.class).isValid(entity) && Infinity.INSTANCE.moduleManager.getModuleByClass(PlayerChams.class).isEnabled()) {
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0f, 1100000.0f);
        }
    }
}
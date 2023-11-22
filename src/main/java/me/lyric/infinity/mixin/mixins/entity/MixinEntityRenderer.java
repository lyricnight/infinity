package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.render.AspectEvent;
import me.lyric.infinity.api.event.render.RenderNametagEvent;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.impl.modules.render.Ambience;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.mixin.transformer.IEntityRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author lyric
 */

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer implements IEntityRenderer, IGlobals {

    @Inject(method = "drawNameplate", at = @At("HEAD"), cancellable = true)
    private static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, CallbackInfo ci) {
        RenderNametagEvent event = new RenderNametagEvent();
        Infinity.eventBus.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Shadow
    private void setupCameraTransform(float partialTicks, int pass) {
    }

    @Override
    public void setupCamera(float partialTicks, int pass) {
        setupCameraTransform(partialTicks, pass);
    }

    @ModifyVariable(method = "updateLightmap", at = @At(value="STORE"), index = 20)
    public int red(int red) {
        if (Managers.MODULES.getModuleByClass(Ambience.class).isEnabled()) {
            red = Managers.MODULES.getModuleByClass(Ambience.class).color.getValue().getRed();
        }
        return red;
    }

    @ModifyVariable(method = "updateLightmap", at = @At(value = "STORE"), index = 21)
    public int green(int green) {
        if (Managers.MODULES.getModuleByClass(Ambience.class).isEnabled()) {
            green = Managers.MODULES.getModuleByClass(Ambience.class).color.getValue().getGreen();
        }
        return green;
    }

    @ModifyVariable(method = "updateLightmap", at = @At(value="STORE"), index = 22)
    public int blue(int blue) {
        if (Managers.MODULES.getModuleByClass(Ambience.class).isEnabled()) {
            blue = Managers.MODULES.getModuleByClass(Ambience.class).color.getValue().getBlue();
        }
        return blue;
    }


    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent event = new AspectEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Infinity.eventBus.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent event = new AspectEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Infinity.eventBus.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent event = new AspectEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Infinity.eventBus.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
}
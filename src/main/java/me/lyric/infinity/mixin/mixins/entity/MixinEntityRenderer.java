package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.render.AspectEvent;
import me.lyric.infinity.api.event.render.RenderNametagEvent;
import me.lyric.infinity.impl.modules.render.CameraClip;
import me.lyric.infinity.mixin.transformer.IEntityRenderer;
import net.minecraft.client.Minecraft;
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
public class MixinEntityRenderer implements IEntityRenderer {

    // TODO: IM FUCKING RETARDED
    Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "drawNameplate", at = @At("HEAD"), cancellable = true)
    private static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, CallbackInfo ci) {
        RenderNametagEvent event = new RenderNametagEvent();
        Infinity.INSTANCE.eventBus.post(event);
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

    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent event = new AspectEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Infinity.INSTANCE.eventBus.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent event = new AspectEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Infinity.INSTANCE.eventBus.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        AspectEvent event = new AspectEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Infinity.INSTANCE.eventBus.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
    //what

    @ModifyVariable(method = "orientCamera", ordinal = 3, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double changeCameraDistanceHook(double range) {
        return Infinity.INSTANCE.moduleManager.getModuleByClass(CameraClip.class).isEnabled() && Infinity.INSTANCE.moduleManager.getModuleByClass(CameraClip.class).extend.getValue()
                ? Infinity.INSTANCE.moduleManager.getModuleByClass(CameraClip.class).distance.getValue()
                : range;
    }

    @ModifyVariable(method = "orientCamera", ordinal = 7, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double orientCameraHook(double range) {
        return Infinity.INSTANCE.moduleManager.getModuleByClass(CameraClip.class).isEnabled()
                ? Infinity.INSTANCE.moduleManager.getModuleByClass(CameraClip.class).extend.getValue()
                ? Infinity.INSTANCE.moduleManager.getModuleByClass(CameraClip.class).distance.getValue()
                : 4.0
                : range;
    }
}
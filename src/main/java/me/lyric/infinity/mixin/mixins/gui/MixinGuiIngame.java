package me.lyric.infinity.mixin.mixins.gui;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.render.crosshair.CrosshairEvent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author lyric
 */

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {
    @Inject(method = "renderAttackIndicator", at = @At("HEAD"), cancellable = true)
    protected void renderAttackIndicator(float partialTicks, ScaledResolution p_184045_2_, CallbackInfo ci) {
        final CrosshairEvent event = new CrosshairEvent();
        Infinity.eventBus.post(event);
        if (event.isCancelled())
            ci.cancel();
    }
}
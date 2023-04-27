package me.lyric.infinity.mixin.mixins.render;

import me.lyric.infinity.impl.modules.render.Swing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ItemRenderer.class})
public abstract class MixinItemRenderer {

    @Shadow
    @Final
    public Minecraft mc;

    @Inject(method = "rotateArm", at = @At("HEAD"), cancellable = true)
    public void rotateArmHook(float partialTicks, CallbackInfo info) {
        Swing mod = Swing.INSTANCE;

        if (mod.isEnabled() && mod.noSway.getValue()) {
            info.cancel();
        }
    }
}

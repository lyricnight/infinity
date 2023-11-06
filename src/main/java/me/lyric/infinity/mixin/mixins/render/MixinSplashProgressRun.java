package me.lyric.infinity.mixin.mixins.render;

import me.lyric.infinity.api.util.gl.SplashProgress;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="net.minecraftforge.fml.client.SplashProgress$2", remap=false)
public abstract class MixinSplashProgressRun {
    @Inject(method="run()V", at = @At(value="HEAD"), remap = false, cancellable = true)
    private void run(@NotNull CallbackInfo callbackInfo) {
        callbackInfo.cancel();
        SplashProgress.drawSplash();
    }

}
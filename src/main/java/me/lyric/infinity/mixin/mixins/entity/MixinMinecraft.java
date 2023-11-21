package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.misc.GameLoopEvent;
import me.lyric.infinity.api.util.gl.SplashProgress;
import me.lyric.infinity.impl.modules.client.Internals;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;


@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {
    @Override
    @Accessor(value = "timer")
    public abstract Timer getTimer();
    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0, shift = At.Shift.AFTER))
    private void post_ScheduledTasks(CallbackInfo callbackInfo)
    {
        GameLoopEvent event = new GameLoopEvent();
        Infinity.INSTANCE.eventBus.post(event);
    }
    @Inject(method={"getLimitFramerate"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLimitFramerateHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        try {
            if (Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).unf.getValue() && !Display.isActive()) {
                callbackInfoReturnable.setReturnValue(Infinity.INSTANCE.moduleManager.getModuleByClass(Internals.class).fps.getValue());
            }
        }
        catch (NullPointerException nullPointerException) {
            //LOL!
        }
    }
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void step1(CallbackInfo ci) {
        SplashProgress.setProgress(1, "textures");
    }
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
    public void step2(CallbackInfo ci) {
        SplashProgress.setProgress(4, "Gui");
    }
}

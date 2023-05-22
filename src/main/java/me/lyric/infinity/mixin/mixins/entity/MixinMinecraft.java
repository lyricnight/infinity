package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.network.GameLoopEvent;
import me.lyric.infinity.api.event.network.TickEvent;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {
    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0, shift = At.Shift.AFTER))
    private void post_ScheduledTasks(CallbackInfo callbackInfo)
    {
        GameLoopEvent event = new GameLoopEvent();
        Infinity.INSTANCE.eventBus.post(event);
    }
    @Override
    @Accessor(value = "timer")
    public abstract Timer getTimer();
    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;" + "startSection(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void runTickHook(CallbackInfo info)
    {
        TickEvent event = new TickEvent();
        Infinity.INSTANCE.eventBus.post(event);
    }
}

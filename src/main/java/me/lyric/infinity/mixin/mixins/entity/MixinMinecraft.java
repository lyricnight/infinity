package me.lyric.infinity.mixin.mixins.entity;

import event.bus.EventBus;
import me.lyric.infinity.api.event.events.network.GameLoopEvent;
import me.lyric.infinity.mixin.transformer.IMinecraft;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {
    @Inject(
            method = "runGameLoop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/profiler/Profiler;endSection()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private void post_ScheduledTasks(CallbackInfo callbackInfo)
    {
        GameLoopEvent event = new GameLoopEvent();
        EventBus.post(event);
    }



}
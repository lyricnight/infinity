package me.lyric.infinity.mixin.mixins.entity;

import event.bus.EventBus;
import me.lyric.infinity.api.event.events.player.TurnEvent;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = {Entity.class}, priority = Integer.MAX_VALUE)
public abstract class MixinEntity {

    @Shadow
    public abstract boolean equals(Object paramObject);

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void onTurnHook(float yaw, float pitch, CallbackInfo info) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        EventBus.post(event);

        if (event.getCancelled()) {
            info.cancel();
        }
    }

}
package me.lyric.infinity.mixin.mixins.entity;

import event.bus.EventBus;
import me.lyric.infinity.api.event.events.player.TurnEvent;
import me.lyric.infinity.api.util.minecraft.IGlobals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = {Entity.class}, priority = Integer.MAX_VALUE)
public abstract class MixinEntity {

    @Shadow
    private int entityId;

    @Shadow
    protected boolean isInWeb;

    @Shadow
    public void move(MoverType type, double x, double y, double z) {}

    @Shadow
    public double motionX;

    @Shadow
    public double motionY;

    @Shadow
    public double motionZ;

    @Shadow
    public abstract boolean equals(Object paramObject);

    @Shadow public abstract int getEntityId();
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void onTurnHook(float yaw, float pitch, CallbackInfo info) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        EventBus.post(event);

        if (event.getCancelled()) {
            info.cancel();
        }
    }

}
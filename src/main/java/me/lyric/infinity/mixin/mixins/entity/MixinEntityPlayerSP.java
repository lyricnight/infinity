package me.lyric.infinity.mixin.mixins.entity;

import com.mojang.authlib.GameProfile;
import event.bus.EventBus;
import event.bus.EventState;
import me.lyric.infinity.api.event.events.entity.LivingUpdateEvent;
import me.lyric.infinity.api.event.events.player.MotionEvent;
import me.lyric.infinity.api.event.events.player.MoveEvent;
import me.lyric.infinity.api.event.events.player.UpdateWalkingPlayerEvent;
import me.lyric.infinity.impl.modules.render.Swing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow protected abstract void onUpdateWalkingPlayer();
    private static Minecraft mc = Minecraft.getMinecraft();
    private UpdateWalkingPlayerEvent eventUpdateWalkingPlayer;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo info) {
        MotionEvent motionEvent = new MotionEvent(type, x, y, z);
        EventBus.post(motionEvent);
        if (motionEvent.getCancelled()) {
            super.move(type, motionEvent.getX(), motionEvent.getY(), motionEvent.getZ());
            info.cancel();
        }
    }
    @Override
    public void move(MoverType type, double x, double y, double z) {
        MoveEvent event = new MoveEvent(x, y, z);
        EventBus.post(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
        LivingUpdateEvent livingUpdateEvent = new LivingUpdateEvent(entityPlayerSP, sprinting);
        EventBus.post(livingUpdateEvent);

        if (livingUpdateEvent.getCancelled())
            livingUpdateEvent.getEntityPlayerSP().setSprinting(true);
        else
            entityPlayerSP.setSprinting(sprinting);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void onUpdateWalkingPlayerPre(CallbackInfo callbackInfo) {
        UpdateWalkingPlayerEvent updateWalkingPlayerEvent = new UpdateWalkingPlayerEvent(EventState.PRE, Minecraft.getMinecraft().player.rotationYaw, Minecraft.getMinecraft().player.rotationPitch);
        EventBus.post(updateWalkingPlayerEvent);
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationYaw:F"))
    private float onUpdateWalkingPlayerRotationYaw(EntityPlayerSP player) {
        return eventUpdateWalkingPlayer.getYaw();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationPitch:F"))
    private float onUpdateWalkingPlayerRotationPitch(EntityPlayerSP player) {
        return eventUpdateWalkingPlayer.getPitch();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerPost(CallbackInfo callbackInfo) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(EventState.POST);
        EventBus.post(event);
    }
    @Inject(method = { "swingArm" }, at = { @At("HEAD") }, cancellable = true)
    public void swingArm(EnumHand enumHand, CallbackInfo info) {
        if (Swing.INSTANCE.isEnabled()) {
            if (Swing.INSTANCE.swing.getValue() == Swing.SwingHand.MAINHAND)
            {
                super.swingArm(EnumHand.MAIN_HAND);
            }
            if (Swing.INSTANCE.swing.getValue() == Swing.SwingHand.OFFHAND)
            {
                super.swingArm(EnumHand.OFF_HAND);
            }
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketAnimation(enumHand));
            info.cancel();
        }
    }


}
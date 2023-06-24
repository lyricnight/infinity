package me.lyric.infinity.mixin.mixins.entity;

import com.mojang.authlib.GameProfile;
import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.entity.LivingUpdateEvent;
import me.lyric.infinity.api.event.player.MoveEvent;
import me.lyric.infinity.api.event.player.UpdateWalkingPlayerEventPost;
import me.lyric.infinity.api.event.player.UpdateWalkingPlayerEventPre;
import me.lyric.infinity.impl.modules.render.Swing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static me.lyric.infinity.api.util.minecraft.IGlobals.mc;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }
    @Override
    public void move(MoverType type, double x, double y, double z) {
        MoveEvent event = new MoveEvent(x, y, z);
        Infinity.INSTANCE.eventBus.post(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
        LivingUpdateEvent livingUpdateEvent = new LivingUpdateEvent(entityPlayerSP, sprinting);
        Infinity.INSTANCE.eventBus.post(livingUpdateEvent);

        if (livingUpdateEvent.isCancelled())
            livingUpdateEvent.getEntityPlayerSP().setSprinting(true);
        else
            entityPlayerSP.setSprinting(sprinting);
    }
    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "HEAD")})
    private void preMotion(CallbackInfo info) {
        UpdateWalkingPlayerEventPre event = new UpdateWalkingPlayerEventPre();
        Infinity.INSTANCE.eventBus.post(event);
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "RETURN")})
    private void postMotion(CallbackInfo info) {
        UpdateWalkingPlayerEventPost event = new UpdateWalkingPlayerEventPost();
        Infinity.INSTANCE.eventBus.post(event);
    }
    @Inject(method = "swingArm" , at =  @At("HEAD") , cancellable = true)
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
            if(Swing.INSTANCE.swing.getValue() == Swing.SwingHand.NONE)
            {
                info.cancel();
            }
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketAnimation(enumHand));
            info.cancel();
        }
    }


}
package me.lyric.infinity.mixin.mixins.entity;


import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.entity.AttackEventPost;
import me.lyric.infinity.api.event.entity.AttackEventPre;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerControllerMP.class, priority = Integer.MAX_VALUE)
public abstract class MixinPlayerControllerMP {
    @Inject(method = {"onPlayerDamageBlock"}, at = {@At("HEAD")})
    private void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
    }
    @Inject(method = "attackEntity", at = @At(value = "HEAD"), cancellable = true)
    public void attackEntityPre(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        AttackEventPre event = new AttackEventPre(targetEntity);
        Infinity.INSTANCE.eventBus.post(event);
        if (event.isCancelled())
            ci.cancel();

    }

    @Inject(method = "attackEntity", at = @At(value = "RETURN"), cancellable = true)
    public void attackEntityPost(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        AttackEventPost event = new AttackEventPost(targetEntity);
        Infinity.INSTANCE.eventBus.post(event);
        if (event.isCancelled())
            ci.cancel();
    }

}
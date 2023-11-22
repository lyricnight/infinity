package me.lyric.infinity.mixin.mixins.network;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.impl.modules.player.Announcer;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IPlayerControllerMP, IGlobals {
    @Override
    @Accessor(value = "blockHitDelay")
    public abstract void setBlockHitDelay(int delay);

    @Override
    @Invoker("syncCurrentPlayItem")
    public abstract void syncItem();

    @Inject(method =  "onPlayerDestroyBlock" , at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Managers.MODULES.getModuleByClass(Announcer.class).isEnabled()) {
            Managers.MODULES.getModuleByClass(Announcer.class).onBreakBlock(pos);
        }
    }
}

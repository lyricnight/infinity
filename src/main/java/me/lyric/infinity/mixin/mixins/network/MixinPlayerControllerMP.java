package me.lyric.infinity.mixin.mixins.network;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.impl.modules.player.Announcer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Inject(method = { "onPlayerDestroyBlock" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V") }, cancellable = true)
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).isEnabled()) {
            Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).onBreakBlock(pos);
        }
    }


}

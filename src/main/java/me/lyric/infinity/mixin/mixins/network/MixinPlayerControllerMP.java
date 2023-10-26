package me.lyric.infinity.mixin.mixins.network;

import me.lyric.infinity.Infinity;
import me.lyric.infinity.api.event.misc.RightClickItemEvent;
import me.lyric.infinity.impl.modules.player.Announcer;
import me.lyric.infinity.mixin.transformer.IPlayerControllerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IPlayerControllerMP {
    @Override
    @Accessor(value = "blockHitDelay")
    public abstract void setBlockHitDelay(int delay);

    @Override
    @Invoker("syncCurrentPlayItem")
    public abstract void syncItem();

    @Inject(method = { "onPlayerDestroyBlock" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V") }, cancellable = true)
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).isEnabled()) {
            Infinity.INSTANCE.moduleManager.getModuleByClass(Announcer.class).onBreakBlock(pos);
        }
    }

    @Inject(method = "processRightClick", at = @At("HEAD"), cancellable = true)
    public void processClickHook(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> info) {
        RightClickItemEvent event = new RightClickItemEvent(player, worldIn, hand);

        Infinity.INSTANCE.eventBus.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }


}

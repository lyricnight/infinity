package me.lyric.infinity.mixin.mixins.blocks;

import me.lyric.infinity.api.event.events.blocks.CanCollideCheckEvent;
import event.bus.EventBus;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author lyric
 */

@Mixin(value = BlockLiquid.class)
public class MixinBlockLiquid {

    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    public void canCollideCheck(final IBlockState blockState, final boolean b, final CallbackInfoReturnable<Boolean> ci) {
        CanCollideCheckEvent event = new CanCollideCheckEvent();
        EventBus.post(event);
        ci.setReturnValue(event.getCancelled());
    }
}
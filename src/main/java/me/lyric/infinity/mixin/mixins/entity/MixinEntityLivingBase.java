package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.impl.modules.render.Swing;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityLivingBase.class})
public class MixinEntityLivingBase {

    @Inject(method={"getArmSwingAnimationEnd"}, at={@At(value="HEAD")}, cancellable=true)
    private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> info) {

        Swing mod = Swing.INSTANCE;

        if (mod.isEnabled() && mod.slowSwing.getValue()) {
            info.setReturnValue(15);

        }
    }
}

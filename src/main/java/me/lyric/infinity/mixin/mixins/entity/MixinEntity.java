package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.api.util.minecraft.IGlobals;
import me.lyric.infinity.impl.modules.render.NoRender;
import me.lyric.infinity.manager.Managers;
import me.lyric.infinity.mixin.mixins.accessors.IEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Entity.class})
public abstract class MixinEntity implements IEntity, IGlobals {

    @Inject(method = "isSneaking", at = @At(value = "RETURN"), cancellable = true)
    private void isSneaking(CallbackInfoReturnable<Boolean> cir)
    {
        if (mc.player != null && mc.world != null)
        {
            cir.setReturnValue(Managers.MODULES.getModuleByClass(NoRender.class).isEnabled() && Managers.MODULES.getModuleByClass(NoRender.class).sneak.getValue());
        }
    }
}
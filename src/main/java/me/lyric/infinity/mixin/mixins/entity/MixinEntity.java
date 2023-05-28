package me.lyric.infinity.mixin.mixins.entity;

import me.lyric.infinity.impl.modules.render.NoRender;
import me.lyric.infinity.mixin.transformer.IEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = {Entity.class})
public abstract class MixinEntity implements IEntity {

    Minecraft mc = Minecraft.getMinecraft();


    @Shadow
    private int entityId;

    @Shadow
    protected boolean isInWeb;

    @Shadow
    public void move(MoverType type, double x, double y, double z) {}

    @Shadow
    public double motionX;

    @Override
    @Accessor ("isInWeb")
    public abstract boolean isInWeb();
    @Shadow
    protected abstract boolean getFlag(int flag);

    @Shadow
    public double motionY;

    @Shadow
    public double motionZ;
    @Shadow
    public abstract boolean equals(Object paramObject);

    @Shadow public abstract int getEntityId();
    @Inject(method = "isSneaking", at = @At(value = "RETURN"), cancellable = true)
    private void isSneaking(CallbackInfoReturnable<Boolean> cir)
    {
        if (mc.player != null && mc.world != null)
        {
            cir.setReturnValue(NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.sneak.getValue() || this.getFlag(1));
        }
    }


}
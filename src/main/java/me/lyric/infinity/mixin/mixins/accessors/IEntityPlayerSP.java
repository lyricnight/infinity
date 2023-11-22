package me.lyric.infinity.mixin.mixins.accessors;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( EntityPlayerSP.class )
public interface IEntityPlayerSP {
    @Accessor(value = "lastReportedPosY")
    void setLastReportedPosY(double lastReportedPosY);

    @Accessor(value = "lastReportedPitch")
    void setLastReportedPitch(float lastReportedPitch);

    @Accessor(value = "lastReportedPitch")
    float getLastReportedPitch();
}
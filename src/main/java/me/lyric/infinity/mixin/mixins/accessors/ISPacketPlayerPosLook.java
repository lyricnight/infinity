package me.lyric.infinity.mixin.mixins.accessors;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketPlayerPosLook.class)
public interface ISPacketPlayerPosLook {
    @Accessor(value = "yaw")
    void setYaw(float yaw);

    @Accessor(value = "pitch")
    void setPitch(float pitch);
}
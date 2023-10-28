package me.lyric.infinity.mixin.mixins.network;

import me.lyric.infinity.mixin.transformer.ICPacketUseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CPacketUseEntity.class)
public class MixinCPacketUseEntity implements ICPacketUseEntity {
    private Entity entity;
    @Override
    public Entity getAttackedEntity()
    {
        return entity;
    }

}

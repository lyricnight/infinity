package me.lyric.infinity.mixin.mixins.accessors;

import net.minecraft.network.play.client.CPacketChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketChatMessage.class)
public interface ICPacketChat
{
    @Accessor("message")
    void setMessage(final String p0);
}
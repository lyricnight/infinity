package me.lyric.infinity.mixin.mixins.accessors;

import org.spongepowered.asm.mixin.*;
import net.minecraft.network.play.client.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(CPacketChatMessage.class)
public interface ICPacketChat
{
    @Accessor("message")
    void setMessage(final String p0);
}
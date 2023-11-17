package me.lyric.infinity.impl.modules.misc;

import io.netty.buffer.Unpooled;
import me.bush.eventbus.annotation.EventListener;
import me.lyric.infinity.api.event.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import me.lyric.infinity.api.module.ModuleInformation;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * @author lyric
 * very useful
 */

@ModuleInformation(name = "NoHandshake", description = "hypixel players when", category = Category.Misc)
public class NoHandshake extends Module {
    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!nullSafe()) return;
        if (mc.isSingleplayer()) return;
        CPacketCustomPayload packet;
        if (event.getPacket() instanceof FMLProxyPacket && !mc.isSingleplayer()) {
            event.cancel();
        }
        if (event.getPacket() instanceof CPacketCustomPayload && (packet = (CPacketCustomPayload) event.getPacket()).getChannelName().equals("MC|Brand")) {
            packet.data = new PacketBuffer(Unpooled.buffer()).writeString("vanilla");
        }

    }
}

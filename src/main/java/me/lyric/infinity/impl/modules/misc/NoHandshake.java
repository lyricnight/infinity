package me.lyric.infinity.impl.modules.misc;

import event.bus.EventListener;
import io.netty.buffer.Unpooled;
import me.lyric.infinity.api.event.events.network.PacketEvent;
import me.lyric.infinity.api.module.Category;
import me.lyric.infinity.api.module.Module;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * @author lyric
 */

public class NoHandshake extends Module {

    public NoHandshake() {
        super("NoHandshake", "Prevents sending your modlist to the server.", Category.MISC);
    }

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

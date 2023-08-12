package me.lyric.infinity.mixin.transformer;

import net.minecraft.network.Packet;

/**
 * @author lyric
 * @link {PacketDelay}
 */

public interface INetworkManager {
    Packet<?> sendPacketNoEvent(Packet<?> packetIn);
}
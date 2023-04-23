package me.lyric.infinity.mixin.mixins.network;

import me.lyric.infinity.api.event.events.network.PacketEvent;
import event.bus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author lyric
 */

@Mixin(value = NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void receive(ChannelHandlerContext context, Packet<?> packetIn, CallbackInfo callback) {
        final PacketEvent.Receive event = new PacketEvent.Receive(packetIn);
        EventBus.post(event);
        if (event.getCancelled()) {
            callback.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callback) {
        final PacketEvent.Send event = new PacketEvent.Send(packet);
        EventBus.post(event);
        if (event.getCancelled()) {
            callback.cancel();
        }
    }
}

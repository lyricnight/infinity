package me.lyric.infinity.api.event.events.network;

import me.bush.eventbus.event.Event;
import net.minecraft.network.Packet;


public class PacketEvent extends Event {

    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        super();

        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
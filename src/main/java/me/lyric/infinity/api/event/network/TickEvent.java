package me.lyric.infinity.api.event.network;

import me.bush.eventbus.event.Event;

public class TickEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }

}

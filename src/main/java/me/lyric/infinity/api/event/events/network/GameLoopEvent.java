package me.lyric.infinity.api.event.events.network;

import me.bush.eventbus.event.Event;

public class GameLoopEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}

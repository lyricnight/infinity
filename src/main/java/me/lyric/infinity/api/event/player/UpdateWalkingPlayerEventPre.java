package me.lyric.infinity.api.event.player;

import me.bush.eventbus.event.Event;

public class UpdateWalkingPlayerEventPre extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}

package me.lyric.infinity.api.event.events.blocks;

import me.bush.eventbus.event.Event;

/**
 * @author lyric
 * uhh
 */

public class CanCollideCheckEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}

